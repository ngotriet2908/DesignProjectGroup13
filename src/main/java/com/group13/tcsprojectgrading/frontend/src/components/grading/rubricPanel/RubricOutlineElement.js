import React, { Component } from 'react'

import styles from '../grading.module.css'
import {connect} from "react-redux";
import {addBlock, addCriterion, deleteElement, setCurrentPath, setSelectedElement} from "../../../redux/rubric/actions";
import {FaChevronDown, FaChevronRight, FaHandPointRight} from "react-icons/fa";
import {IoCheckmarkOutline, IoEllipsisVerticalOutline} from "react-icons/io5";
import {createNewBlock, createNewCriterion, isBlock, isCriterion, removeElement} from "../../rubric/helpers";
import {LOCATIONS} from "../../../redux/navigation/reducers/navigation";
import classnames from "classnames";
import globalStyles from "../../helpers/global.module.css";
import Dropdown from "react-bootstrap/Dropdown";
import {CustomToggle} from "./RubricOutline";

class RubricOutlineElement extends Component {
  constructor (props) {
    super(props);

    this.state = {
      showMenu: false,
    }
  }

  onClick = () => {
    console.log(this.props.path);
    this.props.onClickElement(this.props.data.content.id, this.props.path);
  }

  render () {
    return (
      <div className={classnames(
        styles.outlineElementContainer,
        (this.props.selectedElement != null &&
        this.props.data.content.id === this.props.selectedElement) && styles.outlineElementContainerSelected
      )} onClick={this.onClick}>
        <div className={styles.outlineElement} style={{paddingLeft: `${this.props.padding}rem`}}>
          <div className={classnames(styles.outlineElementLeft, isBlock(this.props.data.content.type) && styles.outlineElementLeftBlock)}>
            {isBlock(this.props.data.content.type) ?
              <div className={classnames(styles.outlineElementIcon,
                !this.props.collapsed && styles.outlineElementIconRotated)}>
                <FaChevronRight onClick={this.props.onClickBlockCollapse}/>
              </div>
              :
              <div className={classnames(styles.outlineElementIcon)}>
                <FaHandPointRight/>
              </div>
            }
            <div>
              {this.props.data.content.title}
            </div>
          </div>
        </div>
      </div>
    )
  }
}


const mapStateToProps = state => {
  return {
    isEditing: state.rubric.isEditing,
    selectedElement: state.rubric.selectedElement,
    rubric: state.rubric.rubric,
  };
};

const actionCreators = {
  setSelectedElement,
  addBlock,
  addCriterion,
  setCurrentPath,
  deleteElement,
}

export default connect(mapStateToProps, actionCreators)(RubricOutlineElement)