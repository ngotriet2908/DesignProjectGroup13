import React, { Component } from 'react'

import styles from '../grading.module.css'
import {connect} from "react-redux";
import RubricOutlineGroup from "./RubricOutlineGroup";
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
import globalStyles from "../../helpers/global.module.css";
import classnames from "classnames";


class RubricOutline extends Component {
  constructor (props) {
    super(props);

    this.padding = 0;
    this.path = "";

    this.data = {
      content: {
        id: this.props.rubric.id,
      },
      children: this.props.rubric.children
    }
  }

  onClickElement = (id, path) => {
    this.props.setSelectedElement(id);
    this.props.setCurrentPath(path);
  }

  render () {
    return (
      <div className={styles.outlineContainer}>
        <div className={classnames(styles.outlineHeaderContainer,
          (this.props.selectedElement != null && this.props.rubric.id === this.props.selectedElement) &&
          styles.outlineHeaderContainerSelected
        )}
        onClick={() => this.onClickElement(this.props.rubric.id, "")}>
          <div className={classnames(styles.outlineHeader)}>
            <h3>Rubric</h3>
          </div>
        </div>

        {this.props.rubric != null ?
          <RubricOutlineGroup path={this.path + "/children"} onClickElement={this.onClickElement} padding={this.padding} data={this.props.rubric.children}/>
          :
          <div>
            Empty
          </div>
        }
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
    selectedElement: state.rubric.selectedElement,
    isEditing: state.rubric.isEditing,
    rubricTemp: state.rubric.rubricTemp,
    updates: state.rubric.updates
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

export default connect(mapStateToProps, actionCreators)(RubricOutline)