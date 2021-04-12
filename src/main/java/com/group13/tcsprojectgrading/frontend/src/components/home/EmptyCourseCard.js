import React, { Component } from 'react'

import classnames from "classnames";
import globalStyles from "../helpers/global.module.css";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import AddIcon from '@material-ui/icons/Add';
import withTheme from "@material-ui/core/styles/withTheme";


class EmptyCourseCard extends Component {
  constructor (props) {
    super(props)
  }

  render () {
    return (
      <Card
        className={classnames(globalStyles.emptyCard, this.props.className)}
        style={{backgroundColor: this.props.theme.palette.primary.light, ...this.props.style}}
        onClick={this.props.action}
      >
        <CardContent className={globalStyles.cardCenter}>
          <div>
            <div>
              <AddIcon/>
            </div>
            <div>
              {this.props.description}
            </div>
          </div>
        </CardContent>
      </Card>
    )
  }
}

export default withTheme(EmptyCourseCard);
