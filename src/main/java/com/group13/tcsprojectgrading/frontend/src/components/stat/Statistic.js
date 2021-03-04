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
      return (
        <Card>
          <Card.Body>
            <Card.Title className={styles.statTitle}>{this.state.title}</Card.Title>
            <Pie data={generatePieData(this.state.data)}/>
          </Card.Body>
        </Card>
      );
    default:
      console.error("Invalid statistic type.");
    }
  }
}

function generatePieData(data) {
  return (
    {
      datasets: [{
        data: data.map(entry => entry.count)
      }],
      labels: data.map(entry => entry.label),
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