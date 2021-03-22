import React, { Component } from 'react'
import {connect} from "react-redux";
import {alterGrade} from "../../redux/rubric/actions";
import styles from "./rubric.module.css";

class RubricEditorElementGrade extends Component {
  constructor (props) {
    super(props);
  }

  onChangeMinGrade = (event) => {
    let newGrade = {
      ...this.props.data,
      min: event.target.value,
    }

    this.props.alterGrade(this.props.id, newGrade, this.props.currentPath + "/content/grade");
  }

  onChangeMaxGrade = (event) => {
    let newGrade = {
      ...this.props.data,
      max: event.target.value,
    }

    this.props.alterGrade(this.props.id, newGrade, this.props.currentPath + "/content/grade");
  }

  onChangeStepGrade = (event) => {
    let newGrade = {
      ...this.props.data,
      step: event.target.value,
    }

    this.props.alterGrade(this.props.id, newGrade, this.props.currentPath + "/content/grade");
  }

  onChangeWeight = (event) => {
    let newGrade = {
      ...this.props.data,
      weight: event.target.value
    }

    this.props.alterGrade(this.props.id, newGrade, this.props.currentPath + "/content/grade");
  }

  // onBlurDefault = () => {
  //   if (this.props.data.min === "") {
  //
  //   }
  // }

  render () {
    return (
      <div className={`${styles.viewerSectionContainer} ${styles.viewerGradeSectionContainer}`}>
        <div className={styles.viewerSectionTitle}>
          <h4>Grade</h4>
        </div>
        <div className={styles.viewerGradeSectionContent}>
          <div>
            <label htmlFor="gradeMin">
            Min:
            </label>
            <input type="number" id="gradeMin" value={this.props.data.min} onChange={this.onChangeMinGrade}/>
          </div>

          <div>
            <label htmlFor="gradeMax">
            Max:
            </label>
            <input type="number" id="gradeMax" value={this.props.data.max} onChange={this.onChangeMaxGrade}/>
          </div>

          <div>
            <label htmlFor="gradeStep">
            Step:
            </label>
            <input type="number" id="gradeStep" value={this.props.data.step} onChange={this.onChangeStepGrade}/>
          </div>

          <div>
            <label htmlFor="gradeWeight">
              Weight (coefficient):
            </label>
            <input type="number" id="gradeWeight" value={this.props.data.weight} onChange={this.onChangeWeight}/>
          </div>
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
  alterGrade
}

export default connect(mapStateToProps, actionCreators)(RubricEditorElementGrade)