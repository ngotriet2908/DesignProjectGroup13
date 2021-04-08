import React, { Component } from 'react'

import styles from '../grading.module.css'
import {connect} from "react-redux";
import {addBlock, addCriterion, deleteElement, setCurrentPath, setSelectedElement} from "../../../redux/rubric/actions";
import {isBlock, isCriterion} from "../../rubric/helpers";
import classnames from "classnames";
import globalStyles from "../../helpers/global.module.css";
import {IoChevronForwardOutline, IoCheckmarkSharp, IoPricetagOutline} from "react-icons/io5";
import {getGrade, isGraded} from "./helpers";

class RubricOutlineElement extends Component {
  constructor (props) {
    super(props);

    this.state = {
      showMenu: false,
    }
  }

  onClick = () => {
    this.props.onClickElement(this.props.data.content.id, this.props.path);
  }

  render () {
    // const graded = isGraded(this.props.grades, this.props.data.content.id);
    const activeGrade = getGrade(this.props.grades, this.props.data.content.id)

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
                <IoChevronForwardOutline size={22} onClick={this.props.onClickBlockCollapse}/>
              </div>
              :
              (activeGrade != null ?
                <div className={classnames(styles.outlineElementIcon, styles.outlineElementIconGraded)}>
                  <IoCheckmarkSharp size={22}/>
                </div>
                :
                <div className={classnames(styles.outlineElementIcon)}>
                  <IoPricetagOutline size={22}/>
                </div>
              )
            }

            <div>
              {this.props.data.content.title}
            </div>
          </div>

          {isCriterion(this.props.data.content.type) && activeGrade != null &&
          <div className={classnames(styles.outlineElementGrade)}>
            <div>
              {activeGrade.grade}
            </div>
          </div>
          }
        </div>
      </div>
    )
  }
}


const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
    selectedElement: state.rubric.selectedElement,
    grades: state.grading.assessment.grades
    // assessment: state.grading.assessment,
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