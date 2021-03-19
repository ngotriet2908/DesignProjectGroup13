import React, {Component} from "react";
import StudentList from "./StudentList";
import {Breadcrumb, Button, Spinner, InputGroup, Form, Card, Modal} from "react-bootstrap";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import styles from "./feedback.module.css";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
import { saveAs } from 'file-saver';

class Feedback extends Component {
  constructor(props) {
    super(props);
    this.state = {
      users: [],
      course: {},
      project: {},
      subject: "",
      body: "",
      receiver: {},
      isLoading: true,
      isSendingFeedback: false,
      isSendingPdf: false,
      gmailModalShow: false,
    }

  }

  componentDidMount() {
    console.log(this.props.location)
    this.setState({
      isLoading: true
    })
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/feedback`)
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.setState({
          users: data.users,
          course: data.course,
          project: data.project,
          isLoading: false,
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  handleSendFeedback = () => {
    let object = {
      "id": this.state.receiver,
      "isGroup": false,
      "subject": this.state.subject,
      "body": this.state.body,
    }
    this.setState({
      isSendingFeedback: true,
    })

    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/feedback`, "POST", object)
      .then(response => {
        this.setState({
          isSendingFeedback: false
        })
        if (response.status === 200) {

        }
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  handleGetPdf = (isDownload) => {
    let object = {
      "id": this.state.receiver,
      "isGroup": false,
      "subject": this.state.subject,
      "body": this.state.body,
    }
    this.setState({
      isSendingPdf: true
    })

    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/feedbackPdf`, "POST", object, 'application/pdf')
      .then(response => {
        this.setState({
          isSendingPdf: false
        })
        if (response.status === 200) {
          //Create a Blob from the PDF Stream
          // console.log(response.blob())
          // // console.log(response.formData())
          // console.log(response.type)
          //
          // const file = new Blob([response.blob().]);
          // //Build a URL from the file
          // const fileURL = URL.createObjectURL(file);
          // //Open the URL on new Window
          // window.open(fileURL);
          // // saveAs(file, 'fileName.pdf');
           return response.blob()
        }
      })
      .then((blob) => {
        console.log(blob)
        const file = new Blob([blob], {
          type: 'application/pdf',
        });

        if (isDownload) {
          saveAs(file, 'feedback.pdf');
        } else {
          const fileURL = URL.createObjectURL(file);
          window.open(fileURL);
        }
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  sendEmailHandler = () => {
    let object = {
      "id": this.state.receiver,
      "isGroup": false,
      "subject": this.state.subject,
      "body": this.state.body,
    }

    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/feedbackEmail`, "POST", object, undefined, false)
      .then((response) => {
        if (response.status === 401) {
          this.setState({
            gmailModalShow: true
          })
          return null
        } else {
          return response.text();
        }
      })
      .then((result) => {
        if (result !== null) {
          console.log(result)
        }
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  handleSubjectChange = (event) => {
      this.setState({
        subject: event.target.value
      })
  }

  handleReceiverChange = (event) => {
    console.log(event.target.value)
    this.setState({
      receiver: event.target.value
    })
  }

  handleBodyChange = (event) => {
    this.setState({
      body: event.target.value
    })
  }

  handleGmailModalClose = () => {
    this.setState({
      gmailModalShow: false,
    })
  }

  render() {
    return (
      // <div>
      //   Place to generate and send feedbac
      //   <StudentList courseId={this.props.match.params.courseId}/>
      // </div>
      (this.state.isLoading)?
        <Spinner className={styles.spinner} animation="border" role="status">
          <span className="sr-only">Loading...</span>
        </Spinner>
        :
        <div className={styles.container}>
          <Breadcrumb>
            <Breadcrumb.Item onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>Home</Breadcrumb.Item>
            <Breadcrumb.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id ))}>
              {this.state.course.name}
            </Breadcrumb.Item>
            <Breadcrumb.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id + "/projects/"+this.state.project.id))}>
              {this.state.project.name}
            </Breadcrumb.Item>
            <Breadcrumb.Item active>
              Feedback
            </Breadcrumb.Item>
          </Breadcrumb>

          <div className={styles.header}>
            <h2>Feedback Form</h2>
            {/*<Button href={"/api/gmail"}>*/}
            {/*  activate gmail*/}
            {/*</Button>*/}
          </div>

          <Card className={styles.feedbackCard}>
            <Form>
              <Form.Group controlId="formSubject">
                <Form.Label>Subject</Form.Label>
                <Form.Control type="text" placeholder="Enter subject" onChange={this.handleSubjectChange}/>
              </Form.Group>

              <Form.Group controlId="formBody">
                <Form.Label>Body</Form.Label>
                <Form.Control as="textarea" rows={3} onChange={this.handleBodyChange}/>
              </Form.Group>

              <Form.Group controlId="exampleForm.ControlSelect1">
                <Form.Label>Select recipient</Form.Label>
                <Form.Control as="select" onChange={this.handleReceiverChange}>
                  <option value={"none"}>None</option>
                  {this.state.users.map((user) => {
                    return <option key={user.id} value={user.id}>{user.name}</option>
                  })}
                </Form.Control>
              </Form.Group>

              <Button
                disabled={this.state.isSendingFeedback}
                onClick={this.handleSendFeedback} variant="primary">
                {this.state.isSendingFeedback ? 'Sending...' : 'Click to send feedback'}
              </Button>

              <Button
                disabled={this.state.isSendingPdf}
                onClick={() => this.handleGetPdf(true)} variant="primary">
                {this.state.isSendingPdf ? 'Sending...' : 'Click to download Pdf'}
              </Button>

              <Button
                onClick={this.sendEmailHandler} variant="primary">
                Click to send email
              </Button>

              {/*<Button*/}
              {/*  disabled={this.state.isSendingPdf}*/}
              {/*  onClick={() => this.handleGetPdf(false)} variant="primary">*/}
              {/*  {this.state.isSendingPdf ? 'Sending...' : 'Click to open Pdf in a new tab'}*/}
              {/*</Button>*/}

            </Form>
          </Card>

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

        </div>

    )
  }
}

export default Feedback;
