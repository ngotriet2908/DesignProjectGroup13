import React, {Component} from "react";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";

import styles from "./course.module.css";

import Spinner from "react-bootstrap/Spinner";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {URL_PREFIX} from "../../services/config";
import EditProjectsModal from "./ImportProjectsModal";
import Breadcrumbs from "../helpers/Breadcrumbs";
import ProjectCard from "../home/ProjectCard";
import {IoCloudDownloadOutline, IoSyncOutline} from "react-icons/io5";
import StatsCard from "../home/StatsCard";
import {Can, ability, updateAbilityCoursePage} from "../permissions/CoursePageAbility";
import {connect} from "react-redux";
import {deleteCurrentCourse, saveCurrentCourse} from "../../redux/courses/actions";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import {setCurrentLocation} from "../../redux/navigation/actions";
import SectionContainer from "../home/SectionContainer";

import globalStyles from '../helpers/global.module.css';
import {IoFileTrayOutline} from "react-icons/io5";
import classnames from 'classnames';
import {Button} from "react-bootstrap";


class Course extends Component {
  constructor (props) {
    super(props)

    this.state = {
      course: {},
      stats: [],
      isLoaded: false,
      user: {},

      syncing: false,

      showModal: false,
    }
  }

  componentDidMount() {
    this.props.setCurrentLocation(LOCATIONS.course);
    this.reloadPageData();
  }

  reloadPageData = () => {
    Promise.all([
      request(BASE + "courses/" + this.props.match.params.courseId),
      // request(`${BASE}courses/${this.props.match.params.courseId}/stats/count`)
    ])
      .then(async([res1, res2]) => {
        const course = await res1.json();
        // const stats = await res2.json();

        updateAbilityCoursePage(ability, course.role)
        console.log(ability)
        this.props.saveCurrentCourse(course);

        course.projects.forEach((project, index, array) => {
          array[index].course = {
            id: course.id,
            name: course.name
          }
        })

        this.setState({
          course: course,
          // stats: stats,
          isLoaded: true,
          syncing: false
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  componentWillUnmount () {
    this.props.deleteCurrentCourse();
  }

  toggleShowModal = () => {
    this.setState(prevState => ({
      showModal: !prevState.showModal
    }))
  }

  syncCourseHandler = () => {
    if (this.state.syncing) {
      return;
    }

    this.setState({
      syncing: true
    })

    request(`${BASE}courses/${this.props.match.params.courseId}/sync`, "POST", {})
      .then(response => {
        if (response.status === 200) {
          // console.log("Sync successful");
          this.reloadPageData()
        }
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  reloadProjects = () => {
    this.setState({
      isLoaded: false,
    })

    request(BASE + "courses/" + this.props.match.params.courseId)
      .then(async response => {
        const course = await response.json();

        updateAbilityCoursePage(ability, course.role)
        this.props.saveCurrentCourse(course);

        course.projects.forEach((project, index, array) => {
          array[index].course = {
            id: course.id,
            name: course.name
          }
        })

        this.setState({
          course: course,
          isLoaded: true,
        })
      })
      .catch(error => {
        console.error(error.message);
      });
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

    return (
      <div className={globalStyles.container}>

        <Breadcrumbs>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>Home</Breadcrumbs.Item>
          <Breadcrumbs.Item active>{this.state.course.name}</Breadcrumbs.Item>
        </Breadcrumbs>

        <div className={classnames(globalStyles.titleContainer, styles.titleContainer, this.state.syncing && globalStyles.titleContainerIconActive)}>
          <div className={globalStyles.titleContainerLeft}>
            <h1>{this.state.course.name}</h1>
            <span>{(new Date(this.state.course.startAt)).getFullYear()}</span>
          </div>

          <Can I="sync" a="Course">
            <Button variant="lightGreen" className={globalStyles.titleActiveButton} onClick={this.syncCourseHandler}>
              <IoSyncOutline size={20}/> Sync
            </Button>
          </Can>
        </div>

        <div className={styles.container}>
          <SectionContainer
            className={styles.section}
            title={"Course projects"}
            data={
              this.state.course.projects
            }
            emptyText={"No projects imported in this course. Click on the button to import course projects"}
            Component={ProjectCard}
            spreadButton={true}
            button={
              <Can I="write" a="Projects">
                <Button variant="lightGreen" onClick={this.toggleShowModal}>
                  <IoCloudDownloadOutline size={20}/> Import
                </Button>
              </Can>
            }
            EmptyIcon={IoFileTrayOutline}
          />

          <Can I="read" a="Statistic">
            <div className={globalStyles.sectionContainer}>
              <div className={[globalStyles.sectionTitle, globalStyles.sectionTitleWithButton].join(" ")}>
                <h3 className={globalStyles.sectionTitleH}>Course statistics</h3>
              </div>

              {/*<ul className={styles.ul}>*/}
              {/*{this.state.stats.map(stat => {*/}
              {/*  return (*/}
              {/*    <li className={styles.li} key={stat.title}>*/}
              {/*      <Statistic title ={stat.title}*/}
              {/*        type={stat.type}*/}
              {/*        data={stat.data}*/}
              {/*        unit={stat.unit}/>*/}
              {/*    </li>*/}
              {/*  );*/}
              {/*})}*/}

              <StatsCard data={this.state.stats}/>
              {/*</ul>*/}
            </div>
          </Can>

          <EditProjectsModal
            show={this.state.showModal}
            toggleShow={this.toggleShowModal}
            refresh={this.reloadProjects}
            imported={this.state.course.projects}
          />

        </div>
      </div>
    )
  }
}

const actionCreators = {
  saveCurrentCourse,
  deleteCurrentCourse,
  setCurrentLocation
}

export default connect(null, actionCreators)(Course)