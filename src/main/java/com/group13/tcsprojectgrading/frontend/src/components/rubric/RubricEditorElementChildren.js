import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import {
  addBlock,
  addCriterion,
  setSelectedElement
} from "../../redux/rubric/actions";

import { findBlocks, findCriteria, isBlock, isCriterion} from "./helpers";
import {v4 as uuidv4} from "uuid";
import Button from "react-bootstrap/Button";
import {FaPlus} from "react-icons/fa";

class RubricEditorElementChildren extends Component {
  constructor (props) {
    super(props);
  }

  onClickNewBlock = () => {
    let newBlock = {
      content: {
        id: uuidv4(),
        type: "0",
        title: "Default section's title", //+ Math.floor(Math.random() * Math.floor(100)),
      },
      children: []
    }

    if (this.props.data.hasOwnProperty("content")) {
      this.props.addBlock(this.props.data.content.id, newBlock);
    } else {
      this.props.addBlock(this.props.data.id, newBlock);
    }

    this.props.setSelectedElement(newBlock.content.id);
  }

  onClickNewCriterion = () => {
    let newCriterion = {
      content: {
        id: uuidv4(),
        type: "1",
        title: "Default criterion's title", // + Math.floor(Math.random() * Math.floor(100)),
        text: "You can edit this text in the edit mode.",
        grade: {
          min: 0,
          max: 10,
          step: 1,
        }
      }
    }

    if (this.props.data.hasOwnProperty("content")) {
      this.props.addCriterion(this.props.data.content.id, newCriterion);
    } else {
      this.props.addCriterion(this.props.data.id, newCriterion);
    }

    this.props.setSelectedElement(newCriterion.content.id);
  }

  render () {
    return (
      <div>
        <div className={styles.viewerSectionContainer}>
          <div className={styles.viewerSectionTitle}>
            <h4>Subsections</h4>
            <Button className={styles.viewerSectionContainerButton} variant="primary" onClick={this.onClickNewBlock}><FaPlus/></Button>
          </div>
          {findBlocks(this.props.data.children) ?
            this.props.data.children.map((child => {
              if (isBlock(child.content.type)) {
                return (
                  <div key={child.content.id} onClick={() => {
                    this.props.setSelectedElement(child.content.id)
                  }}>
                    {child.content.title}
                  </div>
                )
              }
            }))
            :
            <div>
              No subsections in this section.
            </div>
          }
        </div>

        <div className={styles.viewerSectionContainer}>
          <div className={styles.viewerSectionTitle}>
            <h4>Criteria</h4>
            <Button className={styles.viewerSectionContainerButton} variant="primary" onClick={this.onClickNewCriterion}><FaPlus/></Button>
          </div>
          {findCriteria(this.props.data.children) ?
            this.props.data.children.map((child => {
              if (isCriterion(child.content.type)) {
                return (
                  <div key={child.content.id} onClick={() => {
                    this.props.setSelectedElement(child.content.id)
                  }}>
                    {child.content.title}
                  </div>
                )
              }
            }))
            :
            <div>
              No criteria in this section.
            </div>
          }
        </div>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    // elementOpened: state.rubric.elementOpened
  };
};

const actionCreators = {
  addBlock,
  addCriterion,
  setSelectedElement,
}

export default connect(mapStateToProps, actionCreators)(RubricEditorElementChildren)