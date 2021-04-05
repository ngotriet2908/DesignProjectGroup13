import React, {Component} from "react";
import styles from "./students.module.css";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import StudentCard from "./StudentCard";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {connect} from "react-redux";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import globalStyles from "../helpers/global.module.css";
import Breadcrumbs from "../helpers/Breadcrumbs";
import classnames from "classnames";
class Students extends Component {

  constructor(props) {
    super(props);
    this.state = {
      participants: [],
      project: {},
      isLoaded: false,
      filterChoice: "All",
      searchString: "",
    }
  }

  componentDidMount() {
    this.props.setCurrentLocation(LOCATIONS.submissions);

    Promise.all([
      request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/participants`),
      request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}`),
      request(BASE + "courses/" + this.props.match.params.courseId),
    ])
      .then(async ([res1, res2, res3]) =>  {
        const students = await res1.json();
        const project = await res2.json();
        const course = await res3.json();

        this.setState({
          course: course,
          project: project,
          students: students,
          isLoaded: true,
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  onFilterSelectHandler = (eventKey) => {
    this.setState({
      filterChoice: eventKey
    })
  }

  filterParticipantDropDown = () => {
    let filter = this.state.filterChoice
    if (filter === "All") return true;
    // if (filter === "group") return group.isGroup;
    // if (filter === "individual") return !group.isGroup;
    return false
  }

  filterParticipantSearchChange = (student) => {
    let criteria = student.name.toLowerCase().includes(this.state.searchString.toLowerCase())
    if (criteria) return true

    if (student.hasOwnProperty("sid")) {
      criteria = student.sid.toLowerCase().includes(this.state.searchString.toLowerCase())
      if (criteria) return true
    }

    return false
  }

  handleSearchChange = (event) => {
    this.setState({
      searchString: event.target.value
    })
  }

  render () {
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
          <Breadcrumbs.Item active>Students</Breadcrumbs.Item>
        </Breadcrumbs>

        <div className={classnames(globalStyles.titleContainer, styles.titleContainer, this.state.syncing && styles.titleContainerIconActive)}>
          <h1>Students</h1>
        </div>

        <div className={styles.container}>
          <div>
            <h3>Student List</h3>
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
              title={"Group Filter"}
              onSelect={this.handleSearchChange}
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
          </div>

          <div>
            {
              this.state.students
                .filter((student) => {
                  return this.filterParticipantDropDown(student.id.user) && this.filterParticipantSearchChange(student.id.user);
                })
                .map((student) => {
                  return (
                    <StudentCard key={student.id.user.id} match={this.props.match} participant={student}/>
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

export default connect(mapStateToProps, actionCreators)(Students)