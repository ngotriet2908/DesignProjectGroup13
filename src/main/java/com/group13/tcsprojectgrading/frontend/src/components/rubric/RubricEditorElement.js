import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import RubricEditorRichText from "./RubricEditorRichText";
import {
  addBlock,
  addCriterion,
  alterCriterionText,
  alterTitle, deleteAllElements, deleteElement, setCurrentPath,
  setSelectedElement
} from "../../redux/rubric/actions";

import PropTypes from 'prop-types';
import {isBlock, isCriterion} from "./helpers";
import RubricEditorElementChildren from "./RubricEditorElementChildren";
import RubricEditorElementGrade from "./RubricEditorElementGrade";
import {IoTrashBinOutline} from "react-icons/io5";
import debounce from "lodash/debounce"

class RubricEditorElement extends Component {
  constructor (props) {
    super(props);
  }

  onChangeTitle = (event) => {
    // debounce(this.props.alterTitle(this.props.data.content.id, event.target.value, path), 200);
    this.props.alterTitle(this.props.data.content.id, event.target.value, this.props.currentPath + "/content/title");
  }

  // onClickDelete = () => {
  //   this.props.deleteElement(this.props.data.content.id, this.props.currentPath);
  //   // TODO go to parent
  //   this.props.setCurrentPath("");
  //   this.props.setSelectedElement(this.props.rubric.id);
  // }

  // onClickDeleteAll = () => {
  //   this.props.deleteAllElements();
  //   this.props.setCurrentPath("");
  //   this.props.setSelectedElement(this.props.rubric.id);
  // }

  render () {
    // rubric's header
    if (!this.props.data.hasOwnProperty("content")) {
      return(
        <div>
          <div className={styles.viewerHeader}>
            <h2>Rubric</h2>
            {/*<div className={styles.viewerHeaderIcon}>*/}
            {/*  <IoTrashBinOutline size={28} className={styles.viewerHeaderIconRed} onClick={this.onClickDeleteAll}/>*/}
            {/*</div>*/}
          </div>
          {/*<RubricEditorElementChildren data={this.props.data}/>*/}
        </div>
      )
    }

    // rubric's elements
    return (
      <div>
        <div className={styles.viewerHeader}>
          {isCriterion(this.props.data.content.type) ?
            <h2>Criterion</h2>
            :
            <h2>Section</h2>
          }
          {/*<div className={styles.viewerHeaderIcon}>*/}
          {/*  <IoTrashBinOutline size={28} className={styles.viewerHeaderIconRed} onClick={this.onClickDelete}/>*/}
          {/*</div>*/}
        </div>

        <div className={styles.viewerSectionContainer}>
          <div className={styles.viewerSectionTitle}>
            <h4>Title</h4>
          </div>
          <input type="text" name="title" value={this.props.data.content.title} onChange={this.onChangeTitle}/>
        </div>

        {isCriterion(this.props.data.content.type) &&
        <div className={styles.viewerSectionContainer}>
          <div className={styles.viewerSectionTitle}>
            <h4>Text</h4>
          </div>
          <RubricEditorRichText
            elementId={this.props.data.content.id}
            key={this.props.data.content.id}
          />
        </div>
        }

        {isCriterion(this.props.data.content.type) && this.props.data.content.grade &&
        <RubricEditorElementGrade id={this.props.data.content.id} data={this.props.data.content.grade}/>
        }

        {/*{isBlock(this.props.data.content.type) &&*/}
        {/*<RubricEditorElementChildren data={this.props.data}/>*/}
        {/*}*/}
      </div>
    )
  }
}

RubricEditorElement.propTypes = {
  data: PropTypes.object,
  children: PropTypes.array,
  alterTitle: PropTypes.func,
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
    currentPath: state.rubric.currentPath
  };
};

const actionCreators = {
  alterTitle,
  alterCriterionText,
  addBlock,
  addCriterion,
  setSelectedElement,
  deleteElement,
  deleteAllElements,
  setCurrentPath
}

export default connect(mapStateToProps, actionCreators)(RubricEditorElement)


















