import React, { Component } from 'react'

// import styles from '../grading.module.css'
import styles from '../../rubric/rubric.module.css'
import gradingStyles from '../grading.module.css'
import {connect} from "react-redux";
import {addBlock, addCriterion, deleteElement, setCurrentPath, setSelectedElement} from "../../../redux/rubric/actions";
import {isBlock, isCriterion} from "../../rubric/helpers";
import classnames from "classnames";
import globalStyles from "../../helpers/global.module.css";
import {getGrade, isGraded} from "./helpers";
import ChevronRightIcon from "@material-ui/icons/ChevronRight";
import GradeIcon from "@material-ui/icons/Grade";
import CheckIcon from '@material-ui/icons/Check';
import withTheme from "@material-ui/core/styles/withTheme";


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
              <ChevronRightIcon
                className={classnames(styles.outlineElementIcon,
                  !this.props.collapsed && styles.outlineElementIconRotated)}
                onClick={this.props.onClickBlockCollapse}
              />
              :
              (activeGrade != null ?
                <CheckIcon
                  className={classnames(styles.outlineElementIcon)}
                  style={{color: this.props.theme.palette.success.main}}
                />
                :
                <GradeIcon
                  className={classnames(styles.outlineElementIcon)}
                  style={{color: this.props.theme.palette.secondary.main}}
                />
              )
            }

            <div>
              {this.props.data.content.title}
            </div>
          </div>

          {isCriterion(this.props.data.content.type) && activeGrade != null &&
          <div
            className={classnames(gradingStyles.outlineElementGrade)}
            style={{color: this.props.theme.palette.primary.main}}
          >
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

export default connect(mapStateToProps, actionCreators)(withTheme(RubricOutlineElement));