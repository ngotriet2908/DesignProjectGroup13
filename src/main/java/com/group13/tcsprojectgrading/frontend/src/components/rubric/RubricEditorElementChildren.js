import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import {
  addBlock,
  addCriterion, setCurrentPath,
  setSelectedElement
} from "../../redux/rubric/actions";

import {v4 as uuidv4} from "uuid";
import {IoAdd} from "react-icons/io5";

class RubricEditorElementChildren extends Component {
  constructor (props) {
    super(props);
  }

  // onClickNewBlock = () => {
  //   // create new section
  //   let newBlock = {
  //     content: {
  //       id: uuidv4(),
  //       type: "0",
  //       title: "Default section's title",
  //     },
  //     children: []
  //   }
  //
  //   // check if the new section is not in the root section
  //   if (this.props.data.hasOwnProperty("content")) {
  //     this.props.addBlock(this.props.data.content.id, newBlock, this.props.currentPath + "/children/" + this.props.data.children.length);
  //   } else {
  //     this.props.addBlock(this.props.data.id, newBlock, this.props.currentPath + "/children/" + this.props.data.children.length);
  //   }
  //
  //   // open the new section
  //   this.props.setSelectedElement(newBlock.content.id);
  //   // set current path to the new section's path
  //   this.props.setCurrentPath(this.props.currentPath + "/children/" + this.props.data.children.length);
  // }
  //
  // onClickNewCriterion = () => {
  //   // create new criterion
  //   let newCriterion = {
  //     content: {
  //       id: uuidv4(),
  //       type: "1",
  //       title: "Default criterion's title",
  //       text: "You can edit this text in the edit mode.",
  //       grade: {
  //         min: 0,
  //         max: 10,
  //         step: 1,
  //         weight: 1.0
  //       },
  //     }
  //   }
  //
  //   // check if the new criterion is not in the root section
  //   if (this.props.data.hasOwnProperty("content")) {
  //     this.props.addCriterion(this.props.data.content.id, newCriterion, this.props.currentPath + "/children/" + this.props.data.children.length);
  //   } else {
  //     this.props.addCriterion(this.props.data.id, newCriterion, this.props.currentPath + "/children/" + this.props.data.children.length);
  //   }
  //
  //   // open the new criterion
  //   this.props.setSelectedElement(newCriterion.content.id);
  //   // set current path to the new section's path
  //   this.props.setCurrentPath(this.props.currentPath + "/children/" + this.props.data.children.length);
  // }

  render () {
    return (
      <div>
        <div className={styles.viewerSectionContainer}>
          <div className={styles.viewerSectionTitle}>
            <h4>Subsections</h4>
            <div className={styles.viewerSectionContainerIcon}>
              <IoAdd size={28} onClick={this.onClickNewBlock}/>
            </div>
          </div>
          {/*{findBlocks(this.props.data.children) ?*/}
          {/*  this.props.data.children.map((child => {*/}
          {/*    if (isBlock(child.content.type)) {*/}
          {/*      return (*/}
          {/*        <div key={child.content.id} onClick={() => {*/}
          {/*          this.props.setSelectedElement(child.content.id)*/}
          {/*        }}>*/}
          {/*          {child.content.title}*/}
          {/*        </div>*/}
          {/*      )*/}
          {/*    }*/}
          {/*  }))*/}
          {/*  :*/}
          {/*  <div>*/}
          {/*    No subsections in this section.*/}
          {/*  </div>*/}
          {/*}*/}
        </div>

        <div className={styles.viewerSectionContainer}>
          <div className={styles.viewerSectionTitle}>
            <h4>Criteria</h4>
            <div className={styles.viewerSectionContainerIcon}>
              <IoAdd size={28} onClick={this.onClickNewCriterion}/>
            </div>
          </div>
          {/*{findCriteria(this.props.data.children) ?*/}
          {/*  this.props.data.children.map((child => {*/}
          {/*    if (isCriterion(child.content.type)) {*/}
          {/*      return (*/}
          {/*        <div key={child.content.id} onClick={() => {*/}
          {/*          this.props.setSelectedElement(child.content.id)*/}
          {/*        }}>*/}
          {/*          {child.content.title}*/}
          {/*        </div>*/}
          {/*      )*/}
          {/*    }*/}
          {/*  }))*/}
          {/*  :*/}
          {/*  <div>*/}
          {/*    No criteria in this section.*/}
          {/*  </div>*/}
          {/*}*/}
        </div>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    currentPath: state.rubric.currentPath
  };
};

const actionCreators = {
  addBlock,
  addCriterion,
  setSelectedElement,
  setCurrentPath
}

export default connect(mapStateToProps, actionCreators)(RubricEditorElementChildren)