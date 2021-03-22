import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";
import Card from "react-bootstrap/Card";


class IssuesViewer extends Component {
  constructor (props) {
    super(props);
  }


  render () {
    return (
      <div className={styles.issuesContainer}>
        <Card className={styles.panelCard}>
          <Card.Body>
            <div className={styles.gradingCardTitle}>
              <h4>Issues</h4>
            </div>
          </Card.Body>
        </Card>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {

  };
};

const actionCreators = {

}

export default connect(mapStateToProps, actionCreators)(IssuesViewer)
