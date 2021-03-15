import React, { Component } from 'react'
import {connect} from "react-redux";
import {alterGrade} from "../../redux/rubric/actions";
import styles from "./rubric.module.css";

class RubricEditorElementGrade extends Component {
  constructor (props) {
    super(props);

    this.state = {
      min: 0,
      max: 10,
      step: 1,
    }
  }

  onChangeMinGrade = (event) => {
    let newGrade = {
      min: event.target.value,
      max: this.props.data.max,
      step: this.props.data.step
    }

    this.props.alterGrade(this.props.id, newGrade);
  }

  onChangeMaxGrade = (event) => {
    let newGrade = {
      min: this.props.data.min,
      max: event.target.value,
      step: this.props.data.step
    }

    this.props.alterGrade(this.props.id, newGrade);
  }

  onChangeStepGrade = (event) => {
    let newGrade = {
      min: this.props.data.min,
      max: this.props.data.max,
      step: event.target.value,
    }

    this.props.alterGrade(this.props.id, newGrade);
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
        </div>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    
  };
};

const actionCreators = {
  alterGrade
}

export default connect(mapStateToProps, actionCreators)(RubricEditorElementGrade)