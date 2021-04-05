import {Button, Modal, Form, Card, ListGroup, ListGroupItem, Alert} from 'react-bootstrap'
import React, {Component} from "react";
import styles from "./assign.module.css";
import TaskCard from "./TaskCard";
import {IoCheckboxOutline, IoCloseOutline, IoSquareOutline} from "react-icons/io5";
import classnames from "classnames";
import {request} from "../../services/request";
import {BASE, PROJECT, USER_COURSES} from "../../services/endpoints";
import {isTeacher} from "../permissions/functions";

class EditGradersModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      graders: [],
      showAlert: false,
      alertBody: false,
    }
  }

  componentDidMount() {
    request(`${BASE}${USER_COURSES}/${this.props.routeParams.courseId}/${PROJECT}/${this.props.routeParams.projectId}/management/addGraders/getAllGraders`)
      .then(async response => {
        let graders = await response.json();

        this.setState({
          graders: graders
        })

        console.log(graders);
        console.log(this.props.activeGraders);

        // let activeGraders = [...data]
        // activeGraders = activeGraders.filter((grader) => {
        //   return this.containsObject(grader, this.state.graders)
        // })
        //
        // let availableGraders = [...data]
        // availableGraders = availableGraders.filter((grader) => {
        //   return !this.containsObject(grader, this.state.graders)
        // })
        // console.log(availableGraders)

        // this.setState({
        //   modalEditGradersShow: true,
        //   modalEditGradersActiveGraders: activeGraders,
        //   modalEditGradersAvailableGraders: availableGraders,
        //   modalEditShowAlert: false,
        //   modalEditAlertBody: "",
        // })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  deactivate = (grader) => {
    if (this.hasTasks(grader.id)) {
      this.modalEditGradersHandleShowAlert(`Grader ${grader.name} is holding tasks, can't remove grader`)
      return
    }

    if (grader.role === "TEACHER_ROLE") {
      this.modalEditGradersHandleShowAlert(`${grader.name} is a teacher, can't remove grader`)
      return
    }

    let availableGraders = [...this.state.modalEditGradersAvailableGraders]
    availableGraders.push(grader)
    let activeGraders = [...this.state.modalEditGradersActiveGraders]
    activeGraders = activeGraders.filter((grader1) => {
      return grader1.id !== grader.id
    })
    // console.log(activeGraders)
    // console.log(availableGraders)

    this.setState({
      modalEditGradersActiveGraders: activeGraders,
      modalEditGradersAvailableGraders: availableGraders,
    })
  }

  activate = (grader) => {
    let activeGraders = [...this.state.modalEditGradersActiveGraders]
    activeGraders.push(grader)
    let availableGraders = [...this.state.modalEditGradersAvailableGraders]
    availableGraders = availableGraders.filter((grader1) => {
      return grader1.id !== grader.id
    })
    // console.log(activeGraders)
    // console.log(availableGraders)
    this.setState({
      modalEditGradersActiveGraders: activeGraders,
      modalEditGradersAvailableGraders: availableGraders,
    })
  }

  // modalEditGradersHandleShowAlert = (body) => {
  //   this.setState({
  //     modalEditShowAlert: true,
  //     modalEditAlertBody: body,
  //   })
  // }
  //
  // modalEditGradersHandleCloseAlert = () => {
  //   this.setState({
  //     modalEditShowAlert: false,
  //     modalEditAlertBody: "",
  //   })
  // }

  // modalEditGradersHandleClose = () => {
  //   this.setState({
  //     modalEditGradersShow: false,
  //     modalEditGradersActiveGraders: [],
  //     modalEditGradersAvailableGraders: [],
  //     modalEditShowAlert: false,
  //     modalEditAlertBody: "",
  //   })
  // }

  modalEditGradersHandleAccept = () => {
    request(`${BASE}${USER_COURSES}/${this.props.match.params.courseId}/${PROJECT}/${this.props.match.params.projectId}/management/addGraders`,
      "POST",
      this.state.modalEditGradersActiveGraders
    )
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data)

        this.setState({
          modalEditGradersShow: false,
          modalEditShowAlert: false,
          modalEditAlertBody: "",
          graders: data.graders,
          notAssigned: data.notAssigned
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  hasTasks = (graderId) => {
    let i;
    for(i = 0; i < this.state.graders.length; i++) {
      if (this.state.graders[i].id === graderId
        && this.state.graders[i].groups !== null && this.state.graders[i].groups.length > 0) {
        return true;
      }
    }
    return false;
  }

  toggleSelected = () => {

  }

  onClose = () => {
    this.props.toggleShow();
  }

  onAccept = () => {

  }

  render() {
    return(
    // <Modal centered
    //        backdrop="static"
    //        size="lg"
    //        show={this.props.show}
    //        onHide={this.props.onClose}>
    //   <Modal.Header closeButton>
    //     <Modal.Title>Group Details</Modal.Title>
    //   </Modal.Header>
    //   <Modal.Body>
    //     {
    //       <div>
    //         {(!this.props.showAlert)? null:
    //           <Alert variant="danger" onClose={this.props.closeAlertHandle} dismissible>
    //             <p>
    //               {this.props.alertBody}
    //             </p>
    //           </Alert>}
    //         <h4>Active Graders</h4>
    //         <Card className={styles.editGradersModalCard}>
    //           <ListGroup className={styles.notAssignedGroupList}>
    //             {this.props.activeGraders
    //               // .filter((grader) => {
    //               //   return grader.name.toLowerCase().includes(this.state.groupsFilterString.toLowerCase())
    //               // })
    //               .map(grader => {
    //                 return (
    //                   <ListGroupItem
    //                     key={grader.id}
    //                     className={styles.listGroupItemCustom}
    //                     action onClick={() => this.props.onClickDeactive(grader)} >
    //                     name: {grader.name}
    //                   </ListGroupItem>
    //                 )
    //               })}
    //           </ListGroup>
    //         </Card>
    //
    //         <h4>Available Graders</h4>
    //         <Card className={styles.editGradersModalCard}>
    //           <ListGroup className={styles.notAssignedGroupList}>
    //             {this.props.availableGraders
    //               // .filter((grader) => {
    //               //   return grader.name.toLowerCase().includes(this.state.groupsFilterString.toLowerCase())
    //               // })
    //               .map(grader => {
    //                 return (
    //                   <ListGroupItem
    //                     key={grader.id}
    //                     className={styles.listGroupItemCustom}
    //                     action onClick={() => this.props.onClickActive(grader)} >
    //                     name = {grader.name}
    //                   </ListGroupItem>
    //                 )
    //               })}
    //           </ListGroup>
    //         </Card>
    //       </div>
    //     }
    //   </Modal.Body>
    //   <Modal.Footer>
    //     <Button variant="secondary"
    //             onClick={() => {
    //               this.props.onClose()
    //             }}>
    //       Cancel
    //     </Button>
    //
    //     <Button variant="primary"
    //             onClick={() =>
    //             {
    //               this.props.onAccept()
    //             }}>
    //       Apply changes
    //     </Button>
    //   </Modal.Footer>
    // </Modal>

      <Modal
        centered
        backdrop="static"
        size="lg"
        show={this.props.show}
        onHide={this.onClose}
        animation={false}
      >
        <div className={styles.modalContainer}>
          <div className={styles.modalHeaderContainer}>
            <h2>Graders</h2>
            <div className={styles.modalHeaderContainerButton} onClick={this.onClose}>
              <IoCloseOutline size={30}/>
            </div>
          </div>

          <div className={styles.modalDescriptionContainer}>
            <div>
              Choose people responsible for grading
            </div>
          </div>

          {/* body */}
          <div className={styles.modalBodyContainer}>
            {/* teachers */}
            {this.state.graders
              .filter(grader => {
                return isTeacher(grader.role)
              })
              .map((grader) => {
                const eq = false;

                return (
                  <div className={classnames(styles.modalBodyContainerRow, eq && styles.modalBodyContainerRowActive)}
                    key={grader.id}
                    onClick={() => this.props.setModalAssignChoice(grader)}>
                    {eq ?
                      <IoCheckboxOutline size={16}/>
                      :
                      <IoSquareOutline size={16}/>
                    }
                    <span>{grader.name}</span>
                  </div>
                )
              })
            }

            {/* TAs */}
            {this.state.graders
              .filter(grader => {
                return !isTeacher(grader.role)
              })
              .map((grader) => {
              // const eq = this.props.choice != null && this.props.choice.id === grader.id;
                const eq = false;

                return (
                  <div className={classnames(styles.modalBodyContainerRow, eq && styles.modalBodyContainerRowActive)}
                    key={grader.id}
                    onClick={() => this.props.setModalAssignChoice(grader)}>
                    {eq ?
                      <IoCheckboxOutline size={16}/>
                      :
                      <IoSquareOutline size={16}/>
                    }
                    <span>{grader.name}</span>
                  </div>
                )
              })
            }
          </div>

          {/* footer */}
          <div className={styles.modalFooterContainer}>
            <div className={styles.modalFooterContainerButtonGroup}>
              <Button variant="linkLightGray" onClick={this.onClose}>
                Cancel
              </Button>

              <Button variant="lightGreen"
                onClick={this.onAccept}>
                Save
              </Button>
            </div>
          </div>
        </div>
      </Modal>
    )
  }
}

export default EditGradersModal;