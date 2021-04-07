import React, { Component } from 'react'
import styles from '../grading.module.css'
import rubricStyles from '../../rubric/rubric.module.css'
import {saveRubric, setSelectedElement} from "../../../redux/rubric/actions";
import {connect} from "react-redux";
import RubricViewerElementChildren from "../../rubric/RubricViewerElementChildren";
import {findById} from "../../../redux/rubric/functions";
import {isBlock, isCriterion} from "../../rubric/helpers";
import RubricViewerElementGrade from "../../rubric/RubricViewerElementGrade";
import classnames from 'classnames';
import globalStyles from "../../helpers/global.module.css";
import {IoArrowBackOutline, IoCaretBack, IoCaretForward} from "react-icons/io5";


class RubricViewer extends Component {
  constructor (props) {
    super(props);
  }

  onClickBack = () => {
    this.props.toggleOutlineHidden();
  }
  //
  // componentDidMount() {
  //   // console.log("mounted: " + this.props.selectedElement)
  //   console.log(this.props.element.content.id)
  // }

  render () {
    if (!this.props.element.hasOwnProperty("content")) {
      return <div className={classnames(styles.rubricViewerContainer)}/>;
    }

    return (
      <div className={classnames(styles.rubricViewerContainer, this.props.outlineHidden && styles.rubricViewerContainerVisible)}>
        <div className={styles.rubricViewerHeader}>
          {/*<span className={styles.rubricViewerElementBackButton} onClick={this.onClickBack}><FaArrowLeft/> Back</span>*/}

          <div className={classnames(globalStyles.iconButtonSmall, styles.gradingCardTitleButton, styles.issuesCardExpandButton)}
            onClick={this.onClickBack}>
            <IoArrowBackOutline size={26}/>
          </div>

          {isCriterion(this.props.element.content.type) ?
            <h3>Criterion</h3>
            :
            <h3>Section</h3>
          }
          {isCriterion(this.props.element.content.type) ?
            <div className={styles.buttonGroup}>
              <div className={classnames(globalStyles.iconButton)} onClick={() => this.props.handlePrevCriterion(this.props.element.content.id)}>
                <IoCaretBack size={26}/>
              </div>
              <div className={classnames(globalStyles.iconButton)} onClick={() => this.props.handleNextCriterion(this.props.element.content.id)}>
                <IoCaretForward size={26}/>
              </div>
            </div>
            :null
          }
        </div>

        <div className={styles.rubricViewerSectionContainer}>
          <div className={styles.rubricViewerSectionTitle}>
            <h4>Title</h4>
          </div>
          {this.props.element.content.title}
        </div>

        {isCriterion(this.props.element.content.type) &&
        <div className={styles.rubricViewerSectionContainer}>
          <div className={styles.rubricViewerSectionTitle}>
            <h4>Text</h4>
          </div>
          <div dangerouslySetInnerHTML={{__html: this.props.element.content.text}}/>
        </div>
        }

        {isCriterion(this.props.element.content.type) && this.props.element.content.grade &&
        <RubricViewerElementGrade id={this.props.element.content.id} data={this.props.element.content.grade}/>
        }
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    // rubric: state.rubric.rubric,
    selectedElement: state.rubric.selectedElement,
    element: findById(state.rubric.rubric, state.rubric.selectedElement)
  };
};

const actionCreators = {
  setSelectedElement,
  saveRubric
}

export default connect(mapStateToProps, actionCreators)(RubricViewer)
