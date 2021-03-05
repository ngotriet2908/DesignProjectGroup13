import React, {Component} from "react";
import styles from "./groups.module.css";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {Breadcrumb, Button, ListGroup, ListGroupItem, Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import GroupCard from "./GroupCard";


class Groups extends Component {

  constructor(props) {
    super(props);
    this.state = {
      groups: [],
      project: {},
      isLoading: true,
      filterChoice: "all",
      searchString: "",
    }
  }

  componentDidMount() {
    this.setState({
      isLoading: true
    })
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/groups`)
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.setState({
          groups: data.groups,
          project: data.project,
          isLoading: false
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  onFilterSelectHandler = (eventKey, event) => {
    this.setState({
      filterChoice: eventKey
    })
  }

  filterGroupDropDown = (group) => {
    let filter = this.state.filterChoice
    if (filter === "all") return true;
    if (filter === "group") return group.isGroup;
    if (filter === "individual") return !group.isGroup;
    console.log("dumb filter error, check immediately")
    return false
  }

  filterGroupSearchChange = (group) => {
    let criteria = group.name.toLowerCase().includes(this.state.searchString.toLowerCase())
    if (criteria) return true

    if (group.hasOwnProperty("sid")) {
      criteria = group.sid.toLowerCase().includes(this.state.searchString.toLowerCase())
      if (criteria) return true
    }
    if (group.hasOwnProperty("members")) {
      let i;
      for(i = 0; i < group.members.length; i++) {
        criteria = group.members[i].name.toLowerCase().includes(this.state.searchString.toLowerCase())
        if (criteria) return true

        criteria = group.members[i].sid.toLowerCase().includes(this.state.searchString.toLowerCase())
        if (criteria) return true
      }
    }
    return false
  }

  handleSearchChange = (event) => {
    this.setState({
      searchString: event.target.value
    })
  }

  compareFunction = (object1, object2, criteria) => {
    let i;
    let result = 0;
    // console.log()
    for(i = 0; i < criteria.length; i++) {
      let criterion = criteria[i].criterion
      let order = criteria[i].order
      let a = object1[criterion]
      let b = object2[criterion]
      if (typeof a === "boolean") {
        a = a.toLocaleString()
      }
      if (typeof b === "boolean") {
        b = b.toLocaleString()
      }
      // console.log(a,b)
      if (order) {
        if (a.localeCompare(b) !== 0) {
          return a.localeCompare(b)
        } else {
          result = a.localeCompare(b);
        }
      } else {
        if (b.localeCompare(a) !== 0) {
          return b.localeCompare(a)
        } else {
          result = b.localeCompare(a);
        }
      }

    }
    return result;
  }

  render () {
    return (
      (this.state.isLoading)?
        <Spinner className={styles.spinner} animation="border" role="status">
          <span className="sr-only">Loading...</span>
        </Spinner>
        :
      <div className={styles.container}>
        <div className={styles.header}>
          <h2>{(this.state.project != null)? this.state.project.name : null} groups</h2>
        </div>
        <div className={styles.toolbar}>
          <FormControl className={styles.groupsSearchBar}
                       type="text"
                       placeholder="Search with group name, student name, student id, member name, member student id"
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
        <li className={styles.groupsContainer}>
          {
            this.state.groups
              .filter((group) => {
                return this.filterGroupDropDown(group) && this.filterGroupSearchChange(group);
              })
              .sort((group1, group2) => {
                // console.log(this.compareFunction(group1, group2, ["name"]))
                return this.compareFunction(group1, group2,
                  [{criterion: "isGroup", order: false}, {criterion: "name", order: true}])
              })
              .map((group) => {
                return (
                  <ul key={group.id} className={styles.ul}>
                    {<GroupCard group={group}/>}
                  </ul>
                )
              })}
        </li>
      </div>
    )
  }
}

export default Groups;