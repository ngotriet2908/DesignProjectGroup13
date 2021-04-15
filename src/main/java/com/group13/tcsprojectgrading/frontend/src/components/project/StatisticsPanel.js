import React, { Component } from 'react'
import styles from './project.module.css'
import classnames from "classnames";
import globalStyles from "../helpers/global.module.css";
import withTheme from "@material-ui/core/styles/withTheme";
import CardContent from "@material-ui/core/CardContent";
import Card from "@material-ui/core/Card";


class StatisticsPanel extends Component {
  constructor (props) {
    super(props)
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

              {/* put your stats here */}



            </CardContent>
          </Card>
        </div>
      </div>
    )
  }
}

export default withTheme(StatisticsPanel);
