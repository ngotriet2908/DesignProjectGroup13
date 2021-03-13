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
import {Can, ability, updateAbility} from "../permissions/ProjectAbility";


class RubricNew extends Component {
  constructor (props) {
    super(props);

    this.state = {
      isLoaded: false,
      project: {}
    }
  }

  componentDidMount() {

    //TODO in case of directly load this page or refresh page <=> no ability is found
    if (ability.rules.length === 0) {
      request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId)
        .then(response => {
          return response.json()
        })
        .then(data => {
          if (data.grader !== null && data.grader.privileges !== null) {
            updateAbility(ability, data.grader.privileges)
            this.setState({
              project: data
            })
          } else {
            console.log("no grader or privileges found")
          }
          console.log(ability.rules)
        })
    }

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


















