import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import {isBlock, isCriterion} from "./helpers";
import Button from "react-bootstrap/Button";
import {saveRubric, saveRubricTemp, setEditingRubric} from "../../redux/rubricNew/actions";
import RubricViewerElementGrade from "./RubricViewerElementGrade";
import RubricViewerElementChildren from "./RubricViewerElementChildren";
import {Can, ability, updateAbility} from "../permissions/ProjectAbility";


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

  render () {
    // rubric's header
    if (!this.props.data.hasOwnProperty("content")) {
      return(
        <div>
          <div className={styles.viewerHeader}>
            <h2>Rubric</h2>
            <Can I="write" a="Rubric">
              <Button variant="secondary" onClick={this.onClickEdit}>Edit</Button>
            </Can>
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
    // isEditing: state.rubricNew.isEditing,
    rubric: state.rubricNew.rubric,
    // selectedElement: state.rubricNew.selectedElement
  };
};

const actionCreators = {
  setEditingRubric,
  saveRubric,
  saveRubricTemp
}

export default connect(mapStateToProps, actionCreators)(RubricViewerElement)


















