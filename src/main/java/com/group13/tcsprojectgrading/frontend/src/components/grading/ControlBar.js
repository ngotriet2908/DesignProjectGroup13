import React, { Component } from 'react'
import styles from './grading.module.css'
import FileViewer from "./FileViewer";
import Card from "react-bootstrap/Card";
import {addBlock, addCriterion, setSelectedElement} from "../../redux/rubric/actions";
import {connect} from "react-redux";


class ControlBar extends Component {
  constructor (props) {
    super(props)
  }

  render () {
    return (
      <div className={styles.controlBarContainer}>
        {/*<Card className={styles.controlBarCard}>*/}
        {/*  <Card.Body>*/}
        <div>
              Controls and Info, Download submission, Tag submission etc.
        </div>
        {/*</Card.Body>*/}
        {/*</Card>*/}
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

export default connect(mapStateToProps, actionCreators)(ControlBar)
