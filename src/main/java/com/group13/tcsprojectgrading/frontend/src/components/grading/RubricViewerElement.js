import React, { Component } from 'react'
import styles from './grading.module.css'
import rubricStyles from '../rubricNew/rubric.module.css'
import {saveRubric, setSelectedElement} from "../../redux/rubricNew/actions";
import {connect} from "react-redux";
import Button from "react-bootstrap/Button";
import RubricViewerElementChildren from "../rubricNew/RubricViewerElementChildren";
import {findById} from "../../redux/rubricNew/functions";
import {FaArrowLeft} from "react-icons/fa";
import {isBlock, isCriterion} from "../rubricNew/helpers";
import RubricViewerElementGrade from "../rubricNew/RubricViewerElementGrade";


class RubricViewerElement extends Component {
  constructor (props) {
    super(props);

    this.state = {
      hidden: false,
    }
  }

  onClickBack = () => {
    this.props.hiddenCallback(false);
  }

  render () {
    let element = findById(this.props.rubric, this.props.selectedElement);

    if (element == null) {
      console.error("Element not found.")
      return null;
    }

    // let hidden = this.props.isOutlineHidden ? "" : ` ${styles.rubricViewerOutlineHidden}`;
    let hidden = this.props.isOutlineHidden ? "" : ` ${styles.rubricViewerElementHidden}`;


    if (!element.hasOwnProperty("content")) {
      return(
        <div className={`${styles.rubricViewerElement}${hidden}`}>
          <div className={styles.viewerHeader}>
            <span className={styles.rubricViewerElementBackButton} onClick={this.onClickBack}><FaArrowLeft/> Back</span>
            <h2>Rubric</h2>
          </div>
          <RubricViewerElementChildren data={element}/>
        </div>
      )
    }

    return (
      <div className={`${styles.rubricViewerElement}${hidden}`}>
        <div className={styles.viewerHeader}>
          <span className={styles.rubricViewerElementBackButton} onClick={this.onClickBack}><FaArrowLeft/> Back</span>
          {isCriterion(element.content.type) ?
            <h2>Criterion</h2>
            :
            <h2>Section</h2>
          }
        </div>

        <div className={rubricStyles.viewerSectionContainer}>
          <div className={rubricStyles.viewerSectionTitle}>
            <h4>Title</h4>
          </div>
          {element.content.title}
        </div>

        {isCriterion(element.content.type) &&
        <div className={rubricStyles.viewerSectionContainer}>
          <div className={rubricStyles.viewerSectionTitle}>
            <h4>Text</h4>
          </div>
          <div dangerouslySetInnerHTML={{__html: element.content.text}}/>
        </div>
        }

        {isCriterion(element.content.type) && element.content.grade &&
        <RubricViewerElementGrade id={element.content.id} data={element.content.grade}/>
        }

        {isBlock(element.content.type) &&
        <RubricViewerElementChildren data={element}/>
        }
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubricNew.rubric,
    selectedElement: state.rubricNew.selectedElement
  };
};

const actionCreators = {
  setSelectedElement,
  saveRubric
}

export default connect(mapStateToProps, actionCreators)(RubricViewerElement)
