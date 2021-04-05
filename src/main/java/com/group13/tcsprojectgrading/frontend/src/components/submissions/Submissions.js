import React, {Component} from "react";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
import {Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import {push} from "connected-react-router";
import styles from "./submissions.module.css"
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import SubmissionsOverviewCard from "./SubmissionsOverviewCard";
import Breadcrumbs from "../helpers/Breadcrumbs";

import globalStyles from '../helpers/global.module.css';

import classnames from 'classnames';
import {setCurrentLocation} from "../../redux/navigation/actions";
import {connect} from "react-redux";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";

class Submissions extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isLoaded: false,

      course: {},
      submissions: [],
      searchString: "",
      filterGroupChoice: "All",
      filterAssignedChoice: "All",
      syncing: false
    }
  }

  normalizeLowercase(name) {
    return name.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "")
  }
  
  filterGroupSearchChange = (group) => {
    let criteria = this.normalizeLowercase(group.name).includes(this.normalizeLowercase(this.state.searchString))
    return !!criteria;
  }

  filterGroupDropDown = (group) => {
    let filter = this.state.filterGroupChoice;
    if (filter === "All") return true;
    if (filter === "Group") return group.groupId != null;
    if (filter === "Individual") return group.groupId == null;
    return false
  }

  filterAssignedDropDown = (group) => {
    let filter = this.state.filterAssignedChoice;

    switch (filter) {
    case "All":
      return true;
    case "Yours":
      return group.grader != null && group.grader.id === this.props.user.id;
    case "Not yours":
      return group.grader == null || group.grader.id !== this.props.user.id;
    default:
      return false;
    }
  }

  handleSearchChange = (event) => {
    this.setState({
      searchString: event.target.value
    })
  }

  onFilterGroupSelectHandler = (eventKey, event) => {
    this.setState({
      filterGroupChoice: eventKey
    })
  }

  onFilterAssignedSelectHandler = (eventKey, event) => {
    this.setState({
      filterAssignedChoice: eventKey
    })
  }

  calculateOverallProgress = () => {
    let i;
    let avg = 0;
    for(i = 0; i < this.state.submissions.length; i++) {
      avg += this.state.submissions[i].progress
    }
    return Math.round(avg/this.state.submissions.length)
  }

  componentDidMount() {
    this.props.setCurrentLocation(LOCATIONS.submissions);
    this.loadData();
  }

  loadData() {
    Promise.all([
      request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions`),
      request(BASE + "courses/" + this.props.match.params.courseId),
      request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId),
    ])
      .then(async([res1, res2, res3]) => {
        const submissions = await res1.json();
        const course = await res2.json();
        const project = await res3.json();

        this.setState({
          course: course,
          project: project,
          submissions: submissions,
          isLoaded: true,
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  render() {
    if (!this.state.isLoaded) {
      return(
        <div className={globalStyles.container}>
          <Spinner className={globalStyles.spinner} animation="border" role="status">
            <span className="sr-only">Loading...</span>
          </Spinner>
        </div>
      )
    }

    return(
      <div className={globalStyles.container}>
        <Breadcrumbs>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>Home</Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id ))}>{this.state.course.name}</Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id + "/projects/"+this.state.project.id))}>{this.state.project.name}</Breadcrumbs.Item>
          <Breadcrumbs.Item active>Submissions</Breadcrumbs.Item>
        </Breadcrumbs>

        <div className={classnames(globalStyles.titleContainer, styles.titleContainer, this.state.syncing && styles.titleContainerIconActive)}>
          <h1>Submissions</h1>
        </div>

        {/*<div className={styles.overview}>*/}
        {/*  <h3>Submissions overview</h3>*/}
        {/*  <h6>Assigned submissions count: {this.state.submissions.length}</h6>*/}
        {/*  <h6>All submissions count: {this.state.submissions.length}</h6>*/}
        {/*  <h6>Deadline: ???</h6>*/}
        {/*  <h6>Overall progress: {this.calculateOverallProgress()}%</h6>*/}
        {/*</div>*/}

        <div className={styles.submissionContainer}>
          <div>
            <h3>Submission List</h3>
          </div>

          <div className={styles.toolbar}>
            <FormControl className={styles.groupsSearchBar}
              type="text"
              placeholder="Search by a group name"
              onChange={this.handleSearchChange}/>

            <DropdownButton
              as={ButtonGroup}
              key={"primary"}
              id={`dropdown-Primary`}
              variant={"lightGreen"}
              title={"Group Filter"}
              onSelect={this.onFilterGroupSelectHandler}
            >

              {["All", "divider", "Group", "Individual"].map((filterS) => {
                if (filterS === "divider") {
                  return <Dropdown.Divider key={filterS}/>
                } else if (filterS === this.state.filterGroupChoice) {
                  return <Dropdown.Item variant="lightGreen" key={filterS} eventKey={filterS} active>{filterS}</Dropdown.Item>
                } else {
                  return <Dropdown.Item key={filterS} eventKey={filterS}>{filterS}</Dropdown.Item>
                }
              })}
            </DropdownButton>

            <DropdownButton
              as={ButtonGroup}
              key={"assigned-primary"}
              id={`assigned-dropdown-Primary`}
              variant={"lightGreen"}
              title={"Assigned Filter"}
              onSelect={this.onFilterAssignedSelectHandler}
            >

              {["All", "divider", "Yours", "Not yours"].map((filterS) => {
                if (filterS === "divider") {
                  return <Dropdown.Divider key={filterS}/>
                } else if (filterS === this.state.filterAssignedChoice) {
                  return <Dropdown.Item key={filterS} eventKey={filterS} active>{filterS}</Dropdown.Item>
                } else {
                  return <Dropdown.Item key={filterS} eventKey={filterS}>{filterS}</Dropdown.Item>
                }
              })}
            </DropdownButton>
          </div>

          <div className={styles.tasksContainer}>
            {
              this.state.submissions
                .filter((group) => {
                  return this.filterGroupDropDown(group) &&
                      this.filterAssignedDropDown(group) &&
                      this.filterGroupSearchChange(group);
                })
              // .sort((group1, group2) => {
              //   // console.log(this.compareFunction(group1, group2, ["name"]))
              //   return this.compareFunction(group1, group2,
              //     [{criterion: "isGroup", order: false}, {criterion: "name", order: true}])
              // })
                .map((submission) => {
                  return (
                    <SubmissionsOverviewCard
                      key={submission.id}
                      submission={submission}
                      routeParams={this.props.match.params}
                      user={this.props.user}
                    />
                  )
                })}
          </div>
        </div>

      </div>
    )
  }

}

const actionCreators = {
  setCurrentLocation
}

const mapStateToProps = state => {
  return {
    user: state.users.self
  };
};

export default connect(mapStateToProps, actionCreators)(Submissions)