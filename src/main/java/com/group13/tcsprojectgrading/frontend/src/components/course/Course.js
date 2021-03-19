import React, {Component} from "react";
import {Ability, AbilityBuilder } from "@casl/ability";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";

import styles from "./course.module.css";

import Spinner from "react-bootstrap/Spinner";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {URL_PREFIX} from "../../services/config";
// import Statistic from "../stat/Statistic";
import EditProjectsModal from "./EditProjectsModal";
import Breadcrumbs from "../helpers/Breadcrumbs";
import ProjectCard from "../home/ProjectCard";
import {IoPencil} from "react-icons/io5";
import StatsCard from "../home/StatsCard";
import {Can, ability, updateAbilityCoursePage} from "../permissions/CoursePageAbility";
import { subject } from '@casl/ability';
import {connect} from "react-redux";
import {deleteCurrentCourse, saveCurrentCourse} from "../../redux/courses/actions";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import {setCurrentLocation} from "../../redux/navigation/actions";
import SectionContainer from "../home/SectionContainer";

import globalStyles from '../helpers/global.module.css';
import {IoFileTrayOutline} from "react-icons/io5";

// import course from "../../redux/course/reducers/course";

class Course extends Component {
  constructor (props) {
    super(props)

    this.state = {
      projects: [],
      course: {},
      stats: [],
      isLoaded: false,
      user: {},

      showModal: false,
    }
  }

  componentDidMount() {
    // TODO, the location won't be set if the component is not re-mounted, so we need to find a better location for the call below
    // TODO, UPD2: I'm not sure anymore
    this.props.setCurrentLocation(LOCATIONS.course);
    this.reloadPageData();
  }

  reloadPageData = () => {
    Promise.all([
      request(BASE + "courses/" + this.props.match.params.courseId),
      request(`${BASE}courses/${this.props.match.params.courseId}/stats/count`)
    ])
      .then(async([res1, res2]) => {
        const courses = await res1.json();
        const stats = await res2.json();
        updateAbilityCoursePage(ability, courses.user)

        this.props.saveCurrentCourse(courses.course);

        this.setState({
          projects: courses.projects,
          course: courses.course,
          stats: stats,
          isLoaded: true,
          user: courses.user
        })

        console.log(ability.rules)
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  componentWillUnmount () {
    this.props.deleteCurrentCourse();
  }

  openModal = () => {
    this.setState({
      showModal: true
    })
  }

  setShow = (show, reload=false) => {
    if (reload) {
      this.setState({
        showModal: show,
        isLoaded: false,
      })

      this.reloadPageData();
    } else {
      this.setState({
        showModal: show
      })
    }
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

        <div className={[globalStyles.titleContainer].join(" ")}>
          <h1>{this.state.course.name}</h1><span>{(new Date(this.state.course.start_at)).getFullYear()}</span>
        </div>

        {/*<Can I="write" a="Projects">*/}
        {/*  <div className={styles.sectionTitleButton} onClick={this.modalEditProjectsHandleShow}>*/}
        {/*    <IoPencil size={28}/>*/}
        {/*  </div>*/}
        {/*</Can>*/}

        <div className={styles.container}>
          <SectionContainer
            title={"Course projects"}
            data={this.state.projects}
            emptyText={"No projects selected in this course. Click on the pencil button to select course projects."}
            Component={ProjectCard}
            icon={
              <Can I="write" a="Projects">
                <IoPencil size={28} onClick={this.openModal}/>
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
            setShow={this.setShow}
            currentActive={this.state.projects}
          />

        </div>
      </div>
    )
  }
}

// const mapStateToProps = state => {
//   return {
//
//   };
// };

const actionCreators = {
  saveCurrentCourse,
  deleteCurrentCourse,
  setCurrentLocation
}

export default connect(null, actionCreators)(Course)