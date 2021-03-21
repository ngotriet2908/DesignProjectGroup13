import React, {Component} from "react";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
import {Button, ListGroup, ListGroupItem, Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import {push} from "connected-react-router";
import styles from "./submissions.module.css"
import GroupCard from "../groups/GroupCard";
import TaskCard from "../assign/TaskCard";
import TaskOverviewCard from "./SubmissionsOverviewCard";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import SubmissionsOverviewCard from "./SubmissionsOverviewCard";
import {Link} from "react-router-dom";
import Breadcrumbs from "../helpers/Breadcrumbs";

import globalStyles from '../helpers/global.module.css';
import {IoFileTrayOutline} from "react-icons/io5";
import {IoSyncOutline} from "react-icons/io5";

import classnames from 'classnames';
import Card from "react-bootstrap/Card";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {connect} from "react-redux";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";

class Submissions extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isLoaded: false,

      course: {},
      project: {},
      user: {},
      submissions: [],
      searchString: "",
      filterGroupChoice: "",
      filterAssignedChoice: "",
      syncing: false
    }
  }

  normalizeLowercase(name) {
    return name.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "")
  }
  
  filterGroupSearchChange = (group) => {
    let criteria = this.normalizeLowercase(group.name).includes(this.normalizeLowercase(this.state.searchString))
    if (criteria) return true

    // if (group.hasOwnProperty("sid")) {
    //   criteria = group.sid.normalizeLowercase().includes(this.state.searchString.normalizeLowercase())
    //   if (criteria) return true
    // }
    // if (group.hasOwnProperty("members")) {
    //   let i;
    //   for(i = 0; i < group.members.length; i++) {
    //     criteria = group.members[i].name.normalizeLowercase().includes(this.state.searchString.normalizeLowercase())
    //     if (criteria) return true
    //
    //     criteria = group.members[i].sid.normalizeLowercase().includes(this.state.searchString.normalizeLowercase())
    //     if (criteria) return true
    //   }
    // }
    return false
  }

  filterGroupDropDown = (group) => {
    let filter = this.state.filterGroupChoice
    if (filter === "all") return true;
    if (filter === "group") return group.isGroup;
    if (filter === "individual") return !group.isGroup;
    console.log("dumb filter group error, check immediately")
    return false
  }

  filterAssignedDropDown = (group) => {
    let filter = this.state.filterAssignedChoice
    if (filter === "all") return true;
    if (filter === "yours") return group.hasOwnProperty("grader") && group.grader.id === this.state.user.id;
    if (filter === "not yours") return !group.hasOwnProperty("grader") || group.grader.id !== this.state.user.id;
    console.log("dumb filter assigned error, check immediately")
    return false
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
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions`)
      .then(async response => {
        let data = await response.json();

        this.setState({
          submissions: data.submissions,
          project: data.project,
          course: data.course,
          isLoaded: true,
          user: data.user,
          filterGroupChoice: "all",
          filterAssignedChoice: "all",
          syncing: false,
        })
      })
      .catch(error => {
        console.error(error.message);
        this.setState({
          isLoaded: true,
        })
      });
  }

  syncHandler = () => {
    if (this.state.syncing) {
      return;
    }

    this.setState({
      syncing: true
    })

    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/syncCanvas`)
      .then(response => {
        if (response.status === 200) {
          this.loadData()
        }
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
          <span>
            <IoSyncOutline onClick={this.syncHandler}/>
          </span>
        </div>

        {/*<div className={styles.overview}>*/}
        {/*  <h3>Submissions overview</h3>*/}
        {/*  <h6>Assigned submissions count: {this.state.submissions.length}</h6>*/}
        {/*  <h6>All submissions count: {this.state.submissions.length}</h6>*/}
        {/*  <h6>Deadline: ???</h6>*/}
        {/*  <h6>Overall progress: {this.calculateOverallProgress()}%</h6>*/}
        {/*</div>*/}

        <div className={styles.tasksList}>
          <Card>
            <Card.Body>
              <h3>Submissions List</h3>

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

                  {["all", "divider", "group", "individual"].map((filterS) => {
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

                  {["all", "divider", "yours", "not yours"].map((filterS) => {
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
                        // <div key={submission.stringId} className={styles.ul}>
                        //   {
                        <SubmissionsOverviewCard
                          key={submission.stringId}
                          user={this.state.user}
                          submission={submission}
                          route={this.props.match}/>
                      // }
                        // </div>
                      )
                    })}
              </div>
            </Card.Body>
          </Card>
        </div>

      </div>
    )
  }

}

const actionCreators = {
  setCurrentLocation
}

export default connect(null, actionCreators)(Submissions)