import React, {Component} from "react";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
import {Breadcrumb, Button, ListGroup, ListGroupItem, Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import {push} from "connected-react-router";
import styles from "./tasks.module.css"
import GroupCard from "../groups/GroupCard";
import TaskCard from "../assign/TaskCard";
import TaskOverviewCard from "./TaskOverviewCard";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";

class Tasks extends Component {
  constructor(props) {
    super(props);
    this.state = {
      course: {},
      project: {},
      isLoading: true,
      tasks: []
    }
  }

  handleSearchChange = (event) => {
    this.setState({
      searchString: event.target.value
    })
  }

  onFilterSelectHandler = (eventKey, event) => {
    this.setState({
      filterChoice: eventKey
    })
  }

  calculateOverallProgress = () => {
    let i;
    let avg = 0;
    for(i = 0; i < this.state.tasks.length; i++) {
      avg += this.state.tasks[i].progress
    }
    return Math.round(avg/this.state.tasks.length)
  }

  componentDidMount() {
    this.setState({
      isLoading: true
    })
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/tasks`)
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.setState({
          tasks: data.tasks,
          project: data.project,
          course: data.course,
          isLoading: false
        })
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
            Tasks
          </Breadcrumb.Item>
        </Breadcrumb>

        <div className={styles.header}>
          {/*<h2>{this.state.project.name} tasks</h2>*/}
        </div>

        <div className={styles.overview}>
          <h3>Tasks overview</h3>
          <h6>Tasks count: {this.state.tasks.length}</h6>
          <h6>Deadline: ???</h6>
          <h6>Overall progress: {this.calculateOverallProgress()}%</h6>
        </div>

        <div className={styles.tasksList}>
          <h3>Tasks List</h3>
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
              title={"Filter"}
              onSelect={this.onFilterSelectHandler}
            >

              {["all", "divider", "group", "individual"].map((filterS) => {
                if (filterS === "divider") {
                  return <Dropdown.Divider key={filterS}/>
                } else if (filterS === this.state.filterChoice) {
                  return <Dropdown.Item key={filterS} eventKey={filterS} active>{filterS}</Dropdown.Item>
                } else {
                  return <Dropdown.Item key={filterS} eventKey={filterS}>{filterS}</Dropdown.Item>
                }
              })}
            </DropdownButton>
          </div>

          <li className={styles.tasksContainer}>
            {
              this.state.tasks
                // .filter((group) => {
                //   return this.filterGroupDropDown(group) && this.filterGroupSearchChange(group);
                // })
                // .sort((group1, group2) => {
                //   // console.log(this.compareFunction(group1, group2, ["name"]))
                //   return this.compareFunction(group1, group2,
                //     [{criterion: "isGroup", order: false}, {criterion: "name", order: true}])
                // })
                .map((task) => {
                  return (
                    <ul key={task.stringId} className={styles.ul}>
                      {<TaskOverviewCard task={task} route={this.props.match}/>}
                    </ul>
                  )
                })}
          </li>
        </div>

      </div>
    )
  }
}

export default Tasks