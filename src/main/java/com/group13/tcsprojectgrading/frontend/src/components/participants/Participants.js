import React, {Component} from "react";
import styles from "./participants.module.css";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {Breadcrumb, Button, ListGroup, ListGroupItem, Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import {URL_PREFIX} from "../../services/config";
import GroupCard from "../groups/GroupCard";
import ParticipantCard from "./ParticipantCard";
import {Link} from "react-router-dom";
import {deleteRubric, saveRubric} from "../../redux/rubric/actions";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {connect} from "react-redux";
class Participants extends Component {

  constructor(props) {
    super(props);
    this.state = {
      participants: [],
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
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/participants`)
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.setState({
          participants: data.participants,
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

  filterParticipantDropDown = (participant) => {
    let filter = this.state.filterChoice
    if (filter === "all") return true;
    // if (filter === "group") return group.isGroup;
    // if (filter === "individual") return !group.isGroup;
    console.log("dumb filter error, check immediately")
    return false
  }

  filterParticipantSearchChange = (participant) => {
    let criteria = participant.name.toLowerCase().includes(this.state.searchString.toLowerCase())
    if (criteria) return true

    if (participant.hasOwnProperty("sid")) {
      criteria = participant.sid.toLowerCase().includes(this.state.searchString.toLowerCase())
      if (criteria) return true
    }
    // if (group.hasOwnProperty("members")) {
    //   let i;
    //   for(i = 0; i < group.members.length; i++) {
    //     criteria = group.members[i].name.toLowerCase().includes(this.state.searchString.toLowerCase())
    //     if (criteria) return true
    //
    //     criteria = group.members[i].sid.toLowerCase().includes(this.state.searchString.toLowerCase())
    //     if (criteria) return true
    //   }
    // }
    return false
  }

  handleSearchChange = (event) => {
    this.setState({
      searchString: event.target.value
    })
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
            <h2>{this.state.project.name} participants</h2>
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
          <ListGroup className={styles.participantsContainer}>
            {
              this.state.participants
                .filter((participant) => {
                  return this.filterParticipantDropDown(participant) && this.filterParticipantSearchChange(participant);
                })
                // .sort((group1, group2) => {
                //   // console.log(this.compareFunction(group1, group2, ["name"]))
                //   return this.compareFunction(group1, group2,
                //     [{criterion: "isGroup", order: false}, {criterion: "name", order: true}])
                // })
                .map((participant) => {
                  return (
                    <ListGroupItem key={participant.id} className={styles.ul}>
                      {<ParticipantCard match={this.props.match} participant={participant}/>}
                    </ListGroupItem>
                  )
                })}
          </ListGroup>
        </div>
    )
  }

}

const mapStateToProps = state => {
  return {
  };
};

const actionCreators = {

}

export default connect(mapStateToProps, actionCreators)(Participants)