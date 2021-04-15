import React, {Component} from "react";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
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
import CircularProgress from "@material-ui/core/CircularProgress";
import StickyHeader from "../helpers/StickyHeader";
import {Can} from "../permissions/CoursePageAbility";
import Button from "@material-ui/core/Button";
import SyncIcon from "@material-ui/icons/Sync";
import TextField from "@material-ui/core/TextField";
import InputAdornment from "@material-ui/core/InputAdornment";
import SearchIcon from "@material-ui/icons/Search";
import Filter from "../helpers/Filter";


class Submissions extends Component {
  constructor(props) {
    super(props);

    this.assignedFilterOptions = ["All", "Yours", "Not yours"];

    let filterAssignedChoice = 0;
    if (this.props.location.state && this.props.location.state.status) {
      filterAssignedChoice = this.props.location.state.status
    }

    this.state = {
      isLoaded: false,

      course: {},
      submissions: [],

      searchString: "",

      // filterGroupChoice: "All",
      filterAssignedChoice: this.assignedFilterOptions[filterAssignedChoice],

      syncing: false
    }
  }

  normalizeLowercase(name) {
    return name.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "")
  }
  
  filterGroupSearchChange = (group) => {
    let searchString = this.state.searchString;

    let criteria = this.normalizeLowercase(group.name).includes(this.normalizeLowercase(searchString));

    let memberCriteria = group.members.reduce(
      (cur, member) => {
        return (this.normalizeLowercase(member.name).includes(this.normalizeLowercase(searchString)) ||
          this.normalizeLowercase(member.sNumber).includes(this.normalizeLowercase(searchString)) ||
          cur)
      }, false)
    return (criteria || memberCriteria);
  }

  // filterGroupDropDown = (group) => {
  //   let filter = this.state.filterGroupChoice;
  //   if (filter === "All") return true;
  //   if (filter === "Group") return group.groupId != null;
  //   if (filter === "Individual") return group.groupId == null;
  //   return false
  // }

  filterAssignedDropDown = (submission) => {
    let filter = this.state.filterAssignedChoice;

    switch (filter) {
    case "All":
      return true;
    case "Yours":
      return submission.grader != null && submission.grader.id === this.props.user.id;
    case "Not yours":
      return submission.grader == null || submission.grader.id !== this.props.user.id;
    default:
      return false;
    }
  }

  handleSearchChange = (event) => {
    this.setState({
      searchString: event.target.value
    })
  }

  // onFilterGroupSelectHandler = (eventKey, event) => {
  //   this.setState({
  //     filterGroupChoice: eventKey
  //   })
  // }

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
      return (
        <div className={globalStyles.screenContainer}>
          <CircularProgress className={globalStyles.spinner}/>
        </div>
      )
    }

    return(
      <>
        <Breadcrumbs>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>Home</Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id ))}>{this.state.course.name}</Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id + "/projects/"+this.state.project.id))}>{this.state.project.name}</Breadcrumbs.Item>
          <Breadcrumbs.Item active>Submissions</Breadcrumbs.Item>
        </Breadcrumbs>

        <StickyHeader
          title="Submissions"
        />

        <div className={globalStyles.innerScreenContainer}>

          {/*<div className={styles.overview}>*/}
          {/*  <h3>Submissions overview</h3>*/}
          {/*  <h6>Assigned submissions count: {this.state.submissions.length}</h6>*/}
          {/*  <h6>All submissions count: {this.state.submissions.length}</h6>*/}
          {/*  <h6>Deadline: ???</h6>*/}
          {/*  <h6>Overall progress: {this.calculateOverallProgress()}%</h6>*/}
          {/*</div>*/}

          <div className={styles.toolbar}>
            <TextField
              id="outlined-search"
              placeholder="Search by student name or student number"
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

            <Filter
              options={this.assignedFilterOptions}
              selected={this.state.filterAssignedChoice}
              setSelected={(value) => {this.setState({filterAssignedChoice: value})}}
              label="Assigned"
              className={styles.filter}
            />

          </div>

          {/*  <DropdownButton*/}
          {/*    as={ButtonGroup}*/}
          {/*    key={"primary"}*/}
          {/*    id={`dropdown-Primary`}*/}
          {/*    variant={"lightGreen"}*/}
          {/*    title={"Group Filter"}*/}
          {/*    onSelect={this.onFilterGroupSelectHandler}*/}
          {/*  >*/}

          <div className={styles.submissionContainer}>
            {this.state.submissions
              .filter((group) => {
                return(
                // this.filterGroupDropDown(group) &&
                  this.filterAssignedDropDown(group) &&
                  this.filterGroupSearchChange(group)
                );
              })
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
      </>
    )
  }
}

const actionCreators = {
  setCurrentLocation
}

const mapStateToProps = state => {
  return {
    user: state.users.self,
  };
};

export default connect(mapStateToProps, actionCreators)(Submissions)