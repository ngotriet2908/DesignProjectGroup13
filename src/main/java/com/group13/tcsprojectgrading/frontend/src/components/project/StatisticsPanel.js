import React, { Component } from 'react'
import styles from './project.module.css'
import classnames from "classnames";
import globalStyles from "../helpers/global.module.css";
import withTheme from "@material-ui/core/styles/withTheme";
import CardContent from "@material-ui/core/CardContent";
import Card from "@material-ui/core/Card";
import Chart from "chart.js";


class StatisticsPanel extends Component {
  constructor (props) {
    super(props)
    this.chartRef = React.createRef();
  }

  calculateX (total) {
    //  provide min max from rurbic
    let minX = 0;
    let maxX = 10;
    let x = []
    let i = 0
    for(;i < total; i++) {
      x.push(minX + i*((maxX - minX)/total))
    }
    return x;
  }

  appendX(x) {
    let x_new = []
    x.forEach(x => {
      x_new.push("<=" + x.toFixed(1))
    })
    return x_new
  }

  calculateY(x, data, total) {
    let y = []
    let i = 0
    for(;i < total; i++) {
      let count = 0;
      data.forEach(
        a => {
          if (i === 0 && a <= x[i]) {
            count++
          } else if (a > x[i-1] && a <= x[i]){
            count++
          }
        }
      )
      y.push(count)
    }
    return y
  }

  componentDidMount() {
    const myChartRef = this.chartRef.current.getContext("2d");
    let total = 20
    let x = this.calculateX(total)
    let x_label = this.appendX(x)
    let stats = this.props.data
    let y = this.calculateY(x, stats, total)
    console.log(x_label)
    console.log(y)
    new Chart(myChartRef, {
      type: "bar",
      data: {
        //Bring in data
        labels: x_label,
        datasets: [
          {
            label: "number of students",
            data: y,
            backgroundColor: "#878BB6"
          }
        ]
      },
      options: {
        title: {
          display: true,
          text: 'Score distribution'
        }
      }
    });
  }

  render () {
    return(
      <div className={classnames(globalStyles.sectionContainer)}>
        <div className={classnames(globalStyles.sectionTitle, globalStyles.sectionTitleWithButton)}>
          <h3 className={globalStyles.sectionTitleH}>
            Statistics
          </h3>
        </div>

        <div className={globalStyles.sectionFlexContainer}>
          <Card className={classnames(styles.card, globalStyles.cardShadow)}>
            <CardContent
              className={classnames(styles.cardBody, styles.administrationSectionContainerBody)}
            >
              <canvas
                id="myChart"
                ref={this.chartRef}
              />



            </CardContent>
          </Card>
        </div>
      </div>
    )
  }
}

export default withTheme(StatisticsPanel);
