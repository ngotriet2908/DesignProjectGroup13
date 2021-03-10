import React, { Component } from 'react'
import styles from './grading.module.css'
import FileViewer from "./FileViewer";
import RubricViewer from "./RubricViewer";
import ControlBar from "./ControlBar";
import GradeEditor from "./GradeEditor";
import {rubric} from "../rubricNew/rubricSample";
import {saveRubric, setSelectedElement} from "../../redux/rubricNew/actions";
import {connect} from "react-redux";
import {Spinner} from "react-bootstrap";


class Grading extends Component {
  constructor (props) {
    super(props)

    this.rubric = rubric;

    this.state = {
      isLoaded: false,
    }
  }

  componentDidMount() {
    this.props.setSelectedElement(this.rubric.id);
    this.props.saveRubric(this.rubric);

    this.setState({
      isLoaded: true
    });
  }

  render () {
    if (!this.state.isLoaded) {
      return(
        <div className={styles.container}>
          <Spinner className={styles.spinner} animation="border" role="status">
            <span className="sr-only">Loading...</span>
          </Spinner>
        </div>
      )
    }

    return (
      <div className={styles.container}>
        <ControlBar/>
        <FileViewer/>
        <RubricViewer/>
        <GradeEditor/>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    // rubric: state.rubricNew.rubric,
  };
};

const actionCreators = {
  setSelectedElement,
  saveRubric
}

export default connect(mapStateToProps, actionCreators)(Grading)
