import React, {Component} from "react";
import styles from "./students.module.css";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {Breadcrumb, Button, ListGroup, ListGroupItem, Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import {URL_PREFIX} from "../../services/config";
import GroupCard from "../groups/GroupCard";
import ParticipantCard from "./StudentCard";
import {Link} from "react-router-dom";
import {deleteRubric, saveRubric} from "../../redux/rubric/actions";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {connect} from "react-redux";
import globalStyles from "../helpers/global.module.css";
import Breadcrumbs from "../helpers/Breadcrumbs";
import store from "../../redux/store";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import classnames from 'classnames';
import StudentCard from "./StudentCard";


class Students extends Component {

  constructor(props) {
    super(props);
    this.state = {
      participants: [],
      project: {},
      course: {},
      isLoading: true,
      filterChoice: "all",
      searchString: "",
    }
  }

  componentDidMount() {
    this.props.setCurrentLocation(LOCATIONS.students);

    this.setState({
      isLoading: true
    })
    Promise.all([
      request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/participants`),
      request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}`),
      request(BASE + "courses/" + this.props.match.params.courseId)]
    )
      .then(async ([res1, res2, res3]) => {
        const participants = await res1.json();
        const project = await res2.json();
        const course = await res3.json();
        this.setState({
          project: project,
          participants: participants,
          course: course,
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

  render() {
    if (this.state.isLoading) {
      return (
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
          <Breadcrumbs.Item
            onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id))}>{this.state.course.name}</Breadcrumbs.Item>
          <Breadcrumbs.Item
            onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id + "/projects/" + this.state.project.id))}>{this.state.project.name}</Breadcrumbs.Item>
          <Breadcrumbs.Item active>Participants</Breadcrumbs.Item>
        </Breadcrumbs>

        <div
          className={classnames(globalStyles.titleContainer, styles.titleContainer, this.state.syncing && styles.titleContainerIconActive)}>
          <h1>Participants</h1>
        </div>

        <div className={styles.participantContainer}>
          <div className={classnames(styles.sectionTitle, styles.sectionTitleWithButton)}>
            <h3 className={styles.sectionTitleH}>Participant List</h3>
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
              variant={"lightGreen"}
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
          <div className={styles.participantsContainer}>
            {
              this.state.participants
                .filter((participant) => {
                  return this.filterParticipantDropDown(participant.id.user) && this.filterParticipantSearchChange(participant.id.user);
                })
                // .sort((group1, group2) => {
                //   // console.log(this.compareFunction(group1, group2, ["name"]))
                //   return this.compareFunction(group1, group2,
                //     [{criterion: "isGroup", order: false}, {criterion: "name", order: true}])
                // })
                .map((participant) => {
                  return (
                    <StudentCard
                      key={participant.id.user.id}
                      match={this.props.match}
                      participant={participant}/>
                  )
                })}
          </div>
        </div>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
  };
};

const actionCreators = {
  setCurrentLocation
}

export default connect(mapStateToProps, actionCreators)(Students)