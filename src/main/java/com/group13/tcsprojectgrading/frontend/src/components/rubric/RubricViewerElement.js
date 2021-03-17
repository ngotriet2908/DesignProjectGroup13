import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import {isBlock, isCriterion} from "./helpers";
import Button from "react-bootstrap/Button";
import {saveRubric, saveRubricTemp, setEditingRubric} from "../../redux/rubric/actions";
import RubricViewerElementGrade from "./RubricViewerElementGrade";
import RubricViewerElementChildren from "./RubricViewerElementChildren";
import {Can, ability, updateAbility} from "../permissions/ProjectAbility";
import {IoPencil, IoCloudDownloadOutline} from "react-icons/io5";


class RubricViewerElement extends Component {
  constructor (props) {
    super(props);
  }

  onClickEdit = () => {
    // get rubric backup
    let rubricBackup = this.props.rubric;
    this.props.saveRubricTemp(rubricBackup);
    this.props.setEditingRubric(true);
  }

  downloadRubric = () => {
    alert("Handle download.")
  }

  render () {
    // rubric's header
    if (!this.props.data.hasOwnProperty("content")) {
      return(
        <div>
          <div className={styles.viewerHeader}>
            <h2>Rubric</h2>
            <Can I="write" a="Rubric">
              <div className={styles.viewerHeaderIcon}>
                <IoPencil size={28} className={styles.viewerHeaderIconGreen} onClick={this.onClickEdit}/>
              </div>
            </Can>
            <div className={styles.viewerHeaderIcon}>
              <IoCloudDownloadOutline className={styles.viewerHeaderIconOrange} size={28} onClick={this.downloadRubric}/>
            </div>
            {/*<Button variant="secondary" onClick={this.downloadRubric}>Download</Button>*/}
          </div>
          <RubricViewerElementChildren data={this.props.data}/>
        </div>
      )
    }

    // rubric's element
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

        {isBlock(this.props.data.content.type) &&
        <RubricViewerElementChildren data={this.props.data}/>
        }
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    // isEditing: state.rubric.isEditing,
    rubric: state.rubric.rubric,
    // selectedElement: state.rubric.selectedElement
  };
};

const actionCreators = {
  setEditingRubric,
  saveRubric,
  saveRubricTemp
}

export default connect(mapStateToProps, actionCreators)(RubricViewerElement)


















