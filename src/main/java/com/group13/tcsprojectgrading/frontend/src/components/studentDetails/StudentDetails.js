import React, {Component} from "react";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import styles from "./studentDetails.module.css";
import globalStyles from "../helpers/global.module.css";
import classnames from "classnames";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {connect} from "react-redux";
import {setCurrentLocation} from "../../redux/navigation/actions";
import Breadcrumbs from "../helpers/Breadcrumbs";
import {URL_PREFIX} from "../../services/config";
import {ability, Can, updateAbility} from "../permissions/ProjectAbility";
import { subject } from '@casl/ability';
import CircularProgress from "@material-ui/core/CircularProgress";
import StickyHeader from "../helpers/StickyHeader";
import Button from "@material-ui/core/Button";
import SyncIcon from "@material-ui/icons/Sync";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import PermIdentityIcon from '@material-ui/icons/PermIdentity';
import EmailIcon from '@material-ui/icons/Email';
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableFilter from "../helpers/TableFilter";
import TableBody from "@material-ui/core/TableBody";
import Avatar from "@material-ui/core/Avatar";
import Link from "@material-ui/core/Link";
import TableContainer from "@material-ui/core/TableContainer";
import CheckCircleIcon from '@material-ui/icons/CheckCircle';
import DeleteOutlineIcon from '@material-ui/icons/DeleteOutline';
import IconButton from "@material-ui/core/IconButton";
import Grid from "@material-ui/core/Grid";
import withTheme from "@material-ui/core/styles/withTheme";


class StudentDetails extends Component {
  constructor(props) {
    super(props);
    this.state = {
      submissions:[],
      student: {},
      course: {},
      project: {},

      isLoaded: false,
    }
  }

  deleteHandler = (submission) => {
    request(
      `${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${submission.id}/removeParticipant/${this.state.student.id}?returnAllSubmissions=true`,
      "DELETE")
      .then(async response => {
        let data = await response.json();

        this.setState({
          submissions: data
        })
      })
      .catch(error => {
        alert(error.message)
      });
  }

  componentDidMount() {
    this.props.setCurrentLocation(LOCATIONS.student);
    Promise.all([
      request(BASE + "courses/" + this.props.match.params.courseId),
      request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId),
      request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/students/${this.props.match.params.studentId}`)
    ])
      .then(async ([res1, res2, res3]) => {
        const course = await res1.json();
        const project = await res2.json();
        let data = await res3.json();

        if (project.privileges !== null) {
          updateAbility(ability, project.privileges, this.props.user)
          console.log(ability)
        } else {
          console.log("No privileges found.")
        }

        this.setState({
          submissions: data.submissions,
          student: data.id.user,
          isLoaded: true,
          course: course,
          project: project
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

    return (
      <>
        <Breadcrumbs>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>Home</Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id))}>
            {this.state.course.name}
          </Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id + "/projects/" + this.state.project.id))}>
            {this.state.project.name}
          </Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(`${URL_PREFIX}/courses/${this.state.course.id}/projects/${this.state.project.id}/students`))}>
            Students
          </Breadcrumbs.Item>
          <Breadcrumbs.Item active>
            {this.state.student.name}
          </Breadcrumbs.Item>
        </Breadcrumbs>

        <StickyHeader
          title={this.state.student.name}
        />

        <div className={globalStyles.innerScreenContainer}>

          <div className={styles.section}>
            <Card className={classnames(globalStyles.cardShadow)}>
              <CardContent>
                <h4>Student's Information</h4>

                <Grid
                  container
                  className={classnames(globalStyles.cardBodyContent)}
                >
                  <Grid item sm={6}>
                    <div className={classnames(globalStyles.flexRow, globalStyles.flexRowWithIcon)}>
                      <PermIdentityIcon style={{color: this.props.theme.palette.primary.main}}/> <span>{this.state.student.sNumber}</span>
                    </div>
                  </Grid>

                  <Grid item sm={6}>
                    <div className={classnames(globalStyles.flexRow, globalStyles.flexRowWithIcon)}>
                      <EmailIcon style={{color: this.props.theme.palette.primary.main}}/> <span>{this.state.student.email}</span>
                    </div>
                  </Grid>
                </Grid>

              </CardContent>
            </Card>
          </div>


          <div className={styles.section}>
            <div className={globalStyles.sectionTitle}>
              <h2>Submissions</h2>
            </div>

            <TableContainer component={Card} className={classnames(styles.submissionTable, globalStyles.cardShadow)}>
              <Table aria-label="submissions table">
                <TableHead className={styles.tableHeader}>
                  <TableRow>
                    <TableCell>Name</TableCell>
                    <TableCell align="center">Submission #</TableCell>
                    <TableCell align="center">Is current</TableCell>
                    <TableCell align="center">{""}</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody className={styles.tableBody}>
                  {this.state.submissions
                    .map((submission) => {
                      return (
                        <TableRow key={submission.id} hover>
                          <TableCell component="td" scope="row">
                            <Link href="#" color="primary" onClick={(event) => {
                              event.preventDefault();
                              store.dispatch(push(this.props.match.url.split("/").slice(0, this.props.match.url.split("/").length - 2).join("/") + "/submissions/"+ submission.id))
                            }}>
                              {submission.name}
                            </Link>
                          </TableCell>
                          <TableCell align="center" component="td" scope="row">
                            {submission.id}
                          </TableCell>
                          <TableCell align="center" component="td" scope="row">
                            {submission.containsCurrentAssessment &&
                              <CheckCircleIcon style={{color: "green"}}/>
                            }
                          </TableCell>
                          <TableCell align="center" component="td" scope="row">
                            <IconButton
                              size="medium"
                              onClick={() => this.deleteHandler(submission)}
                            >
                              <DeleteOutlineIcon style={{color: "red"}}/>
                            </IconButton>
                          </TableCell>
                        </TableRow>
                      )
                    })}
                </TableBody>
              </Table>
            </TableContainer>
          </div>
        </div>
      </>
    );
  }
}

const mapStateToProps = state => {
  return {
    user: state.users.self
  };
};

const actionCreators = {
  setCurrentLocation
}

export default connect(mapStateToProps, actionCreators)(withTheme(StudentDetails))