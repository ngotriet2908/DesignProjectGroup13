import styles from "./assign.module.css";
import React, {Component} from "react";
import GraderCard from "./GraderCard";
import {ListGroup, ListGroupItem} from "react-bootstrap";
import TaskCard from "./TaskCard";
import {notAssignedGroupsData, gradersData} from './GradersData'
import {Button, Card, FormControl, Modal, Alert, Spinner} from 'react-bootstrap'
import {request} from "../../services/request";
import {BASE, COURSES, PROJECT, USER_COURSES} from "../../services/endpoints";
import AssigningModal from "./AssigningModal";
import { withRouter } from 'react-router-dom'
import EditGradersModal from "./EditGradersModal";

class GraderManagement extends Component {

  constructor(props) {
    super(props)
    this.state = {
      graders : [],
      notAssigned: [],
      groupsFilterString: "",
      gradersFilterString: "",
      hideSearch: true,
      isLoading: true,

      //return tasks modal
      modalGraderShow: false,
      modalGraderObj: null,

      //assign tasks modal
      modalAssignShow: false,
      modalAssignGrader: null,
      modalAssignTask: null,
      modalAssignIsFromNotAssigned: false,
      modalAssignGraderChoice: null,

      //assign tasks modal
      modalEditGradersShow: false,
      modalEditGradersActiveGraders: [],
      modalEditGradersAvailableGraders: [],
      modalEditShowAlert: false,
      modalEditAlertBody: "",

      //Alert
      alertShow: false,
      alertBody: "",
    }
  }

  componentDidMount () {
    console.log("Grader Management mounted.")
    console.log(this.props)
    this.setState({
      isLoading: true
    })
    this.projectManagementHandler()

    // this.setState({
    //   graders : gradersData,
    //   notAssigned: notAssignedGroupsData,
    // })
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

  projectManagementHandler = () => {
    request(`${BASE}${USER_COURSES}/${this.props.match.params.courseId}/${PROJECT}/${this.props.match.params.projectId}/management`)
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.setState({
          graders: data.graders,
          notAssigned: data.notAssigned,
          isLoading: false
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  addGradersHandler = () => {
    request(`${BASE}${USER_COURSES}/${this.props.match.params.courseId}/${PROJECT}/${this.props.match.params.projectId}/addGraders`, "POST")
      .then(response => {
        this.projectManagementHandler();
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  showAlertOnScreen = (body) => {
    this.setState({
      alertShow: true,
      alertBody: body,
    })
  }

  modalAssignHandleClose = (event) => {
    this.setState({
      modalAssignShow: false,
      modalAssignGrader: null,
      modalAssignTask: null,
      modalAssignIsFromNotAssigned: false
    })
  }

  modalAssignHandleShow = (grader, group, isFromNotAssigned) => {
    this.setState({
      modalAssignShow: true,
      modalAssignGrader: grader,
      modalAssignTask: group,
      modalAssignIsFromNotAssigned: isFromNotAssigned
    })
  }

  modalAssignHandleAccept = (choice) => {
    // alert("assign to: " + choice.name)

    this.handleAssignTask(
      this.state.modalAssignGrader,
      this.state.modalAssignIsFromNotAssigned,
      choice,
      this.state.modalAssignTask)

    this.setState({
      modalAssignShow: false,
      modalAssignGrader: null,
      modalAssignTask: null,
      modalAssignIsFromNotAssigned: false
    })
  }

  modalAssignHandleReturnTask = () => {
    this.showAlertOnScreen(`Return ${this.state.modalAssignTask.name} from ${this.state.modalAssignGrader.name} to Not Assigned`)

    this.handleReturnTask(
      this.state.modalAssignGrader,
      this.state.modalAssignTask)

    this.setState({
      modalAssignShow: false,
      modalAssignGrader: null,
      modalAssignTask: null,
      modalAssignIsFromNotAssigned: false
    })
  }

  // handleReturnTask = (grader, task) => {
  //   let graderOriginalTasks = [...grader.groups]
  //
  //   let graderLeftTasks = graderOriginalTasks.filter((group) => {
  //     return group.id !== task.id
  //   })
  //   let notAssignedTasks = [...this.state.notAssigned]
  //   notAssignedTasks.push(task)
  //
  //   let gradersList = [...this.state.graders]
  //   gradersList.forEach((grader1) => {
  //     if (grader1.id === grader.id) {
  //       grader1.groups = graderLeftTasks
  //     }
  //   })
  //   this.setState({
  //     notAssigned: notAssignedTasks,
  //     graders : gradersList,
  //   })
  // }

  handleReturnTask = (grader, task) => {
    request(`${BASE}${USER_COURSES}/${this.props.match.params.courseId}/${PROJECT}/${this.props.match.params.projectId}/management/assign/${task.id}/${task.isGroup}/notAssigned`)
      .then(response => {
        return response.json();
      })
      .then(data => {
        this.showAlertOnScreen(`Return ${task.name} to not assigned from ${grader.name}`)
        this.setState({
          graders: data.graders,
          notAssigned: data.notAssigned
        })
      })
      .catch(error => {
        console.error(error.message);
      });
    }

  // handleAssignTask = (fromGrader, isFromNotAssigned,toGrader, task) => {
  //   if (isFromNotAssigned) {
  //     this.showAlertOnScreen(`Assigned ${task.name} to ${toGrader.name} from Not Assigned`)
  //
  //     let notAssignedTasks = [...this.state.notAssigned]
  //     console.log(notAssignedTasks)
  //     let notAssignedLeftTasks = notAssignedTasks.filter((group) => {
  //       return group.id !== task.id
  //     })
  //
  //     let gradersList = [...this.state.graders]
  //     console.log(gradersList)
  //
  //     gradersList.forEach((grader1) => {
  //       if (grader1.id === toGrader.id) {
  //         grader1.groups.push(task)
  //       }
  //     })
  //
  //     console.log(gradersList)
  //     this.setState({
  //       notAssigned: notAssignedLeftTasks,
  //       graders : gradersList,
  //     })
  //   } else {
  //     this.showAlertOnScreen(`Assigned ${task.name} to ${toGrader.name} from ${fromGrader.name}`)
  //     let gradersList = [...this.state.graders]
  //     let fromGraderTasks = [...fromGrader.groups]
  //     let toGraderTasks = [...toGrader.groups]
  //
  //     let fromGraderLeftTasks = fromGraderTasks.filter((group) => {
  //       return group.id !== task.id
  //     })
  //     toGraderTasks.push(task)
  //
  //     gradersList.forEach((grader) => {
  //       if (grader.id === fromGrader.id) {
  //         grader.groups = fromGraderLeftTasks
  //       } else if (grader.id === toGrader.id) {
  //         grader.groups = toGraderTasks
  //       }
  //     })
  //
  //     this.setState({
  //       graders : gradersList,
  //     })
  //   }
  // }

  handleAssignTask = (fromGrader, isFromNotAssigned,toGrader, task) => {
      request(`${BASE}${USER_COURSES}/${this.props.match.params.courseId}/${PROJECT}/${this.props.match.params.projectId}/management/assign/${task.id}/${task.isGroup}/${toGrader.id}`)
        .then(response => {
          return response.json();
        })
        .then(data => {
          console.log(data);
          if (isFromNotAssigned) {
            this.showAlertOnScreen(`Assigned ${task.name} to ${toGrader.name} from Not Assigned`)
          } else {
            this.showAlertOnScreen(`Assigned ${task.name} to ${toGrader.name} from ${fromGrader.name}`)
          }
          this.setState({
            graders: data.graders,
            notAssigned: data.notAssigned
          })
        })
        .catch(error => {
          console.error(error.message);
        });
  }

  //Grader modal handlers
  modalGraderHandleClose = (event) => {
    this.setState({
      modalGraderShow: false,
      modalGraderObj: null
    })
  }

  modalGraderHandleShow = (grader) => {
    this.setState({
      modalGraderShow: true,
      modalGraderObj: grader
    })
  }

  modalGraderHandleAccept = () => {
    this.showAlertOnScreen(`Return all tasks from ${this.state.modalGraderObj.name} to Not Assigned`)

    this.handleReturnTasks(this.state.modalGraderObj)
    this.setState({
      modalGraderShow: false,
      modalGraderObj: null
    })
  }

  handleHideSearch = (event) => {
    this.setState((prevState) => {
      return {hideSearch : !prevState.hideSearch}
    })
  }

  handleSearchChange = (event) => {
    this.setState({
      groupsFilterString : event.target.value
    })
  }

  onGroupClicked = (group, grader, isFromNotAssigned) => {
    this.modalAssignHandleShow(grader, group, isFromNotAssigned)
  }

  containsObject(obj, list) {
    let i;
    for (i = 0; i < list.length; i++) {
      if (list[i].id === obj.id) {
        return true;
      }
    }
    return false;
  }

  // handleReturnTasks = (grader) => {
  //   let graderOriginalTasks = [...grader.groups]
  //   console.log(graderOriginalTasks)
  //   let graderReturnTasks = graderOriginalTasks.filter((group) => {
  //     return group.progress < 100;
  //   })
  //   console.log(graderReturnTasks)
  //   let graderLeftTasks = graderOriginalTasks.filter((group) => {
  //     return !this.containsObject(group, graderReturnTasks)
  //   })
  //   console.log(graderLeftTasks)
  //   let notAssignedTasks = [...this.state.notAssigned]
  //   graderReturnTasks.forEach(tasks => {
  //     notAssignedTasks.push(tasks)
  //   })
  //   console.log(notAssignedTasks)
  //
  //   let gradersList = [...this.state.graders]
  //   console.log(gradersList)
  //   gradersList.forEach((grader1) => {
  //     if (grader1.id === grader.id) {
  //       grader1.groups = graderLeftTasks
  //     }
  //   })
  //   console.log(gradersList)
  //   this.setState({
  //     notAssigned: notAssignedTasks,
  //     graders : gradersList,
  //   })
  // }

  handleReturnTasks = (grader) => {
    request(`${BASE}${USER_COURSES}/${this.props.match.params.courseId}/${PROJECT}/${this.props.match.params.projectId}/management/return/${grader.id}`)
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.showAlertOnScreen(`return all tasks from ${grader.name}`)
        this.setState({
          graders: data.graders,
          notAssigned: data.notAssigned
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  handleGraderSearchChange = (event) => {
    // let list = [...this.state.graders]
    // let filteredList = list.filter((grader) => {
    //   return grader.name.toLowerCase().includes(event.target.value.toLowerCase())
    // })
    this.setState({
      gradersFilterString : event.target.value
    })
  }

  modalEditGradersHandleClose = (event) => {
    this.setState({
      modalEditGradersShow: false,
      modalEditGradersActiveGraders: [],
      modalEditGradersAvailableGraders: [],
      modalEditShowAlert: false,
      modalEditAlertBody: "",
    })
  }

  modalEditGradersHandleAccept = (event) => {
    request(`${BASE}${USER_COURSES}/${this.props.match.params.courseId}/${PROJECT}/${this.props.match.params.projectId}/addGraders`,
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

  modalEditGradersHandleShow = () => {
    request(`${BASE}${USER_COURSES}/${this.props.match.params.courseId}/${PROJECT}/${this.props.match.params.projectId}/addGraders/getAllGraders`)
      .then(response => {
        return response.json();
      })
      .then(data => {
        let activeGraders = [...data]
        activeGraders = activeGraders.filter((grader) => {
          return this.containsObject(grader, this.state.graders)
        })

        let availableGraders = [...data]
        availableGraders = availableGraders.filter((grader) => {
          return !this.containsObject(grader, this.state.graders)
        })
        // console.log(availableGraders)

        this.setState({
          modalEditGradersShow: true,
          modalEditGradersActiveGraders: activeGraders,
          modalEditGradersAvailableGraders: availableGraders,
          modalEditShowAlert: false,
          modalEditAlertBody: "",
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  modalEditGradersHandleDeactive = (grader) => {
    if (this.hasTasks(grader.id)) {
      this.modalEditGradersHandleShowAlert(`Grader ${grader.name} is holding tasks, can't remove grader`)
      return
    }

    let availableGraders = [...this.state.modalEditGradersAvailableGraders]
    availableGraders.push(grader)
    let activeGraders = [...this.state.modalEditGradersActiveGraders]
    activeGraders = activeGraders.filter((grader1) => {
      return grader1.id !== grader.id
    })
    console.log(activeGraders)
    console.log(availableGraders)

    this.setState({
      modalEditGradersActiveGraders: activeGraders,
      modalEditGradersAvailableGraders: availableGraders,
    })
  }

  modalEditGradersHandleActive = (grader) => {
    let activeGraders = [...this.state.modalEditGradersActiveGraders]
    activeGraders.push(grader)
    let availableGraders = [...this.state.modalEditGradersAvailableGraders]
    availableGraders = availableGraders.filter((grader1) => {
      return grader1.id !== grader.id
    })
    console.log(activeGraders)
    console.log(availableGraders)
    this.setState({
      modalEditGradersActiveGraders: activeGraders,
      modalEditGradersAvailableGraders: availableGraders,
    })
  }

  modalEditGradersHandleShowAlert = (body) => {
    this.setState({
      modalEditShowAlert: true,
      modalEditAlertBody: body,
    })
  }

  modalEditGradersHandleCloseAlert = () => {
    this.setState({
      modalEditShowAlert: false,
      modalEditAlertBody: "",
    })
  }

  render () {
    return (
      (this.state.isLoading)?
        <Spinner className={styles.spinner} animation="border" role="status">
          <span className="sr-only">Loading...</span>
        </Spinner>
        :
      <div className={styles.graderManagement}>
        {(!this.state.alertShow)? null :
          <Alert variant="success" onClose={() => {this.setState({alertShow:false})}} dismissible>
            <p>
              {this.state.alertBody}
            </p>
          </Alert>}


        <Card border="secondary" className={styles.gradersCardContainer}>
          <div className={styles.manageTaToolbar}>
            <h3 className={styles.subtitle} >Manage Graders</h3>
            <FormControl className={styles.manageTaSearch}
                         type="text"
                         placeholder="Search for grader or group name"
                         onChange={this.handleGraderSearchChange}/>

            <Button className={styles.manageTaToolbarButton}
                    variant="primary"
                    onClick={this.modalEditGradersHandleShow}>
              edit graders
            </Button>
            <Button className={styles.manageTaToolbarButton}
                    variant="primary"
                    onClick={null}>
              sort
            </Button>
          </div>

          <div className={styles.gradersContainer}>
            {/*<h3>Assigned to Graders</h3>*/}
            <Card border="secondary" className={styles.gradersListContainer}>
              <ul className={styles.grader_ul}>
                {this.state.graders
                  .filter((grader) => {
                    let filterStringTmp = this.state.gradersFilterString.toLowerCase()
                    return (grader.name.toLowerCase().includes(filterStringTmp)
                      || grader.groups.reduce(((result, group) => result || group.name.toLowerCase().includes(filterStringTmp)),false))
                  })
                  .map(grader => {
                    return (
                      <li className={styles.grader_li} key={grader.id}>
                        <GraderCard grader={grader}
                          onReturnClicked={() => this.modalGraderHandleShow(grader)}
                          onClickFunc={this.onGroupClicked}
                        />
                      </li>
                    )
                  })}
              </ul>
            </Card>
          </div>
        </Card>


        <Card border="secondary" className={styles.notAssignedContainer}>
          <div className={styles.notAssignedToolbar}>
            <h4 className={styles.notAssignedText}>Not assigned </h4>
            {/*<Button className={styles.notAssignedButton}*/}
            {/*        variant="primary"*/}
            {/*        onClick={() => null}>*/}
            {/*  hide groups*/}
            {/*</Button> {" "}*/}
            <Button className={styles.notAssignedButton}
              variant="primary"
              onClick={this.handleHideSearch}>
                search
            </Button>
            {(this.state.hideSearch) ? null :
              <FormControl className={styles.notAssignedToolBarSearch}
                type="text"
                placeholder="Normal text"
                onChange={this.handleSearchChange}/>
            }
            <Button className={styles.notAssignedButton}
              variant="primary"
              onClick={null}>
                bulk assign
            </Button>
            <h5 className={styles.notAssignedCount}> Submissions: {this.state.notAssigned.length}</h5>
          </div>

          <ListGroup className={styles.notAssignedGroupList}>
            {this.state.notAssigned
              .filter((group) => {
                return group.name.toLowerCase().includes(this.state.groupsFilterString.toLowerCase())
              })
              .map(group => {
                return (
                  <ListGroupItem
                    key={group.id}
                    className={styles.listGroupItemCustom}
                    action onClick={() => this.onGroupClicked(group, null, true)} >
                    {<TaskCard data={group}/>}
                  </ListGroupItem>
                )
              })}
          </ListGroup>
        </Card>

        {/*Are you sure return tasks Modal*/}
        <Modal show={this.state.modalGraderShow} onHide={this.modalGraderHandleClose}>
          <Modal.Header closeButton>
            <Modal.Title>Are you sure?</Modal.Title>
          </Modal.Header>
          <Modal.Body>You are returning {(this.state.modalGraderObj != null)? this.state.modalGraderObj.name : null}'s tasks. This action can't be undone</Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={this.modalGraderHandleClose}>
              Close
            </Button>
            <Button variant="primary" onClick={this.modalGraderHandleAccept}>
              Return tasks
            </Button>
          </Modal.Footer>
        </Modal>

        <AssigningModal
          show={this.state.modalAssignShow}
          graders={this.state.graders}
          notAssigned={this.state.notAssigned}
          onClose={this.modalAssignHandleClose}
          onAccept={this.modalAssignHandleAccept}
          currentGrader={this.state.modalAssignGrader}
          taskGroup={this.state.modalAssignTask}
          isFromNotAssigned={this.state.modalAssignIsFromNotAssigned}
          onReturnTask={this.modalAssignHandleReturnTask}
        />

        <EditGradersModal
          show={this.state.modalEditGradersShow}
          activeGraders={this.state.modalEditGradersActiveGraders}
          availableGraders={this.state.modalEditGradersAvailableGraders}
          onClickDeactive={this.modalEditGradersHandleDeactive}
          onClickActive={this.modalEditGradersHandleActive}
          onClose={this.modalEditGradersHandleClose}
          onAccept={this.modalEditGradersHandleAccept}
          hasTask={this.hasTasks}

          showAlert={this.state.modalEditShowAlert}
          alertBody={this.state.modalEditAlertBody}
          closeAlertHandle={this.modalEditGradersHandleCloseAlert}
        />

      </div>
    )
  }
}

export default withRouter(GraderManagement);