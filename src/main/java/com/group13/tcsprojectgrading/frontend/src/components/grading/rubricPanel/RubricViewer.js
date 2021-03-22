import React, { Component } from 'react'
import styles from '../grading.module.css'
import Card from "react-bootstrap/Card";
import {saveRubric, setSelectedElement} from "../../../redux/rubric/actions";
import {connect} from "react-redux";

import RubricViewerOutline from "./RubricViewerOutline";
import RubricViewerElement from "./RubricViewerElement";


class RubricViewer extends Component {
  constructor (props) {
    super(props);

    this.state = {
      isOutlineHidden: false,
    }
  }

  isOutlineHiddenChangeCallback = (isOutlineHidden) => {
    this.setState({
      isOutlineHidden: isOutlineHidden
    })
  }

  render () {
    return (
      <div className={styles.rubricViewerContainer}>
        <Card className={styles.rubricViewerCard}>
          {/*<Card.Body>*/}
          <div className={styles.rubricViewerCardInner}>
            <RubricViewerOutline isOutlineHidden={this.state.isOutlineHidden} hiddenCallback={this.isOutlineHiddenChangeCallback}/>
            <RubricViewerElement isOutlineHidden={this.state.isOutlineHidden} hiddenCallback={this.isOutlineHiddenChangeCallback}/>
          </div>
          {/*</Card.Body>*/}
        </Card>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
  };
};

const actionCreators = {
  setSelectedElement,
  saveRubric
}

export default connect(mapStateToProps, actionCreators)(RubricViewer)
