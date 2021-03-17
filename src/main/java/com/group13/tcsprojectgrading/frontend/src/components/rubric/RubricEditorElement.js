import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import RubricEditorRichText from "./RubricEditorRichText";
import {
  addBlock,
  addCriterion,
  alterCriterionText,
  alterTitle, deleteAllElements, deleteElement,
  setSelectedElement
} from "../../redux/rubric/actions";

import PropTypes from 'prop-types';
import {isBlock, isCriterion} from "./helpers";
import RubricEditorElementChildren from "./RubricEditorElementChildren";
import Button from "react-bootstrap/Button";
import RubricEditorElementGrade from "./RubricEditorElementGrade";
import RubricViewerElementChildren from "./RubricViewerElementChildren";
import RubricViewerElementGrade from "./RubricViewerElementGrade";
import {IoTrashBinOutline} from "react-icons/io5";

class RubricEditorElement extends Component {
  constructor (props) {
    super(props);
  }

  onChangeTitle = (event) => {
    this.props.alterTitle(this.props.data.content.id, event.target.value);
  }

  onClickDelete = () => {
    this.props.setSelectedElement(this.props.rubric.id);
    this.props.deleteElement(this.props.data.content.id);
  }

  onClickDeleteAll = () => {
    this.props.setSelectedElement(this.props.rubric.id);
    this.props.deleteAllElements();
  }

  render () {
    // rubric's header
    if (!this.props.data.hasOwnProperty("content")) {
      return(
        <div>
          <div className={styles.viewerHeader}>
            <h2>Rubric</h2>
            <div className={styles.viewerHeaderIcon}>
              <IoTrashBinOutline size={28} className={styles.viewerHeaderIconRed} onClick={this.onClickDeleteAll}/>
            </div>
            {/*<Button variant="danger" onClick={this.onClickDeleteAll}>Clear</Button>*/}
          </div>
          <RubricEditorElementChildren data={this.props.data}/>
        </div>
      )
    }

    // rubric's elements
    return (
      <div>
        {/* TODO rubric breadcrumbs */}

        <div className={styles.viewerHeader}>
          {isCriterion(this.props.data.content.type) ?
            <h2>Criterion</h2>
            :
            <h2>Section</h2>
          }
          <div className={styles.viewerHeaderIcon}>
            <IoTrashBinOutline size={28} className={styles.viewerHeaderIconRed} onClick={this.onClickDelete}/>
          </div>
          {/*<Button variant="danger" onClick={this.onClickDelete}>Delete</Button>*/}
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

        {isBlock(this.props.data.content.type) &&
        <RubricEditorElementChildren data={this.props.data}/>
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
    rubric: state.rubric.rubric
  };
};

const actionCreators = {
  alterTitle,
  alterCriterionText,
  addBlock,
  addCriterion,
  setSelectedElement,
  deleteElement,
  deleteAllElements
}

export default connect(mapStateToProps, actionCreators)(RubricEditorElement)


















