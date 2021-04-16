import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import {isCriterion} from "./helpers";
import {saveRubric, saveRubricTemp, setEditingRubric} from "../../redux/rubric/actions";
import RubricViewerElementGrade from "./RubricViewerElementGrade";
import FormatAlignLeftIcon from '@material-ui/icons/FormatAlignLeft';


class RubricViewerElement extends Component {
  constructor (props) {
    super(props);
  }

  render () {
    // rubric's header
    if (!this.props.data.hasOwnProperty("content")) {
      return(
        <div className={styles.viewerBodyEmpty}>
          <FormatAlignLeftIcon/>
          <p>Choose a criterion</p>
        </div>
      )
    }

    // rubric's element
    return (
      <div className={styles.viewerContainer}>
        <div className={styles.viewerHeader}>
          {isCriterion(this.props.data.content.type) ?
            <h2>Criterion</h2>
            :
            <h2>Section</h2>
          }
        </div>

        <div className={styles.viewerSectionContainer}>
          <div className={styles.viewerSectionTitle}>
            <h4>Title</h4>
          </div>
          {this.props.data.content.title}
        </div>

        {isCriterion(this.props.data.content.type) &&
        <div className={styles.viewerSectionContainer}>
          <div className={styles.viewerSectionTitle}>
            <h4>Text</h4>
          </div>
          <div dangerouslySetInnerHTML={{__html: this.props.data.content.text}}/>
        </div>
        }

        {isCriterion(this.props.data.content.type) && this.props.data.content.grade &&
          <RubricViewerElementGrade id={this.props.data.content.id} data={this.props.data.content.grade}/>
        }
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
  };
};

const actionCreators = {
  setEditingRubric,
  saveRubric,
  saveRubricTemp
}

export default connect(mapStateToProps, actionCreators)(RubricViewerElement)


















