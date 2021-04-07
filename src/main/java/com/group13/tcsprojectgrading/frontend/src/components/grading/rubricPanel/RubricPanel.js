import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";
import RubricOutline from "./RubricOutline";
import Card from "react-bootstrap/Card";
import RubricViewer from "./RubricViewer";
import {
  addBlock, addCriterion,
  deleteAllElements, deleteElement,
  resetUpdates,
  saveRubric,
  saveRubricTemp,
  setCurrentPath,
  setEditingRubric,
  setSelectedElement
} from "../../../redux/rubric/actions";


class RubricPanel extends Component {
  constructor (props) {
    super(props);

    this.state = {
      isOutlineHidden: false,
    }
  }

  toggleOutlineHidden = () => {
    this.setState(prevState => ({
      isOutlineHidden: !prevState.isOutlineHidden
    }))
  }

  handlePrevCriterion = (id) => {
    let nextId = -1
    console.log(id)
    this.props.rubricCriteria.forEach((criterion, i) => {
      if (criterion.id === id) {
        nextId = i - 1
      }
    })
    if (nextId >= 0) {
      console.log("next: " + this.props.rubricCriteria[nextId].id)
      this.props.setSelectedElement(this.props.rubricCriteria[nextId].id);
      // this.props.toggleOutlineHidden();
    }
  }

  handleNextCriterion = (id) => {
    let nextId = -1
    console.log(id)
    this.props.rubricCriteria.forEach((criterion, i) => {
      if (criterion.id === id) {
        nextId = i + 1
      }
    })
    if (nextId >= 0 && nextId < this.props.rubricCriteria.length) {
      console.log("next: " + this.props.rubricCriteria[nextId].id)
      this.props.setSelectedElement(this.props.rubricCriteria[nextId].id);
      // this.props.toggleOutlineHidden();
    }
  }

  render () {
    return (
      <div className={styles.rubricPanelContainer}>
        <Card className={styles.panelCard}>
          <Card.Body className={styles.gradeViewerBody}>
            <div className={styles.gradingCardTitle}>
              <h4>Rubric</h4>
            </div>
            <div className={styles.rubricPanelInnerContainer}>
              {/*{!this.state.isOutlineHidden ?*/}
              <RubricOutline rubricCriteria={this.props.rubricCriteria} outlineHidden={this.state.isOutlineHidden} toggleOutlineHidden={this.toggleOutlineHidden}/>
              {/*:*/}
              <RubricViewer rubricCriteria={this.props.rubricCriteria}
                            firstCritId={(this.props.rubricCriteria.length === 0)? "null" : this.props.rubricCriteria[0].id}
                            lastCritId={(this.props.rubricCriteria.length === 0)? "null" : this.props.rubricCriteria[this.props.rubricCriteria.length - 1].id}
                            handlePrevCriterion = {this.handlePrevCriterion}
                            handleNextCriterion = {this.handleNextCriterion}
                            outlineHidden={this.state.isOutlineHidden}
                            toggleOutlineHidden={this.toggleOutlineHidden}/>
              {/*}*/}
            </div>
          </Card.Body>
        </Card>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
    selectedElement: state.rubric.selectedElement,
  };
};

const actionCreators = {
  setSelectedElement,
  setCurrentPath,
  setEditingRubric,
  saveRubric,
  saveRubricTemp,
  resetUpdates,
  deleteAllElements,
  addBlock,
  addCriterion,
  deleteElement,
}

export default connect(mapStateToProps, actionCreators)(RubricPanel)
