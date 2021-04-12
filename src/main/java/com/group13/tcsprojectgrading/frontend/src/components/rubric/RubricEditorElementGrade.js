import React, { Component } from 'react'
import {connect} from "react-redux";
import {alterGrade} from "../../redux/rubric/actions";
import styles from "./rubric.module.css";
import FormControl from "@material-ui/core/FormControl";
import FilledInput from "@material-ui/core/FilledInput";
import InputAdornment from "@material-ui/core/InputAdornment";
import FormHelperText from "@material-ui/core/FormHelperText";
import TextField from "@material-ui/core/TextField";
import Grid from "@material-ui/core/Grid";

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

  render () {
    return (
      <div className={`${styles.viewerSectionContainer} ${styles.viewerGradeSectionContainer}`}>
        <div className={styles.viewerSectionTitle}>
          <h4>Grade</h4>
        </div>
        <Grid container spacing={1} className={styles.viewerGradeSectionContent} >
          <Grid item xs={3}>
            <TextField
              label="Minimum"
              type="number"
              InputLabelProps={{
                shrink: true,
              }}
              InputProps={{
                startAdornment: <InputAdornment position="start">Pts</InputAdornment>,
              }}
              variant="outlined"
              value={this.props.data.min}
              onChange={this.onChangeMinGrade}
            />
          </Grid>

          <Grid item xs={3}>
            <TextField
              label="Maximum"
              type="number"
              InputLabelProps={{
                shrink: true,
              }}
              InputProps={{
                startAdornment: <InputAdornment position="start">Pts</InputAdornment>,
              }}
              variant="outlined"
              value={this.props.data.max}
              onChange={this.onChangeMaxGrade}
            />
          </Grid>

          <Grid item xs={3}>
            <TextField
              label="Step"
              type="number"
              InputLabelProps={{
                shrink: true,
              }}
              variant="outlined"
              value={this.props.data.step}
              onChange={this.onChangeStepGrade}
            />
          </Grid>

          <Grid item xs={3}>
            <TextField
              label="Weight"
              type="number"
              InputLabelProps={{
                shrink: true,
              }}
              variant="outlined"
              value={this.props.data.weight}
              onChange={this.onChangeWeight}
              helperText="Between 0 and 1"
            />
          </Grid>
        </Grid>
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