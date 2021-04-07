import {Button, Spinner, Modal, Alert} from 'react-bootstrap'
import React, {Component} from "react";
import {request} from "../../services/request";
import {BASE, PROJECT, USER_COURSES} from "../../services/endpoints";
import {connect} from "react-redux";
import {IoCloseOutline, IoCheckboxOutline, IoSquareOutline, IoReturnUpBack, IoAddOutline} from "react-icons/io5";
import classnames from 'classnames';
import globalStyles from "../helpers/global.module.css";
import {Can} from "../permissions/ProjectAbility";


class AssignSubmissionModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isLoaded: false,
      selected: null,
    }
  }

  onShow = () => {
    // set the selected grader
    this.setState({
      selected: this.props.currentGrader,
    })
  }

  onClose = () => {
    this.setState({
      isLoaded: false,
      selected: {},
    })

    this.props.toggleShow();
  }

  onAccept = () => {
    let body = this.state.selected.id;

    request(`${BASE}courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.submission.id}/assign`,
      "POST",
      body
    ).then(async () => {
      // let data = await response.json();

      this.setState({
        isLoaded: false,
        selected: null,
      })

      this.props.toggleShow();
      this.props.reloadPage();
    })
  }

  disassociateSubmission = () => {
    request(`${BASE}courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.submission.id}/dissociate`,
      "POST",
    ).then(async () => {
      this.setState({
        isLoaded: false,
        selected: null,
      })

      this.props.toggleShow();
      this.props.reloadPage();
    })
  }

  handleGraderClick = (grader) => {
    this.setState(prevState => ({
      selected: grader,
    }))
  }

  createFirstRow = () => {
    let result = [];

    result.push(
      <div className={
        classnames(globalStyles.modalBodyContainerRow,
          this.props.choice == null && globalStyles.modalBodyContainerRowActive)
      } onClick={() => this.props.setModalAssignChoice(null)}
      key="unassigned">
        {this.props.choice == null ?
          <IoCheckboxOutline size={16}/>
          :
          <IoSquareOutline size={16}/>
        }
        <span>Unassigned</span>
      </div>
    )

    if (this.props.currentGrader != null) {
      // const eq = this.props.choice != null && this.props.choice.id === this.props.currentGrader.id;
      //
      // result.push(
      //   <div className={classnames(globalStyles.modalBodyContainerRow,
      //     eq && globalStyles.modalBodyContainerRowActive)}
      //   onClick={() => this.props.setModalAssignChoice(this.props.currentGrader)}
      //   key="current">
      //     {eq ?
      //       <IoCheckboxOutline size={16}/>
      //       :
      //       <IoSquareOutline size={16}/>
      //     }
      //     <span>{this.props.currentGrader.name} - {this.props.currentGrader.groups.length} task(s)</span>
      //   </div>
      // )
    }

    return result;
  }

  render() {
    return(
      <Modal
        centered
        backdrop="static"
        size="lg"
        onShow={this.onShow}
        show={this.props.show}
        onHide={this.onClose}
        animation={false}
      >

        <div className={globalStyles.modalContainer}>
          <div className={globalStyles.modalHeaderContainer}>
            <h2>Assign</h2>
            <div className={classnames(globalStyles.modalHeaderContainerButton)} onClick={this.onClose}>
              <IoCloseOutline size={30}/>
            </div>
          </div>

          <Can I="edit" a="ManageGraders">

            <div className={globalStyles.modalDescriptionContainer}>
              <div>
                Choose a person who will be responsible for grading the submission <b>'{(this.props.submission != null)? this.props.submission.name : null}'</b> or leave the submission unassigned.
              </div>
            </div>


            {/* body */}
            <div className={globalStyles.modalBodyContainer}>
              {/*{this.createFirstRow()}*/}

              {this.props.graders.length === 0 &&
              <div className={classnames(globalStyles.modalBodyContainerRow, globalStyles.modalBodyContainerRowEmpty)}>
                No graders available in this project
              </div>
              }

              {this.props.graders
                // .filter((grader) => {
                // return (this.props.currentGrader == null) || (grader.id !== this.props.currentGrader.id)
              // })
                .map((grader) => {
                  const eq = this.state.selected != null && this.state.selected.id === grader.id;

                  return (
                    <div className={classnames(globalStyles.modalBodyContainerRow, eq && globalStyles.modalBodyContainerRowActive)}
                      key={grader.id}
                      onClick={() => this.handleGraderClick(grader)}>
                      {eq ?
                        <IoCheckboxOutline size={16}/>
                        :
                        <IoSquareOutline size={16}/>
                      }
                      <span>{grader.name} - {grader.submissions.length} submission(s)</span>
                    </div>
                  )
                })
              }
            </div>
          </Can>
          {/* footer */}
          <div className={classnames(globalStyles.modalFooterContainer, this.props.currentGrader && globalStyles.modalFooterContainerSpaceBetween)}>
            {this.props.currentGrader &&
              <div>
                <Button variant="red" onClick={this.disassociateSubmission}><IoReturnUpBack size={20}/> Return submission</Button>
              </div>
            }
            <div className={globalStyles.modalFooterContainerButtonGroup}>
              <Button variant="linkLightGray" onClick={this.onClose}>Cancel</Button>
              <Can I="edit" a="ManageGraders">
                <Button variant="lightGreen" onClick={this.onAccept}>Save</Button>
              </Can>

            </div>
          </div>
        </div>
      </Modal>
    )
  }
}

export default connect(null, null)(AssignSubmissionModal)