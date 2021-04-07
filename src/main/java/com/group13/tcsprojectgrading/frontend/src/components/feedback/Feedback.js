import React, {Component} from "react";
import StudentList from "./StudentList";
import {Breadcrumb, Button, Spinner, InputGroup, Form, Card, Modal, ListGroup} from "react-bootstrap";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import styles from "./feedback.module.css";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
import { saveAs } from 'file-saver';
import globalStyles from "../helpers/global.module.css";
import TemplatesContainer from "./TemplatesContainer";
import classnames from "classnames";
import TemplatesEditContainer from "./TemplatesEditContainer";
import {IoPencilOutline, IoAdd, IoCloseOutline, IoFileTrayOutline} from "react-icons/io5";
import FeedbackSendingForm from "./FeedbackSendingForm";
import Breadcrumbs from "../helpers/Breadcrumbs";
import {push} from "connected-react-router";
import EmptyCard from "../home/EmptyCard";

class Feedback extends Component {
  constructor(props) {
    super(props);
    this.state = {
      users: [],
      participantsNotSent: [],
      participantsAll: [],
      course: {},
      project: {},
      templates: [],
      isCreatingTemplate: false,
      isLoaded: false,
      isEditingTemplates: false,
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
        const participantsNotSent = await res3.json();
        const participantsAll = await res4.json();
        const course = await res5.json();

        this.setState({
          templates: templates,
          project: project,
          course: course,
          isLoaded: true,
          participantsNotSent: participantsNotSent,
          participantsAll: participantsAll
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


  render() {
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
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id ))}>{this.state.course.name}</Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id + "/projects/"+this.state.project.id))}>{this.state.project.name}</Breadcrumbs.Item>
          <Breadcrumbs.Item active>Feedback</Breadcrumbs.Item>
        </Breadcrumbs>

        <div className={classnames(globalStyles.titleContainer, styles.titleContainer, this.state.syncing && styles.titleContainerIconActive)}>
          <h1>Feedback</h1>
        </div>

        <div className={classnames(styles.container)}>
          <div className={styles.section}>
            <div className={classnames(styles.sectionTitle, styles.sectionTitleWithButton)}>
              <h3 className={styles.sectionTitleH}>Templates</h3>
              {(!this.state.isEditingTemplates) ?
                <div className={classnames(globalStyles.iconButton, styles.primaryButton)} onClick={this.handleToggleEditingTemplates}>
                  <IoPencilOutline size={26}/>
                </div>
                :
                <div className={styles.buttonGroup}>
                  <div className={classnames(globalStyles.iconButton, styles.primaryButton)} onClick={this.handleToggleCreatingTemplates}>
                    <IoAdd size={26}/>
                  </div>
                  <div className={classnames(globalStyles.iconButton, styles.primaryButton)} onClick={this.handleToggleEditingTemplates}>
                    <IoCloseOutline size={26}/>
                  </div>
                </div>
              }
            </div>

            <div className={styles.sectionContent}>
              {this.state.templates.length === 0 &&
                <EmptyCard data={"No templates"} icon={IoFileTrayOutline}/>
              }


              {(this.state.isEditingTemplates)?
                <TemplatesEditContainer params={this.props.match.params} isCreating={this.state.isCreatingTemplate} updateTemplates={this.updateTemplatesHandler} toggleCreating={this.handleToggleCreatingTemplates} toggleEditing={this.handleToggleEditingTemplates} templates={this.state.templates}/>
                :
                <TemplatesContainer templates={this.state.templates}/>
              }
            </div>
          </div>
        </div>

        <div className={classnames(styles.container)}>
          <div className={styles.section}>
            <div className={classnames(styles.sectionTitle, styles.sectionTitleWithButton)}>
              <h3 className={styles.sectionTitleH}>Send feedback</h3>
            </div>

            <div className={styles.sectionContent}>
              <Card>
                <Card.Body>
                  <FeedbackSendingForm
                    params={this.props.match.params}
                    templates={this.state.templates}
                    pAll={this.state.participantsAll}
                    pNotSent={this.state.participantsNotSent}
                  />
                </Card.Body>
              </Card>
            </div>

          </div>
        </div>
      </div>
    )
  }
}

export default Feedback;
