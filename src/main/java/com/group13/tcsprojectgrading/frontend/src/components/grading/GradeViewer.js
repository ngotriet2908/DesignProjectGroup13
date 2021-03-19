import React, { Component } from 'react'
import styles from './grading.module.css'
import {connect} from "react-redux";

import globalStyles from '../helpers/global.module.css';
import Card from "react-bootstrap/Card";
import {isCriterion} from "../rubric/helpers";
import Button from "react-bootstrap/Button";
import {findById} from "../../redux/rubric/functions";
import {findCriterion} from "../../redux/grading/functions";
import {setActive} from "../../redux/grading/actions";
import {request} from "../../services/request";

import classnames from 'classnames';
import {IoCheckboxOutline, IoFileTrayOutline, IoSearchOutline, IoSquareOutline} from "react-icons/io5";
import {findUserById} from "../../redux/user/functions";


class GradeViewer extends Component {
  constructor (props) {
    super(props);

    this.state = {
      isLoaded: false
    }
  }

  componentDidMount() {

  }

  makeActive = (key) => {
    // send request
    request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}/grading/${this.props.selectedElement}/active/${key}`, "PUT")
      .then(() => {
        this.props.setActive(this.props.selectedElement, key)
      })
      .catch(error => {
        console.error(error.message)
      });
  }

  // getUserData = (userId ) => {
  //   if (this.props.self.id === userId) {
  //     return this.props.self;
  //   } else {
  //     let user = findUserById(this.props.users, userId);
  //
  //     if (user) {
  //       return user;
  //     } else {
  //       // fetch user's data
  //       request(`/api/users/${userId}`, "GET")
  //         .then((response) => {
  //           console.log(response);
  //         })
  //         .catch(error => {
  //           console.error(error.message)
  //         });
  //     }
  //   }
  // }

  fetchUserData = (userIds) => {
    if (this.props.self.id === userId) {
      return this.props.self;
    } else {
      let user = findUserById(this.props.users, userId);

      if (user) {
        return user;
      } else {
        // fetch user's data
        request(`/api/users/${userId}`, "GET")
          .then((response) => {
            console.log(response);
          })
          .catch(error => {
            console.error(error.message)
          });
      }
    }
  }

  getUnknownUsers = (userIds) =>  {
    let unknownUsers = []

    userIds.forEach((userId) => {
      if (!findUserById(userId, this.props.users)) {
        unknownUsers.push(userId)
      }
    })

    return unknownUsers;
  }




  render () {
    // if (!this.state.isLoaded) {
    //   return(
    //     <div>
    //       NO!
    //     </div>
    //   )
    // }

    return (
      <div className={styles.gradeViewerContainer}>
        {this.props.element.content && isCriterion(this.props.element.content.type) &&
          (<Card className={styles.gradeViewerCard}>
            <Card.Body>
              <h4>Grading history</h4>
              {this.props.grades ?
                this.props.grades.history.map((grade, index) => {
                  return (
                    <div key={index} onClick={() => this.makeActive(index)}
                      className={classnames(styles.gradeViewerRow, (index === this.props.grades.active) && styles.gradeViewerRowActive)}>
                      <div className={styles.gradeViewerRowIcon}>
                        {index === this.props.grades.active ?
                          <IoCheckboxOutline size={26}/>
                          :
                          <IoSquareOutline size={26}/>
                        }
                      </div>
                      <div className={styles.gradeViewerRowContent}>
                        <div>
                        Graded by {grade.userId} on {new Date(grade.created).toDateString()}
                        </div>
                        <div>
                        Grade {grade.grade}
                          {grade.comment != null &&
                        <span> with a note: {grade.comment}
                          {/*<div className={styles.gradeViewerRowExplanation}>{grade.comment}</div>*/}
                        </span>
                          }
                        </div>
                      </div>
                    </div>
                  )
                })
                :
                <div className={styles.gradeViewerNotGraded}>
                  <IoFileTrayOutline size={26}/>
                  <h6>Not graded yet.</h6>
                </div>
              }
            </Card.Body>
          </Card>
          )
        }
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    // selectedElement: state.rubric.selectedElement,
    // rubric: state.rubric.rubric,
    // assessment: state.grading.assessment,
    users: state.users.users,
    self: state.users.self,

    element: findById(state.rubric.rubric, state.rubric.selectedElement),
    grades: findCriterion(state.grading.assessment.grades, state.rubric.selectedElement)
  };
};

const actionCreators = {
  setActive
}

export default connect(mapStateToProps, actionCreators)(GradeViewer)
