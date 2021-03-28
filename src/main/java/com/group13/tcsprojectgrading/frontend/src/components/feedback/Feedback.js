import React, {Component} from "react";
import {Breadcrumb, Button, Spinner, Form, Card, Modal} from "react-bootstrap";
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
      receiver: "",
      isLoading: true,
      isSendingFeedback: false,
      isSendingPdf: false,
      gmailModalShow: false,
      isPdfAttached: true,
      isAllRecipientsChecked: false,
    }

  }

  componentDidMount() {
    this.setState({
      isLoading: true
    })
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/feedback`)
      .then(response => {
        return response.json();
      })
      .then(data => {
        this.setState({
          users: data.users,
          course: data.course,
          project: data.project,
          isLoading: false,
          subject: "Feedback: " + data.project.name,
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  handleSendFeedback = () => {
    //TODO: add possibility of sending the same email for All course students (there already is a checkbox for that )
    if (!this.state.isAllRecipientsChecked && (this.state.receiver === 'none' || this.state.receiver === "")) {
      alert('Recipient needs to be selected for feedback to be sent');
    } else if (this.state.body.trim() === "" || this.state.subject.trim() === "")  {
      alert('Subject and body cannot be empty');
    } else {
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
  }

  handleGetPdf = (isDownload) => {
    // TODO: maybe also add xls export? not sure if needed, but if there is time, something to think about
    if (this.state.isAllRecipientsChecked || (this.state.receiver === 'none' || this.state.receiver === "")) {
      alert('Please select a specific student to get .pdf of their feedback');
    } else if (this.state.subject.trim() === "") {
      alert('Subject is required to export .pdf');
    } else {
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
            const file = new Blob([blob], {
              type: 'application/pdf',
            });

            if (isDownload) {
              const user = this.state.users.find(user => Number(user.id) === Number(this.state.receiver));
              if (typeof user.sis_user_id !== 'undefined') {
                saveAs(file, 'feedback_' + this.props.match.params.projectId + '_s' + user.sis_user_id + '.pdf');
              } else {
                saveAs(file, 'feedback.pdf');
              }
            } else {
              const fileURL = URL.createObjectURL(file);
              window.open(fileURL);
            }
          })
          .catch(error => {
            console.error(error.message);
          });
    }
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

  handlePdfCheckboxChange = () => {
    this.setState({
      isPdfAttached: !this.state.isPdfAttached
    })
  }

  handleRecipientCheckboxChange = () => {
    this.setState({
      isAllRecipientsChecked: !this.state.isAllRecipientsChecked,
      receiver: "",
    })
  }

  render() {
    return (
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
                <Form.Control
                    type="text"
                    defaultValue={this.state.subject}
                    placeholder="Enter subject"
                    onChange={this.handleSubjectChange}
                />
              </Form.Group>

              <Form.Group controlId="formBody">
                <Form.Label>Body</Form.Label>
                <Form.Control as="textarea" rows={3} onChange={this.handleBodyChange}/>
              </Form.Group>

              <Form.Group controlId="formPdfCheckbox">
                <Form.Check defaultChecked={this.state.isPdfAttached}
                            type="checkbox"
                            label="Generate and attach .pdf"
                            onChange={this.handlePdfCheckboxChange}
                />
              </Form.Group>

              <Form.Group controlId="exampleForm.ControlSelect1">
                <Form.Label>Select recipient</Form.Label>
                <Form.Control as="select" onChange={this.handleReceiverChange} disabled={this.state.isAllRecipientsChecked}>
                    {
                      this.state.isAllRecipientsChecked
                          ? <option value={"all"}>All course students selected</option>
                          : <React.Fragment>
                            <option value={"none"}>None</option>
                            {/*TODO: None looks a bit weird here, i think. Maybe change it to something like 'Click here to select recipient' , i dont know*/}
                            {this.state.users.map((user) => {
                              return <option key={user.id} value={user.id}>{user.name}</option>
                            })}
                          </React.Fragment>
                    }
                </Form.Control>
              </Form.Group>

              <Form.Group controlId="formRecipientCheckbox">
                {/*TODO: implement all recipients = same email text with different pdfs OR if there is no time, remove this :D */}
                <Form.Check defaultChecked={this.state.isAllRecipientsChecked}
                            type="checkbox"
                            label="Send to all course students"
                            onChange={this.handleRecipientCheckboxChange}
                />
              </Form.Group>

              <Button
                className="mr-3"
                disabled={this.state.isSendingFeedback}
                onClick={this.handleSendFeedback} variant="primary">
                {this.state.isSendingFeedback ? 'Sending...' : 'Click to send feedback'}
              </Button>

              <Button
                className= {
                  !this.state.isAllRecipientsChecked && this.state.receiver !== '' && this.state.receiver !== 'none'
                      ? 'mr-3'
                      : 'd-none'
                }
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
