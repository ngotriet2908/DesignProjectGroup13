import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";

import RubricOutline from "./RubricOutline";
import RubricEditor from "./RubricEditor";
import RubricBottomBar from "./RubricBottomBar";

import {request} from "../../services/request";
import {BASE } from "../../services/endpoints";

import {saveRubric, saveRubricTemp, setSelectedElement} from "../../redux/rubricNew/actions";
import {Spinner} from "react-bootstrap";
import RubricViewer from "./RubricViewer";


class RubricNew extends Component {
  constructor (props) {
    super(props);

    this.state = {
      isLoaded: false,
    }
  }

  componentDidMount() {
    request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId + "/rubric")
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);

        this.props.saveRubric(data.rubric);
        this.props.setSelectedElement(data.rubric.id);

        this.setState({
          isLoaded: true
        });
      })
      .catch(error => {
        console.error(error.message)

        this.setState({
          isLoaded: true
        });
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
        <div className={styles.outline}>
          <RubricOutline/>
        </div>

        <div className={styles.editor}>
          {this.props.isEditing ?
            <RubricEditor/>
            :
            <RubricViewer/>
          }
        </div>

        {this.props.isEditing &&
          <RubricBottomBar courseId={this.props.match.params.courseId} projectId={this.props.match.params.projectId}/>
        }
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubricNew.rubric,
    isEditing: state.rubricNew.isEditing,
  };
};

const actionCreators = {
  saveRubric,
  saveRubricTemp,
  setSelectedElement,
}

export default connect(mapStateToProps, actionCreators)(RubricNew)


















