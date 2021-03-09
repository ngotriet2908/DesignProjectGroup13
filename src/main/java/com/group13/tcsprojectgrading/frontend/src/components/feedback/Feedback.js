import React, {Component} from "react";
import StudentList from "./StudentList";
import {Breadcrumb, Button, Spinner, InputGroup, Form, Card} from "react-bootstrap";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import styles from "./feedback.module.css";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";

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
      isSending: false,
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
      isSending: true
    })

    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/feedback`, "POST", object)
      .then(response => {
        this.setState({
          isSending: false
        })
        if (response.status === 200) {

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
                disabled={this.state.isSending}
                onClick={this.handleSendFeedback} variant="primary">
                {this.state.isSending ? 'Sending...' : 'Click to send'}
              </Button>

            </Form>
          </Card>

        </div>

    )
  }
}

export default Feedback;
