import React, { Component } from 'react';
import { Pie } from 'react-chartjs-2';
import Card from 'react-bootstrap/Card';
import styles from './stat.module.css';
import 'chartjs-plugin-colorschemes';

class Statistic extends Component {
  constructor(props) {
    super(props);
    this.state = {
      title: props.title,
      type: props.type,
      data: props.data,
      unit: props.unit,
    }
  }

  render() {
    switch (this.state.type) {
    case 'number':
      return (
        <Card>
          <Card.Body>
            <Card.Title className={styles.statTitle}>{this.state.title}</Card.Title>
            <Card.Text className={styles.statTextValue}>{this.state.data}</Card.Text>
            <Card.Text className={styles.statTextUnit}>{this.state.unit}</Card.Text>
          </Card.Body>
        </Card>
      );
    case 'piechart':
      console.log(generatePieData(this.state.data));
      return (
        <Card>
          <Card.Body>
            <Card.Title className={styles.statTitle}>{this.state.title}</Card.Title>
            <Pie data={generatePieData(this.state.data)}/>
          </Card.Body>
          <Card.Footer>Total: {Object.values(this.state.data).reduce((acc,cur) => acc + cur)}</Card.Footer>
        </Card>
      );
    default:
      console.error("Invalid statistic type.");
      console.log(this.props);
    }
  }
}

function generatePieData(data) {
  return (
    {
      datasets: [{
        data: Object.values(data)
      }],
      labels: Object.keys(data),
      options: {
        plugins: {
          colorschemes: {
            scheme: 'brewer.Blues3'
          }
        }
      }
    }
  );
}
export default Statistic;