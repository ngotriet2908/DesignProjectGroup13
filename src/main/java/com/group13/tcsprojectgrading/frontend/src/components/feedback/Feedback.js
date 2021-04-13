import React, {Component} from "react";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import styles from "./feedback.module.css";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
import globalStyles from "../helpers/global.module.css";
import classnames from "classnames";
import Breadcrumbs from "../helpers/Breadcrumbs";
import {push} from "connected-react-router";
import CreateTemplateModal from "./CreateTemplateModal";
import CircularProgress from "@material-ui/core/CircularProgress";
import StickyHeader from "../helpers/StickyHeader";
import IconButton from "@material-ui/core/IconButton";
import AddIcon from '@material-ui/icons/Add';
import Grid from "@material-ui/core/Grid";
import EmptyCourseCard from "../home/EmptyCourseCard";
import withTheme from "@material-ui/core/styles/withTheme";
import FeedbackSendingForm from "./FeedbackSendingForm";
import TemplateCard from "./TemplateCard";


class Feedback extends Component {
  constructor(props) {
    super(props);
    this.state = {
      users: [],

      studentsNotSent: [],
      studentsAll: [],

      course: {},
      project: {},
      templates: [],

      isLoaded: false,

      showCreateTemplateModal: false,
    }

  }

  componentDidMount() {
    Promise.all([
      request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}`),
      request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/feedback/templates`),
      request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/feedback/participants/notSent`),
      request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/feedback/participants/all`),
      request(BASE + "courses/" + this.props.match.params.courseId),
    ])
      .then(async([res1, res2, res3, res4, res5]) => {
        const project = await res1.json();
        const templates = await res2.json();
        const studentsNotSent = await res3.json();
        const studentsAll = await res4.json();
        const course = await res5.json();

        this.setState({
          templates: templates,
          project: project,
          course: course,
          isLoaded: true,
          studentsNotSent: studentsNotSent,
          studentsAll: studentsAll
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  handleToggleEditingTemplates = () => {
    this.setState(prev => {
      return {
        isEditingTemplates: !prev.isEditingTemplates
      }
    })
  }

  handleToggleCreatingTemplates = () => {
    this.setState(prev => {
      return {
        isCreatingTemplate: !prev.isCreatingTemplate
      }
    })
  }

  updateTemplatesHandler = (data) => {
    this.setState({
      templates: data
    })
  }

  toggleShowCreateTemplateModal = () => {
    this.setState(prevState => ({
      showCreateTemplateModal: !prevState.showCreateTemplateModal
    }))
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
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id ))}>{this.state.course.name}</Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id + "/projects/"+this.state.project.id))}>{this.state.project.name}</Breadcrumbs.Item>
          <Breadcrumbs.Item active>Feedback</Breadcrumbs.Item>
        </Breadcrumbs>

        <StickyHeader
          title={"Feedback"}
        />

        <div className={globalStyles.innerScreenContainer}>
          <Grid container spacing={8}>
            <Grid item xs={7}>
              <div className={classnames(globalStyles.sectionContainer)}>
                <div className={classnames(globalStyles.sectionTitle, globalStyles.sectionTitleWithButtonSpread)}>
                  <h2 className={globalStyles.sectionTitleH}>Templates</h2>

                  <IconButton
                    onClick={this.toggleShowCreateTemplateModal}
                  >
                    <AddIcon/>
                  </IconButton>
                </div>

                {this.state.templates.map((template, index) => (
                  <TemplateCard
                    key={template.id}
                    template={template}
                  />

                ))}

                {this.state.templates.length === 0 &&
                <EmptyCourseCard
                  action={this.toggleShowCreateTemplateModal}
                  description={"Create template"}
                  className={styles.templateCard}
                />
                }
              </div>
            </Grid>

            {/* right */}
            <Grid item xs={5}>
              <FeedbackSendingForm
                templates={this.state.templates}
                params={this.props.match.params}
                all={this.state.studentsAll}
                notSent={this.state.studentsNotSent}
              />
            </Grid>

          </Grid>
        </div>

        <CreateTemplateModal
          show={this.state.showCreateTemplateModal}
          toggleShow={this.toggleShowCreateTemplateModal}
          routeParams={this.props.match.params}
          updateTemplates={this.updateTemplatesHandler}
        />
      </>
    )
  }
}

export default withTheme(Feedback);
