import React, {Component} from "react";
import styles from "../project/project.module.css";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import Button from 'react-bootstrap/Button'
import {URL_PREFIX} from "../../services/config";
import {Link} from 'react-router-dom'

import {v4 as uuidv4} from "uuid";
import {connect} from "react-redux";
import {CardColumns, Spinner} from "react-bootstrap";
import store from "../../redux/store";
import {push} from "connected-react-router";
import Card from "react-bootstrap/Card";
import {deleteRubric, saveRubric} from "../../redux/rubric/actions";

import testStats from "../stat/testStats.json";
import Statistic from "../stat/Statistic";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import Breadcrumbs from "../helpers/Breadcrumbs";

import globalStyles from '../helpers/global.module.css';
import HomeTaskCard from "../home/HomeTaskCard";
import SectionContainer from "../home/SectionContainer";
import {IoCheckboxOutline} from "react-icons/io5";
import TaskContainer from "./TaskContainer";
import {Can, ability, updateAbility} from "../permissions/ProjectAbility";

class Project extends Component {
  constructor(props) {
    super(props)

    this.state = {
      project: {},
      course: {},
      rubric: null,
      grader: {},
      stats: [],
      isLoaded: false
    }
  }

  componentDidMount() {
    this.props.setCurrentLocation(LOCATIONS.project);

    const courseId = this.props.match.params.courseId;
    const projectId = this.props.match.params.projectId;

    Promise.all([
      request(BASE + "courses/" + courseId + "/projects/" + projectId),
      request(`${BASE}courses/${courseId}/projects/${projectId}/stats/submissions`),
      request(`${BASE}courses/${courseId}/projects/${projectId}/stats/grades`),
      // request(`${BASE}courses/${courseId}/projects/${projectId}/stats/groups`),
    ])
      .then(async([res1, res2, res3]) => {
        const project = await res1.json();
        const statsSubmissions = await res2.json();
        const statsGrades = await res3.json();

        const stats = [statsSubmissions].concat(statsGrades)//.concat(statsGroups);

        this.props.saveRubric(project.rubric);

        if (project.grader !== null && project.grader.privileges !== null) {
          updateAbility(ability, project.grader.privileges, project.grader)
        } else {
          console.log("No grader or privileges found.")
        }
        // console.log(ability.rules)

        this.setState({
          project: project.project,
          course: project.course,
          stats: stats,
          grader: project.grader,
          isLoaded: true
        });
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  /*
  Creates a new rubric object and saves it to the store
  */
  onClickCreateRubric = () => {
    // console.log("Creating rubric for project " + parseInt(this.props.match.params.projectId));

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
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id ))}>
            {this.state.course.name}
          </Breadcrumbs.Item>
          <Breadcrumbs.Item active>
            {this.state.project.name}
          </Breadcrumbs.Item>
        </Breadcrumbs>

        <div className={globalStyles.titleContainer}>
          <h1>{this.state.project.name}</h1>
        </div>

        <div className={styles.container}>
          <div>
            <Can I="view" a="AdminToolbar">
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

                      <Button variant="lightGreen" onClick={() => store.dispatch(push(this.props.match.url + "/submissions"))}>
                      Submissions
                      </Button>

                      <Can I="open" a={"ManageGraders"}>
                        <Button variant="lightGreen" onClick={() => store.dispatch(push(this.props.match.url + "/graders"))}>
                      Graders
                        </Button>
                      </Can>

                      <Button variant="lightGreen" onClick={() => store.dispatch(push(this.props.match.url + "/feedback"))}>
                        Feedback
                      </Button>

                      <Can I="read" a="Rubric">
                        <Button variant="lightGreen" onClick={() => store.dispatch(push(this.props.match.url + "/rubric"))}>
                          Rubric
                        </Button>
                      </Can>

                    </Card.Body>
                  </Card>
                </div>
              </div>
            </Can>

            {/*<Can I="read" a="Rubric">*/}
            {/*  <div className={[globalStyles.sectionContainer, styles.rubricSectionContainer].join(" ")}>*/}
            {/*    <div className={[globalStyles.sectionTitle, globalStyles.sectionTitleWithButton].join(" ")}>*/}
            {/*      <h3 className={globalStyles.sectionTitleH}>*/}
            {/*      Rubric*/}
            {/*      </h3>*/}
            {/*    </div>*/}

            {/*    <div className={globalStyles.sectionFlexContainer}>*/}
            {/*      <Card>*/}
            {/*        <Card.Body>*/}
            {/*          /!*<div>*!/*/}
            {/*          /!*  Last modified at {this.state.project}*!/*/}
            {/*          /!*</div>*!/*/}
            {/*          <div>*/}
            {/*            <Can I="read" a="Rubric">*/}
            {/*              <Button variant="lightGreen" onClick={() => store.dispatch(push(this.props.match.url + "/rubric"))}>*/}
            {/*              Open rubric*/}
            {/*              </Button>*/}
            {/*            </Can>*/}
            {/*          </div>*/}
            {/*        </Card.Body>*/}
            {/*      </Card>*/}
            {/*    </div>*/}
            {/*  </div>*/}
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

          {/*<div>*/}
          {/*  <Can I="view" a="TodoList">*/}
          {/*    <SectionContainer*/}
          {/*      title={"To-Do list"}*/}
          {/*      data={[]}*/}
          {/*      // emptyText={"Your tasks will appear here when they are assigned to you."}*/}
          {/*      emptyText={"Nothing to do"}*/}
          {/*      Component={HomeTaskCard}*/}
          {/*      className={styles.tasksSectionContainer}*/}
          {/*      EmptyIcon={IoCheckboxOutline}*/}
          {/*    />*/}
          {/*  </Can>*/}
          {/*</div>*/}
        </div>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric
  };
};

const actionCreators = {
  saveRubric,
  deleteRubric,
  setCurrentLocation
}

export default connect(mapStateToProps, actionCreators)(Project)