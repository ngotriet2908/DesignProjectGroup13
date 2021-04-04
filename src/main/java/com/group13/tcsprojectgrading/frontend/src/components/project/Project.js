import React, {Component} from "react";
import styles from "../project/project.module.css";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import Button from 'react-bootstrap/Button'
import {URL_PREFIX} from "../../services/config";

import {v4 as uuidv4} from "uuid";
import {connect} from "react-redux";
import {Spinner} from "react-bootstrap";
import store from "../../redux/store";
import {push} from "connected-react-router";
import Card from "react-bootstrap/Card";
import {deleteRubric, saveRubric} from "../../redux/rubric/actions";

import {setCurrentCourseAndProject, setCurrentLocation} from "../../redux/navigation/actions";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import Breadcrumbs from "../helpers/Breadcrumbs";

import globalStyles from '../helpers/global.module.css';
import {IoSyncOutline, IoHelpCircleOutline} from "react-icons/io5";
import {Can, ability, updateAbility} from "../permissions/ProjectAbility";
import classnames from "classnames";
import OverlayTrigger from "react-bootstrap/OverlayTrigger";
import Tooltip from "react-bootstrap/Tooltip";

class Project extends Component {
  constructor(props) {
    super(props)

    this.state = {
      project: {},
      course: {},
      rubric: null,
      grader: {},
      stats: [],
      isLoaded: false,

      syncing: false
    }
  }

  componentDidMount() {
    const courseId = this.props.match.params.courseId;
    const projectId = this.props.match.params.projectId;

    this.props.setCurrentLocation(LOCATIONS.project);
    this.props.setCurrentCourseAndProject(courseId, projectId);

    Promise.all([
      request(BASE + "courses/" + courseId + "/projects/" + projectId),
      // request(`${BASE}courses/${courseId}/projects/${projectId}/stats/submissions`),
      // request(`${BASE}courses/${courseId}/projects/${projectId}/stats/grades`),
      // request(`${BASE}courses/${courseId}/projects/${projectId}/stats/groups`),
    ])
      .then(async([res1, res2, res3]) => {
        const project = await res1.json();

        // console.log(project);

        // const statsSubmissions = await res2.json();
        // const statsGrades = await res3.json();

        // const stats = [statsSubmissions].concat(statsGrades)//.concat(statsGroups);

        this.props.saveRubric(project.rubric);

        if (project.privileges !== null) {
          updateAbility(ability, project.privileges, this.props.user)
        } else {
          console.log("No privileges found.")
        }

        this.setState({
          project: project,
          // course: project.course,
          // stats: stats,
          // grader: project.grader,
          isLoaded: true,
          syncing: false
        });
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  componentWillUnmount() {
    this.props.setCurrentCourseAndProject(null, null);
  }

  /*
  Creates a new rubric object and saves it to the store
  */
  onClickCreateRubric = () => {
    let rubric = {
      id: uuidv4(),
      projectId: this.props.match.params.projectId,
      children: []
    }

    request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId + "/rubric", "POST", rubric)
      .then(data => {
        // console.log(data);
        this.props.saveRubric(rubric);
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  onClickRemoveRubric = () => {
    request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId + "/rubric", "DELETE")
      .then(data => {
        // console.log(data);
        this.props.deleteRubric();
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  syncHandler = () => {
    if (this.state.syncing) {
      return;
    }

    this.setState({
      syncing: true
    })

    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/sync`)
      .then(response => {
        if (response.status === 200) {
          this.setState({
            syncing: false
          })
        }
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  render () {
    if (!this.state.isLoaded) {
      return (
        <div className={globalStyles.container}>
          <Spinner className={globalStyles.spinner} animation="border" role="status">
            <span className="sr-only">Loading...</span>
          </Spinner>
        </div>
      );
    }

    return (
      <div className={globalStyles.container}>
        <Breadcrumbs>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>Home</Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.project.course.id ))}>
            {this.state.project.course.name}
          </Breadcrumbs.Item>
          <Breadcrumbs.Item active>
            {this.state.project.name}
          </Breadcrumbs.Item>
        </Breadcrumbs>

        <div className={classnames(globalStyles.titleContainer, this.state.syncing && globalStyles.titleContainerIconActive)}>
          <h1>{this.state.project.name}</h1>

          <div className={styles.titleContainerButtons}>
            <Button className={globalStyles.titleActiveButton} variant="lightGreen" onClick={this.syncHandler}>
              <IoSyncOutline size={20}/> Sync
            </Button>

            {/*<OverlayTrigger*/}
            {/*  placement={'left'}*/}
            {/*  overlay={*/}
            {/*    <Tooltip className={styles.questionTooltip}>*/}
            {/*      Click on the sync button to update the course's user list and students' submissions.*/}
            {/*    </Tooltip>*/}
            {/*  }*/}
            {/*>*/}
            {/*  <div className={styles.questionTooltipContainer}>*/}
            {/*    <IoHelpCircleOutline size={25}/>*/}
            {/*  </div>*/}
            {/*</OverlayTrigger>*/}

          </div>
        </div>

        <div className={styles.container}>
          <div>
            {/*<Can I="view" a="AdminToolbar">*/}
            <div className={[globalStyles.sectionContainer, styles.administrationSectionContainer].join(" ")}>
              <div className={[globalStyles.sectionTitle, globalStyles.sectionTitleWithButton].join(" ")}>
                <h3 className={globalStyles.sectionTitleH}>
                  Administration
                </h3>
              </div>

              <div className={globalStyles.sectionFlexContainer}>
                <Card className={styles.card}>
                  <Card.Body className={[styles.cardBody, styles.administrationSectionContainerBody].join(" ")}>
                    {/*<Button variant="lightGreen" onClick={() => store.dispatch(push(this.props.match.url + "/groups"))}>*/}
                    {/*Groups*/}
                    {/*</Button>*/}

                    <Button variant="lightGreen" onClick={() => store.dispatch(push(this.props.match.url + "/participants"))}>
                        Participants
                    </Button>

                    <Button variant="lightGreen" onClick={() => store.dispatch(push(this.props.match.url + "/submissions"))}>
                      Submissions
                    </Button>

                    {/*<Can I="open" a={"ManageGraders"}>*/}
                    <Button variant="lightGreen" onClick={() => store.dispatch(push(this.props.match.url + "/graders"))}>
                      Graders
                    </Button>
                    {/*</Can>*/}

                    <Button variant="lightGreen" onClick={() => store.dispatch(push(this.props.match.url + "/feedback"))}>
                        Feedback
                    </Button>

                    {/*<Can I="read" a="Rubric">*/}
                    <Button variant="lightGreen" onClick={() => store.dispatch(push(this.props.match.url + "/rubric"))}>
                          Rubric
                    </Button>
                    {/*</Can>*/}

                  </Card.Body>
                </Card>
              </div>
            </div>
            {/*</Can>*/}

            {/*<Can I="read" a="Statistic">*/}
            {/*  <div className={[globalStyles.sectionContainer, styles.statisticSectionContainer].join(" ")}>*/}
            {/*    <div className={[globalStyles.sectionTitle, globalStyles.sectionTitleWithButton].join(" ")}>*/}
            {/*      <h3 className={globalStyles.sectionTitleH}>*/}
            {/*        Statistics*/}
            {/*      </h3>*/}
            {/*    </div>*/}
            {/*    <Card>*/}
            {/*      <Card.Body>*/}
            {/*        Here comes the Stats*/}
            {/*        /!*<CardColumns className={styles.stats}>*!/*/}
            {/*        /!*  {testStats.map(stat => {*!/*/}
            {/*        /!*    return (*!/*/}
            {/*        /!*      <Statistic title={stat.title}*!/*/}
            {/*        /!*        type={stat.type}*!/*/}
            {/*        /!*        data={stat.data}*!/*/}
            {/*        /!*        unit={stat.unit}/>*!/*/}
            {/*        /!*    );*!/*/}
            {/*        /!*  }).concat(this.state.stats.map((stat, index) => {*!/*/}
            {/*        /!*    return (*!/*/}
            {/*        /!*      <Statistic title={stat.title}*!/*/}
            {/*        /!*        key={index}*!/*/}
            {/*        /!*        type={stat.type}*!/*/}
            {/*        /!*        data={stat.data}*!/*/}
            {/*        /!*        unit={stat.unit}/>*!/*/}
            {/*        /!*    );*!/*/}
            {/*        /!*  }))}*!/*/}
            {/*        /!*</CardColumns>*!/*/}
            {/*      </Card.Body>*/}
            {/*    </Card>*/}
            {/*  </div>*/}
            {/*</Can>*/}
          </div>
        </div>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
    user: state.users.self
  };
};

const actionCreators = {
  saveRubric,
  deleteRubric,
  setCurrentLocation,
  setCurrentCourseAndProject
}

export default connect(mapStateToProps, actionCreators)(Project)