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
        <Card className={styles.stat}>
          <Card.Body>
            <Card.Title className={styles.statTitle}>{this.state.title}</Card.Title>
            <Card.Text className={styles.statNumber}>{this.state.data}</Card.Text>
            <Card.Text className={styles.statUnit}>{this.state.unit}</Card.Text>
          </Card.Body>
        </Card>
      );
    case 'piechart':
      console.log(generatePieData(this.state.data));
      return (
        <Card className={styles.stat}>
          <Card.Body>
            <Card.Title className={styles.statTitle}>{this.state.title}</Card.Title>
            <Pie data={generatePieData(this.state.data)} options={{ legend: { display: false } }} style = {styles.piechart}/>
            <p className={styles.statText}> Total: {Object.values(this.state.data).reduce(
              (acc,cur) => acc + cur)}</p>
          </Card.Body>
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