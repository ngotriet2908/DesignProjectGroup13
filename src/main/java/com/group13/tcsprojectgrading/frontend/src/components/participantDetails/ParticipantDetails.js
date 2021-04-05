import React, {Component} from "react";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {Breadcrumb, Spinner, Button, ListGroup, ListGroupItem, Card, Badge} from "react-bootstrap";
import styles from "../submissionDetails/submissionDetails.module.css";
import globalStyles from "../helpers/global.module.css";
import classnames from "classnames";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {connect} from "react-redux";
import {toast} from "react-toastify";

class ParticipantDetails extends Component {
  constructor(props) {
    super(props);
    this.state = {
      submissions:[],
      participant: {},
      isLoaded: false,
    }
  }

  deleteHandler = (submission) => {
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${submission.id}/removeParticipant/${this.state.participant.id}?returnAllSubmissions=true`, "DELETE")
      .then(response => {
        return response.json()
      })
      .then(data => {
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
        this.setState({
          submissions: data
        })
      })
      .catch(error => {
        alert(error.message)
      });
  }

  componentDidMount() {
    // this.props.setCurrentLocation(LOCATIONS.submission);

    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/participants/${this.props.match.params.participantId}`)
      .then(async response => {
        let data = await response.json();
        // console.log(data);
        this.setState({
          submissions: data.submissions,
          participant: data.id.user,
          isLoaded: true,
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  render() {
    return (
      (this.state.isLoading)?
        <Spinner className={styles.spinner} animation="border" role="status">
          <span className="sr-only">Loading...</span>
        </Spinner>
        :
      <div>
        <div className={globalStyles.container}>
          <div className={classnames(globalStyles.titleContainer, styles.titleContainer)}>
            <h1>{this.state.participant.name}</h1>
          </div>

          <Card>
            <Card.Body>
              <Card.Title>
                Participant Info
              </Card.Title>
              <h6>Name: {this.state.participant.name}</h6>
              <h6>sid: {this.state.participant.sid}</h6>
              <h6>email: {this.state.participant.email}</h6>
            </Card.Body>
          </Card>

          <Card>
            <Card.Body>
              <Card.Title>
                Submissions
              </Card.Title>
              {this.state.submissions.map((submission) => {
                return (
                  <Card>
                    <Card.Body>
                      <Card.Title>
                        {submission.name}
                      </Card.Title>
                      <Button onClick={() => store.dispatch(push(this.props.match.url.split("/").slice(0, this.props.match.url.split("/").length - 2).join("/") + "/submissions/"+ submission.id))}>
                        open </Button>
                      <Button onClick={() => this.deleteHandler(submission)}>delete</Button>
                      <h6>id: {submission.id}</h6>
                      {/*<h6>name: {submission.name}</h6>*/}
                      <h6>contains current assessment: {submission.containsCurrentAssessment.toString()}</h6>
                    </Card.Body>
                  </Card>
                )
              })}
            </Card.Body>
          </Card>
        </div>
      </div>
    );
  }
}

const mapStateToProps = state => {
  return {
  };
};

const actionCreators = {

}

export default connect(mapStateToProps, actionCreators)(ParticipantDetails)