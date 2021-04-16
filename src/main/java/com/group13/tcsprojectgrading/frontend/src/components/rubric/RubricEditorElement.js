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
import classnames from 'classnames';

import PropTypes from 'prop-types';
import {isBlock, isCriterion} from "./helpers";
import RubricEditorElementGrade from "./RubricEditorElementGrade";
import debounce from "lodash/debounce"
import FormatAlignLeftIcon from "@material-ui/icons/FormatAlignLeft";
import TextField from "@material-ui/core/TextField";

class RubricEditorElement extends Component {
  constructor (props) {
    super(props);
  }

  onChangeTitle = (event) => {
    // debounce(this.props.alterTitle(this.props.data.content.id, event.target.value, path), 200);
    this.props.alterTitle(this.props.data.content.id, event.target.value, this.props.currentPath + "/content/title");
  }

  render () {
    console.log()

    // rubric's header
    if (!this.props.data || !this.props.data.hasOwnProperty("content")) {
      return(
        <div className={styles.viewerBodyEmpty}>
          <FormatAlignLeftIcon/>
          <p>Choose a criterion</p>
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
        </div>

        <div className={styles.viewerSectionContainer}>
          <TextField
            id="outlined-basic"
            label="Title"
            variant="outlined"
            value={this.props.data.content.title}
            onChange={this.onChangeTitle}
            className={styles.viewerSectionContainerField}
          />
        </div>

        {isCriterion(this.props.data.content.type) &&
        <div className={classnames(styles.viewerSectionContainer, styles.viewerSectionContainerQuill)}>
          {/*<div className={styles.viewerSectionTitle}>*/}
          {/*  <h4>Text</h4>*/}
          {/*</div>*/}

          <RubricEditorRichText
            elementId={this.props.data.content.id}
            key={this.props.data.content.id}
          />
        </div>
        }

        {isCriterion(this.props.data.content.type) && this.props.data.content.grade &&
        <RubricEditorElementGrade id={this.props.data.content.id} data={this.props.data.content.grade}/>
        }
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


















