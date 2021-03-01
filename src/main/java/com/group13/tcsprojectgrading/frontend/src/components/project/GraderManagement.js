import styles from "./project.module.css";
import React, {Component} from "react";
import ProjectCard from "../course/ProjectCard";
import GraderCard from "./GraderCard";
// import GraderCard from "./GraderCard";
import {DragDropContext, Droppable, Draggable}  from "react-beautiful-dnd";
import {ListGroup, ListGroupItem} from "react-bootstrap";
import TaskCard from "./TaskCard";
import {notAssignedGroupsData, gradersData} from './GradersData'
import {Button, Card, FormControl} from 'react-bootstrap'

class GraderManagement extends Component {

  constructor(props) {
    super(props)
    this.state = {
      graders : [],
      notAssigned: [],
      // groupsFiltered: [],
      // gradersFiltered: [],
      groupsFilterString: "",
      gradersFilterString: "",
      hideSearch: true
    }

  }

  componentDidMount () {
    console.log("Grader Management mounted.")
    // console.log(this.state.graders)
    this.setState({
      graders : gradersData,
      gradersFiltered : gradersData,
      notAssigned: notAssignedGroupsData,
      groupsFiltered: notAssignedGroupsData,
    })
  }

  handleHideSearch = (event) => {
    this.setState((prevState) => {
      return {hideSearch : !prevState.hideSearch}
    })
  }

  handleSearchChange = (event) => {
    // console.log(event.target.value)
    // let list = [...this.state.notAssigned]
    // // let list = Arrays.from(this.state.grader.groups)
    // // console.log(list)
    // let filteredList = list.filter((group) => {
    //   return group.name.toLowerCase().includes(event.target.value.toLowerCase())
    // })
    // // console.log(filteredList)
    //
    // this.setState({
    //     groupsFiltered: filteredList
    //   }
    // )
    this.setState({
      groupsFilterString : event.target.value
    })
  }

  onGroupClicked = (group) => {
    alert(group.name)
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

  handleReturnTasks = (grader) => {
    let graderOriginalTasks = [...grader.groups]
    console.log(graderOriginalTasks)
    let graderReturnTasks = graderOriginalTasks.filter((group) => {
      return group.progress < 100;
    })
    console.log(graderReturnTasks)
    let graderLeftTasks = graderOriginalTasks.filter((group) => {
      return !this.containsObject(group, graderReturnTasks)
    })
    console.log(graderLeftTasks)
    let notAssignedTasks = [...this.state.notAssigned]
    graderReturnTasks.forEach(tasks => {
      notAssignedTasks.push(tasks)
    })
    console.log(notAssignedTasks)

    let gradersList = [...this.state.graders]
    console.log(gradersList)
    gradersList.forEach((grader1) => {
      if (grader1.id === grader.id) {
        grader1.groups = graderLeftTasks
      }
    })
    console.log(gradersList)
    this.setState({
      notAssigned: notAssignedTasks,
      groupsFiltered: notAssignedTasks,
      graders : gradersList,
      gradersFiltered : gradersList,
    })

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

  render () {
    return (
      <div className={styles.graderManagement}>
        <Card border="secondary" className={styles.GradersCardContainer}>
          <div className={styles.manageTaToolbar}>
            <h3 className={styles.subtitle} >Manage Graders</h3>
            <FormControl className={styles.manageTaSearch}
                         type="text"
                         placeholder="Search for grader or group name"
                         onChange={this.handleGraderSearchChange}/>
          </div>

          <div className={styles.gradersContainer}>
            {/*<h3>Assigned to Graders</h3>*/}
            <div className={styles.gradersListContainer}>
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
                      <GraderCard grader={grader} onReturnClicked={() => this.handleReturnTasks(grader)}/>
                    </li>
                  )
                })}
              </ul>
            </div>
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
                    action onClick={() => this.onGroupClicked(group)} >
                    {<TaskCard data={group}/>}
                  </ListGroupItem>
                )
              })}
            </ListGroup>
          </Card>
      </div>
    )
  }
}

export default GraderManagement;