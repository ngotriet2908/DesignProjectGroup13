import React, { Component } from 'react'
import styles from '../grading.module.css'
import {saveRubric, setSelectedElement} from "../../../redux/rubric/actions";
import {connect} from "react-redux";
import {findById} from "../../../redux/rubric/functions";
import {isCriterion} from "../../rubric/helpers";
import RubricViewerElementGrade from "../../rubric/RubricViewerElementGrade";
import classnames from 'classnames';
import globalStyles from "../../helpers/global.module.css";


class RubricViewer extends Component {
  constructor (props) {
    super(props);
  }

  onClickBack = () => {
    this.props.toggleOutlineHidden();
  }

  render () {
    if (!this.props.element.hasOwnProperty("content")) {
      return <div className={classnames(styles.rubricViewerContainer)}/>;
    }

    return (
      <div className={classnames(styles.rubricViewerContainer, this.props.outlineHidden && styles.rubricViewerContainerVisible)}>
        <div className={styles.rubricViewerHeader}>
          {isCriterion(this.props.element.content.type) ?
            <h3>Criterion</h3>
            :
            <h3>Section</h3>
          }
        </div>

        <div className={styles.rubricViewerSectionContainer}>
          <div className={styles.rubricViewerSectionTitle}>
            <h4>Title</h4>
          </div>
          {this.props.element.content.title}
        </div>

        {isCriterion(this.props.element.content.type) &&
        <div className={styles.rubricViewerSectionContainer}>
          <div className={styles.rubricViewerSectionTitle}>
            <h4>Text</h4>
          </div>
          <div dangerouslySetInnerHTML={{__html: this.props.element.content.text}}/>
        </div>
        }

        {isCriterion(this.props.element.content.type) && this.props.element.content.grade &&
        <RubricViewerElementGrade id={this.props.element.content.id} data={this.props.element.content.grade}/>
        }
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    // rubric: state.rubric.rubric,
    selectedElement: state.rubric.selectedElement,
    element: findById(state.rubric.rubric, state.rubric.selectedElement)
  };
};

const actionCreators = {
  setSelectedElement,
  saveRubric
}

export default connect(mapStateToProps, actionCreators)(RubricViewer)
