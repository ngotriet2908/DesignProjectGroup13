import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import {
  addBlock,
  addCriterion,
  setSelectedElement
} from "../../redux/rubric/actions";

import {findBlocks, findCriteria, isBlock, isCriterion} from "./helpers";

class RubricViewerElementChildren extends Component {
  constructor (props) {
    super(props);
  }

  render () {
    return (
      <div>
        <div className={styles.viewerSectionContainer}>
          <div className={styles.viewerSectionTitle}>
            <h4>Subsections</h4>
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
    // elementOpened: state.rubric.elementOpened
  };
};

const actionCreators = {
  setSelectedElement,
}

export default connect(mapStateToProps, actionCreators)(RubricViewerElementChildren)