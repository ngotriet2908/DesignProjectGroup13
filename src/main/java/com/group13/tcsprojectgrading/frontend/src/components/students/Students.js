import React, {Component} from "react";
import styles from "./students.module.css";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {connect} from "react-redux";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import globalStyles from "../helpers/global.module.css";
import Breadcrumbs from "../helpers/Breadcrumbs";
import classnames from "classnames";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {URL_PREFIX} from "../../services/config";
import {ability, updateAbility} from "../permissions/ProjectAbility";
import CircularProgress from "@material-ui/core/CircularProgress";
import StickyHeader from "../helpers/StickyHeader";
import TextField from "@material-ui/core/TextField";
import InputAdornment from "@material-ui/core/InputAdornment";
import SearchIcon from '@material-ui/icons/Search';
import TableContainer from "@material-ui/core/TableContainer";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";
import Card from "@material-ui/core/Card";
import Avatar from "@material-ui/core/Avatar";
import Link from "@material-ui/core/Link";
import TableFilter from "../helpers/TableFilter";


class Students extends Component {
  constructor(props) {
    super(props);

    this.submissionsFilterOptions = ["All", "Has submissions", "Has no submissions"];

    this.state = {
      participants: [],
      project: {},
      isLoaded: false,

      submissionsFilterChoice: 0,

      searchString: "",
    }
  }

  componentDidMount() {
    this.props.setCurrentLocation(LOCATIONS.submissions);

    Promise.all([
      request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/students?submissions=true`),
      request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}`),
      request(BASE + "courses/" + this.props.match.params.courseId),
    ])
      .then(async ([res1, res2, res3]) =>  {
        let students = await res1.json();
        const project = await res2.json();
        const course = await res3.json();

        // TODO remove
        // students = Array(10).fill(students).flat()

        if (project.privileges !== null) {
          updateAbility(ability, project.privileges, this.props.user)
        } else {
          console.log("No privileges found.")
        }

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

  filterParticipantDropDown = (user) => {
    let filter = this.state.submissionsFilterChoice;

    if (filter === 0) {
      return true;
    }

    if (filter === 1) {
      return user.submissions.length > 0;
    }

    if (filter === 2) {
      return user.submissions.length === 0;
    }

    return false;
  }

  filterParticipantSearchChange = (student) => {
    let criteria = student.name.toLowerCase().includes(this.state.searchString.toLowerCase())
    if (criteria) return true

    if (student.hasOwnProperty("sid")) {
      criteria = student.sid.toLowerCase().includes(this.state.searchString.toLowerCase())
      if (criteria) return true
    }

    if (student.hasOwnProperty("email")) {
      criteria = student.email.toLowerCase().includes(this.state.searchString.toLowerCase())
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
          <Breadcrumbs.Item active>Students</Breadcrumbs.Item>
        </Breadcrumbs>

        <StickyHeader
          title="Students"
        />

        <div className={globalStyles.innerScreenContainer}>
          <div className={styles.toolbar}>
            <TextField
              id="outlined-search"
              className={styles.searchBar}
              placeholder="Search by student name, email address or student number"
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
          </div>

          <TableContainer component={Card} className={classnames(styles.studentTable, globalStyles.cardShadow)}>
            <Table aria-label="students table">
              <TableHead className={styles.tableHeader}>
                <TableRow>
                  <TableCell className={styles.tableAvatarColumn}>{""}</TableCell>
                  <TableCell>Name</TableCell>
                  <TableCell>sNumber</TableCell>
                  <TableCell>Email</TableCell>
                  <TableCell>Final grade</TableCell>
                  <TableCell align="right">
                    <TableFilter
                      options={this.submissionsFilterOptions}
                      selected={this.state.submissionsFilterChoice}
                      setSelected={(index) => this.setState({submissionsFilterChoice: index})}
                      size={"small"}
                    />
                    <span style={{display: "inline-block"}}>Submissions</span>
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody className={styles.tableBody}>
                {this.state.students
                  .filter((student) => {
                    return this.filterParticipantDropDown(student) && this.filterParticipantSearchChange(student.id.user);
                  })
                  .sort((s1, s2) => s1.id.user.name.localeCompare(s2.id.user.name))
                  .map((student) => {
                    return (
                      <TableRow key={student.id.user.id} hover>
                        <TableCell scope="row">
                          <Avatar
                            alt={student.id.user.name}
                            src={student.id.user.avatar.includes("avatar-50") ? "" : student.id.user.avatar}
                          />
                        </TableCell>
                        <TableCell component="td" scope="row">

                          <Link href="#" color="primary" onClick={(event) => {
                            event.preventDefault();
                            store.dispatch(push(this.props.match.url +"/"+ student.id.user.id))
                          }}>
                            {student.id.user.name}
                          </Link>
                        </TableCell>
                        <TableCell component="td" scope="row">
                          {student.id.user.sNumber}
                        </TableCell>
                        <TableCell component="td" scope="row">
                          {student.id.user.email}
                        </TableCell>
                        <TableCell component="td" scope="row">
                          { (student.currentAssessment != null &&
                              student.currentAssessment.progress === 100)?
                                student.currentAssessment.finalGrade : null
                          }
                        </TableCell>
                        <TableCell align="right">{student.submissions.length}</TableCell>
                      </TableRow>
                    )
                  })}
              </TableBody>
            </Table>
          </TableContainer>
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
    user: state.users.self
  };
};

export default connect(mapStateToProps, actionCreators)(Students)