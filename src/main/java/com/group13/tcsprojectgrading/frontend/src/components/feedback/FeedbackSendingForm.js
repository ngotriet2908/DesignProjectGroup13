import React, {Component} from "react";
import {Breadcrumb, Button, Spinner, InputGroup, Form, Card, Modal, ListGroup, Badge} from "react-bootstrap";
import {request} from "../../services/request";
import {toast} from 'react-toastify'
import styles from './feedback.module.css';

class FeedbackSendingForm extends Component {
  constructor(props) {
    super(props);
    this.formRef = React.createRef()
    this.state = {
      gmailModalShow : false
    }
  }

  handleSendFeedback = (isAll) => {
    let templateId = this.formRef.current.templateChoice.value
    if (templateId === "null") {
      toast.error("choose one template", {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
      });
      return;
    }

    request(`/api/courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/feedback/send/${templateId}?isAll=${isAll}`
      , "GET", undefined, undefined, false)
      .then((response) => {
        return response.json()
      })
      .then((data) => {
        if (data.hasOwnProperty("error")) {
          if (data.status === 401) {
            this.handleGmailModalShow()
            return
          }

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
        toast.success("sent emails", {
          position: "top-center",
          autoClose: 5000,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
        });
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

  handleGmailModalClose = () => {
    this.setState({
      gmailModalShow: false,
    })
  }

  handleGmailModalShow = () => {
    this.setState({
      gmailModalShow: true,
    })
  }


  render() {
    return (
      <>
        <Form ref={this.formRef}>
          <Form.Group controlId="templateChoice">
            <Form.Label>Choose template</Form.Label>
            <Form.Control as="select">
              <option value="null">None</option>
              {this.props.templates.map(template => {
                return (
                  <option key={template.id} value={template.id}>{template.name}</option>
                )
              })}
            </Form.Control>
          </Form.Group>

          <div className={styles.sendButtonGroup}>
            <Button variant="yellow" onClick={() => this.handleSendFeedback(false)}>
            Send to not sent
              <Badge style={{marginLeft: "0.5rem"}} variant="light">{this.props.pNotSent.length}</Badge>
            </Button>

            <Button variant="lightGreen" onClick={() => this.handleSendFeedback(true)}>
            Send to all
              <Badge style={{marginLeft: "0.5rem"}} variant="light">{this.props.pAll.length}</Badge>
            </Button>
          </div>
        </Form>

        <Modal show={this.state.gmailModalShow} onHide={this.handleGmailModalClose}>
          <Modal.Header closeButton>
            <Modal.Title>Gmail authentication needed</Modal.Title>
          </Modal.Header>
          <Modal.Body>This step requires Gmail authentication, Do you want to continue?</Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={this.handleGmailModalClose}>
              Close
            </Button>
            <Button href={"/api/gmail/auth"}>
              Continue
            </Button>
          </Modal.Footer>
        </Modal>
      </>
    );
  }
}

export default FeedbackSendingForm