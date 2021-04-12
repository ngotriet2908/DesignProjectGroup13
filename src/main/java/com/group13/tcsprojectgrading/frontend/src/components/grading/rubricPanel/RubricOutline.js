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
import InboxIcon from '@material-ui/icons/Inbox';


class RubricOutline extends Component {
  constructor (props) {
    super(props);

    this.padding = -2;
    this.path = "";
  }

  componentDidMount = () => {
    this.props.setSelectedElement(this.props.rubric.id)
  }

  onClickElement = (id, path) => {
    console.log(id)
    this.props.setSelectedElement(id);
    this.props.toggleOutlineHidden();
  }

  render () {
    return (
      <div className={classnames(styles.outlineContainer, this.props.outlineHidden && styles.outlineContainerHidden)}>
        {this.props.rubric.children.length === 0 ?
          <div className={styles.gradeViewerNotGraded}>
            <InboxIcon fontSize={"large"}/>
            <h6>Rubric is empty</h6>
          </div>
          :
          <RubricOutlineGroup path={this.path + "/children"} onClickElement={this.onClickElement} padding={this.padding}
            data={this.props.rubric.children}/>
        }
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

export default connect(mapStateToProps, actionCreators)(RubricOutline)