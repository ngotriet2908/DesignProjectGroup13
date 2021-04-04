import React, {Component} from "react";
import {Breadcrumb, Button, Spinner, InputGroup, Form, Card, Modal, ListGroup} from "react-bootstrap";
import styles from "../submissionDetails/submissionDetails.module.css";
import globalStyles from "../helpers/global.module.css";
import SubmissionDetailsAssessmentItemContainer
  from "../submissionDetails/SubmissionDetailsAssessmentsContainer/SubmissionDetailsAssessmentItemContainer";
import classnames from 'classnames';
import {IoPencilOutline, IoAdd, IoCloseOutline} from "react-icons/io5";
import TemplateContainer from "./TemplateContainer";
import {request} from "../../services/request";
import {toast} from 'react-toastify'
import TemplateEditContainer from "./TemplateEditContainer";

class TemplatesEditContainer extends Component {
  constructor(props) {
    super(props);
    this.formRef = React.createRef()
  }

  handleSubmitNewTemplate = () => {
    let obj = {
      "name": this.formRef.current.formName.value,
      "subject": this.formRef.current.formSubject.value,
      "body": this.formRef.current.formBody.value,
    }
    request(`/api/courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/feedback/templates`
      , "POST", obj)
      .then((response) => {
        return response.json()
      })
      .then((data) => {
        if (data.hasOwnProperty("error")) {
          console.log(data.status)
          console.log(data.message)
          // alert(data.message)
          toast.error(data.message, {
            position: "top-center",
            autoClose: 5000,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
          });
          return
        }
        this.props.updateTemplates(data)
        this.props.toggleCreating()
      }).catch(error => {
        toast.error(error.message, {
          position: "top-center",
          autoClose: 5000,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
        });
    })
  }

  render() {
    return (
      <>
        <div className={styles.sectionContent}>
          {(this.props.isCreating)?
            <Card>
              <Card.Body>
                <Form ref={this.formRef}>
                  <Form.Group controlId="formName">
                    <Form.Label>Name</Form.Label>
                    <Form.Control type="text" placeholder="Enter template name" />
                  </Form.Group>

                  <Form.Group controlId="formSubject">
                    <Form.Label>Subject</Form.Label>
                    <Form.Control type="text" placeholder="Enter template subject" />
                  </Form.Group>

                  <Form.Group controlId="formBody">
                    <Form.Label>Body</Form.Label>
                    <Form.Control as="textarea" rows={3} placeholder="Enter template body" />
                  </Form.Group>

                  <Button variant="primary" onClick={this.handleSubmitNewTemplate}>
                    Create
                  </Button>

                  <Button variant="danger" onClick={this.props.toggleCreating}>
                    Cancel
                  </Button>
                </Form>
              </Card.Body>
            </Card>
          :
            <ListGroup>
              {this.props.templates.map((template) => {
                return (
                  <ListGroup.Item key={template.id}>
                    <TemplateEditContainer updateTemplates={this.props.updateTemplates} params={this.props.params} template={template}/>
                  </ListGroup.Item>)
              })}
            </ListGroup>
          }

        </div>
      </>
    );
  }
}

export default TemplatesEditContainer