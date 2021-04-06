import styles from "./assign.module.css";
import React, {Component} from "react";
import {ButtonGroup, Dropdown, DropdownButton, ListGroup, ListGroupItem} from "react-bootstrap";
import {Button, Card, FormControl, Modal, Alert, Spinner, Breadcrumb} from 'react-bootstrap'
import {request} from "../../services/request";
import {BASE, COURSES, PROJECT, USER_COURSES} from "../../services/endpoints";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
import {push} from "connected-react-router";
import BulkAssignModal from "./BulkAssignModal";
import globalStyles from "../helpers/global.module.css";
import Breadcrumbs from "../helpers/Breadcrumbs";
import classnames from "classnames";
import {IoPencilOutline} from "react-icons/io5";
import Grader from "./Grader";
import AssignSubmissionModal from "./AssignSubmissionModal";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import {deleteCurrentCourse, saveCurrentCourse} from "../../redux/courses/actions";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {connect} from "react-redux";
import GradersModal from "./GradersModal";
import LabelModal from "../submissionDetails/labels/LabelModal";
import BulkModal from "./BulkModal";


class GraderManagement extends Component {
  constructor(props) {
    super(props)
    this.state = {
      isLoaded: false,

      graders: [],
      notAssigned: [],

      // graders
      showGradersModal: false,

      // assign
      showAssignModal: false,
      modalAssignSubmission: null,
      modalAssignGrader: null,

      // bulk
      showBulkModal: false,

      // search
      searchQuery: "",

      // //bulk assign tasks modal
      // modalBulkAssignShow: false,
      // modalBulkAssignObj: null,
    }
  }

  componentDidMount () {
    this.props.setCurrentLocation(LOCATIONS.graders);
    this.fetchData();
  }

  fetchData = () => {
    Promise.all([
      request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/graders`),
      request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions?grader=unassigned`),
      request(BASE + "courses/" + this.props.match.params.courseId),
      request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId),
    ])
      .then(async([res1, res2, res3, res4]) => {
        const graders = await res1.json();
        const unassigned = await res2.json();
        const course = await res3.json();
        const project = await res4.json();

        console.log(graders);
        console.log(unassigned);

        this.setState({
          graders: graders,
          unassigned: unassigned,
          course: course,
          project: project,
          isLoaded: true,
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  modalAssignHandleReturnTask = (submission) => {
    // this.showAlertOnScreen(`Return ${this.state.modalAssignTask.name} from ${this.state.modalAssignGrader.name} to Not Assigned`)

    request(`${BASE}${USER_COURSES}/${this.props.match.params.courseId}/${PROJECT}/${this.props.match.params.projectId}/management/assign/${submission.id}/notAssigned`)
      .then(async response => {
        let data = await response.json();

        this.setState({
          graders: data.graders,
          notAssigned: data.notAssigned,

          modalAssignShow: false,
          modalAssignGrader: null,
          modalAssignTask: null,
          modalAssignGraderChoice: null,
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  // search bar

  handleSearchChange = (event) => {
    this.setState({
      searchQuery : event.target.value
    })
  }

  modalBulkAssignHandleShow = () => {
    this.setState({
      modalBulkAssignShow: true
    })
  }

  modalBulkAssignHandleClose = () => {
    this.setState({
      modalBulkAssignShow: false
    })
  }

  modalBulkAssignHandleAccept = (object) => {
    request(`${BASE}${USER_COURSES}/${this.props.match.params.courseId}/${PROJECT}/${this.props.match.params.projectId}/management/bulkAssign`,
      "POST",
      object
    )
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data)

        this.setState({
          modalBulkAssignShow: false,
          graders: data.graders,
          notAssigned: data.notAssigned
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  // graders

  toggleShowGradersModal = () => {
    this.setState(prevState => ({
      showGradersModal: !prevState.showGradersModal,
    }))
  }

  // assign

  toggleShowAssignModal = (submission, currentGrader) => {
    if (this.state.showAssignModal) {
      // modal is displayed

      this.setState({
        showAssignModal: false,
      })
    } else {
      // modal is hidden

      this.setState({
        showAssignModal: true,
        graderAssignModal: currentGrader,
        submissionAssignModal: submission
      })
    }
  }

  // bulk
  toggleShowBulkModal = () => {
    this.setState(prevState => ({
      showBulkModal: !prevState.showBulkModal,
    }))
  }

  render () {
    if (!this.state.isLoaded) {
      return(
        <div className={globalStyles.container}>
          <Spinner className={globalStyles.spinner} animation="border" role="status">
            <span className="sr-only">Loading...</span>
          </Spinner>
        </div>
      )
    }

    return (
      <div className={globalStyles.container}>
        <Breadcrumbs>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>Home</Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id ))}>{this.state.course.name}</Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id + "/projects/"+this.state.project.id))}>{this.state.project.name}</Breadcrumbs.Item>
          <Breadcrumbs.Item active>Graders</Breadcrumbs.Item>
        </Breadcrumbs>

        {/*{(!this.state.alertShow)? null :*/}
        {/*  <Alert variant="success" onClose={() => {this.setState({alertShow:false})}} dismissible>*/}
        {/*    <p>*/}
        {/*      {this.state.alertBody}*/}
        {/*    </p>*/}
        {/*  </Alert>*/}
        {/*}*/}

        <div className={classnames(globalStyles.titleContainer, styles.titleContainer)}>
          <h1>Graders</h1>
          <Button variant="lightGreen" onClick={this.toggleShowGradersModal}><IoPencilOutline size={20}/> Edit graders</Button>
        </div>

        {/*  /!*<Button className={styles.manageTaToolbarButton}*!/*/}
        {/*  /!*  variant="primary" onClick={null}>*!/*/}
        {/*  /!*   Sort*!/*/}
        {/*  /!*</Button>*!/*/}

        <div>
          <h3>Grader List</h3>
        </div>

        <div className={styles.toolbar}>
          <FormControl
            className={styles.groupsSearchBar}
            type="text"
            placeholder="Search by a group or grader's name"
            onChange={this.handleSearchChange}/>

          {/*<DropdownButton*/}
          {/*  as={ButtonGroup}*/}
          {/*  key={"primary"}*/}
          {/*  id={`dropdown-Primary`}*/}
          {/*  variant={"lightGreen"}*/}
          {/*  title={"Group Filter"}*/}
          {/*  onSelect={this.onFilterGroupSelectHandler}*/}
          {/*>*/}

          {/*  {["All", "divider", "Group", "Individual"].map((filterS) => {*/}
          {/*    if (filterS === "divider") {*/}
          {/*      return <Dropdown.Divider key={filterS}/>*/}
          {/*    } else if (filterS === this.state.filterGroupChoice) {*/}
          {/*      return <Dropdown.Item variant="lightGreen" key={filterS} eventKey={filterS} active>{filterS}</Dropdown.Item>*/}
          {/*    } else {*/}
          {/*      return <Dropdown.Item key={filterS} eventKey={filterS}>{filterS}</Dropdown.Item>*/}
          {/*    }*/}
          {/*  })}*/}
          {/*</DropdownButton>*/}

          {/*<DropdownButton*/}
          {/*  as={ButtonGroup}*/}
          {/*  key={"assigned-primary"}*/}
          {/*  id={`assigned-dropdown-Primary`}*/}
          {/*  variant={"lightGreen"}*/}
          {/*  title={"Assigned Filter"}*/}
          {/*  onSelect={this.onFilterAssignedSelectHandler}*/}
          {/*>*/}

          {/*  {["All", "divider", "Yours", "Not yours"].map((filterS) => {*/}
          {/*    if (filterS === "divider") {*/}
          {/*      return <Dropdown.Divider key={filterS}/>*/}
          {/*    } else if (filterS === this.state.filterAssignedChoice) {*/}
          {/*      return <Dropdown.Item key={filterS} eventKey={filterS} active>{filterS}</Dropdown.Item>*/}
          {/*    } else {*/}
          {/*      return <Dropdown.Item key={filterS} eventKey={filterS}>{filterS}</Dropdown.Item>*/}
          {/*    }*/}
          {/*  })}*/}
          {/*</DropdownButton>*/}
        </div>


        {/* assigned */}
        <div className={styles.gradersContainer}>
          {this.state.graders
            .filter((grader) => {
              let filterString = this.state.searchQuery.toLowerCase()
              return (grader.name.toLowerCase().includes(filterString)
                || grader.submissions.reduce(((result, submission) => result || submission.name.toLowerCase().includes(filterString)), false))
            })
            .map(grader => {
              return (
                <Grader
                  key={grader.id}
                  grader={grader}
                  name={grader.name}
                  submissions={grader.submissions}
                  toggleShow={this.toggleShowAssignModal}
                />
              )
            })}
        </div>

        {/* not assigned */}
        <div className={styles.notAssignedContainer}>
          <Grader
            grader={null}
            name={"Not assigned"}
            submissions={this.state.unassigned}
            toggleShow={this.toggleShowAssignModal}
            toggleShowBulk={this.toggleShowBulkModal}
          />
        </div>

        {/*<Modal show={this.state.modalGraderShow} onHide={this.modalGraderHandleClose}>*/}
        {/*  <Modal.Header closeButton>*/}
        {/*    <Modal.Title>Are you sure?</Modal.Title>*/}
        {/*  </Modal.Header>*/}
        {/*  <Modal.Body>You are returning {(this.state.modalGraderObj != null)? this.state.modalGraderObj.name : null}'s tasks. This action can't be undone</Modal.Body>*/}
        {/*  <Modal.Footer>*/}
        {/*    <Button variant="secondary" onClick={this.modalGraderHandleClose}>*/}
        {/*      Close*/}
        {/*    </Button>*/}
        {/*    <Button variant="primary" onClick={this.modalGraderHandleAccept}>*/}
        {/*      Return tasks*/}
        {/*    </Button>*/}
        {/*  </Modal.Footer>*/}
        {/*</Modal>*/}

        {/* assign submissions modal */}
        <AssignSubmissionModal
          show={this.state.showAssignModal}
          toggleShow={() => this.toggleShowAssignModal(null, null)}
          routeParams={this.props.match.params}
          graders={this.state.graders}
          currentGrader={this.state.graderAssignModal}
          submission={this.state.submissionAssignModal}
          reloadPage={() => {
            this.setState({
              isLoaded: false,
            })
            this.fetchData()
          }}
        />

        <GradersModal
          show={this.state.showGradersModal}
          toggleShow={this.toggleShowGradersModal}
          routeParams={this.props.match.params}
          currentGraders={this.state.graders}
          reloadPage={() => {
            this.setState({
              isLoaded: false,
            })
            this.fetchData()
          }}
        />

        <BulkModal
          show={this.state.showBulkModal}
          toggleShow={this.toggleShowBulkModal}
          graders={this.state.graders}
          submissions={this.state.notAssigned}
          // onClose={this.modalBulkAssignHandleClose}
          // onAccept={this.modalBulkAssignHandleAccept}
        />

        {/*<BulkAssignModal*/}
        {/*  show={this.state.modalBulkAssignShow}*/}
        {/*  graders={this.state.graders}*/}
        {/*  notAssigned={this.state.notAssigned}*/}
        {/*  onClose={this.modalBulkAssignHandleClose}*/}
        {/*  onAccept={this.modalBulkAssignHandleAccept}*/}
        {/*/>*/}

      </div>
    )
  }
}

const actionCreators = {
  setCurrentLocation
}

export default connect(null, actionCreators)(GraderManagement)