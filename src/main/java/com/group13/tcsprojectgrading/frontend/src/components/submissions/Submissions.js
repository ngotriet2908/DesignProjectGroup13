import React, {Component} from "react";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
import {Breadcrumb, Button, ListGroup, ListGroupItem, Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import {push} from "connected-react-router";
import styles from "./submissions.module.css"
import GroupCard from "../groups/GroupCard";
import TaskCard from "../assign/TaskCard";
import TaskOverviewCard from "./SubmissionsOverviewCard";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import SubmissionsOverviewCard from "./SubmissionsOverviewCard";
import {Link} from "react-router-dom";
import globalStyles from "../helpers/global.module.css";

class Submissions extends Component {
  constructor(props) {
    super(props);
    this.state = {
      course: {},
      project: {},
      user: {},
      isLoading: true,
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
    this.setState({
      isLoading: true
    })
    this.startThePage();
  }

  startThePage() {
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions`)
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.setState({
          submissions: data.submissions,
          project: data.project,
          course: data.course,
          isLoading: false,
          user: data.user,
          filterGroupChoice: "all",
          filterAssignedChoice: "all",
          syncing: false,
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  syncHandler = () => {
    this.setState({
      syncing: true
    })

    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/syncCanvas`)
      .then(response => {
        if (response.status === 200) {
          this.startThePage()
        }
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  render() {
    return(
      (this.state.isLoading)?
        <Spinner className={styles.spinner} animation="border" role="status">
          <span className="sr-only">Loading...</span>
        </Spinner>
        :
        <div className={styles.container}>
          <Breadcrumb>
            <Breadcrumb.Item onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>Home</Breadcrumb.Item>
            <Breadcrumb.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id ))}>
              {this.state.course.name}
            </Breadcrumb.Item>
            <Breadcrumb.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id + "/projects/"+this.state.project.id))}>
              {this.state.project.name}
            </Breadcrumb.Item>
            <Breadcrumb.Item active>
              Submissions
            </Breadcrumb.Item>
          </Breadcrumb>

          <div className={styles.header}>
            {/*<h2>{this.state.project.name} tasks</h2>*/}
          </div>

          <div className={styles.overview}>
            <h3>Submissions overview</h3>
            <h6>Assigned submissions count: {this.state.submissions.length}</h6>
            <h6>All submissions count: {this.state.submissions.length}</h6>
            <h6>Deadline: ???</h6>
            <h6>Overall progress: {this.calculateOverallProgress()}%</h6>
            <Button variant="lightGreen" onClick={this.syncHandler}>
              {(!this.state.syncing)? "Sync with Canvas":
                <Spinner
                  as="span"
                  animation="grow"
                  size="sm"
                  role="status"
                  aria-hidden="true"/>
              }
            </Button>
          </div>

          <div className={styles.tasksList}>
            <h3>Submissions List</h3>
            <div className={styles.toolbar}>
              <FormControl className={styles.groupsSearchBar}
                           type="text"
                           placeholder="Search with group name"
                           onChange={this.handleSearchChange}/>

              <DropdownButton
                as={ButtonGroup}
                key={"primary"}
                id={`dropdown-Primary`}
                variant={"primary"}
                title={"Group Filter"}
                onSelect={this.onFilterGroupSelectHandler}
              >

                {["all", "divider", "group", "individual"].map((filterS) => {
                  if (filterS === "divider") {
                    return <Dropdown.Divider key={filterS}/>
                  } else if (filterS === this.state.filterGroupChoice) {
                    return <Dropdown.Item key={filterS} eventKey={filterS} active>{filterS}</Dropdown.Item>
                  } else {
                    return <Dropdown.Item key={filterS} eventKey={filterS}>{filterS}</Dropdown.Item>
                  }
                })}
              </DropdownButton>

              <DropdownButton
                as={ButtonGroup}
                key={"assigned-primary"}
                id={`assigned-dropdown-Primary`}
                variant={"primary"}
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

            <li className={styles.tasksContainer}>
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
                      <ul key={submission.stringId} className={styles.ul}>
                        {<SubmissionsOverviewCard user={this.state.user} submission={submission} route={this.props.match}/>}
                      </ul>
                    )
                  })}
            </li>
          </div>

        </div>
    )
  }

}

export default Submissions;