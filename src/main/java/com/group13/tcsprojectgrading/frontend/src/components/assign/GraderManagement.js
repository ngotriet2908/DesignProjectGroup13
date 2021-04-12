import styles from "./assign.module.css";
import React, {Component} from "react";
import {request} from "../../services/request";
import {BASE, PROJECT, USER_COURSES} from "../../services/endpoints";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
import {push} from "connected-react-router";
import globalStyles from "../helpers/global.module.css";
import Breadcrumbs from "../helpers/Breadcrumbs";
import classnames from "classnames";
import Grader from "./Grader";
import AssignSubmissionModal from "./AssignSubmissionModal";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {connect} from "react-redux";
import GradersModal from "./GradersModal";
import BulkModal from "./BulkModal";
import {Can, ability, updateAbility} from "../permissions/ProjectAbility";
import CircularProgress from "@material-ui/core/CircularProgress";
import StickyHeader from "../helpers/StickyHeader";
import Button from "@material-ui/core/Button";
import SyncIcon from "@material-ui/icons/Sync";

import EditIcon from '@material-ui/icons/Edit';
import TextField from "@material-ui/core/TextField";
import InputAdornment from "@material-ui/core/InputAdornment";
import SearchIcon from "@material-ui/icons/Search";
import Filter from "../helpers/Filter";


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

        if (project.privileges !== null) {
          updateAbility(ability, project.privileges, this.props.user)
          console.log(ability)
          // console.log(ability.can('view',"AdminToolbar"))
          // console.log(ability.can('read',"Submissions"))
        } else {
          console.log("No privileges found.")
        }

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
      return (
        <div className={globalStyles.screenContainer}>
          <CircularProgress className={globalStyles.spinner}/>
        </div>
      )
    }

    return (
      <>
        <Breadcrumbs>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>Home</Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id ))}>{this.state.course.name}</Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id + "/projects/"+this.state.project.id))}>{this.state.project.name}</Breadcrumbs.Item>
          <Breadcrumbs.Item active>Graders</Breadcrumbs.Item>
        </Breadcrumbs>

        <StickyHeader
          title={"Graders"}
          buttons={
            <Can I="edit" a="ManageGraders">
              <Button
                variant="contained"
                color="primary"
                className={globalStyles.titleActiveButton}
                onClick={this.toggleShowGradersModal}
                startIcon={<EditIcon/>}
                disableElevation
              >
                Edit graders
              </Button>
            </Can>
          }
        />

        <div className={globalStyles.innerScreenContainer}>

          <div className={styles.toolbar}>
            <TextField
              id="outlined-search"
              placeholder="Search by student name, group name or grader's name"
              type="search"
              variant="outlined"
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon style={{color: "gray"}} />
                  </InputAdornment>
                ),
              }}
              onChange={this.handleSearchChange}
              fullWidth
            />

            {/*<Filter*/}
            {/*  options={this.assignedFilterOptions}*/}
            {/*  selected={this.state.filterAssignedChoice}*/}
            {/*  setSelected={(value) => {this.setState({filterAssignedChoice: value})}}*/}
            {/*  label="Assigned"*/}
            {/*  className={styles.filter}*/}
            {/*/>*/}
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
                    routeParams={this.props.match.params}
                    user={this.props.user}
                    reloadPage={() => {
                      this.setState({
                        isLoaded: false,
                      })
                      this.fetchData()
                    }}
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
              routeParams={this.props.match.params}
              user={this.props.user}
              reloadPage={() => {
                this.setState({
                  isLoaded: false,
                })
                this.fetchData()
              }}
            />
          </div>
        </div>

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
      </>
    )
  }
}

const mapStateToProps = state => {
  return {
    user: state.users.self
  };
};


const actionCreators = {
  setCurrentLocation
}

export default connect(mapStateToProps, actionCreators)(GraderManagement)