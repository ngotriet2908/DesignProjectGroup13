import React, {Component} from "react";
import {request} from "../../services/request";
import {connect} from "react-redux";
import styles from "../grading/grading.module.css";
import TextField from "@material-ui/core/TextField";
import CustomModal from "../helpers/CustomModal";


class CreateTemplateModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isLoaded: false,

      name: "",
      subject: "",
      body: ""
    }
  }

  componentDidMount() {
    this.setState({
      isLoaded: true
    })
  }

  onClose = () => {
    this.setState({
      isLoaded: false,

      name: "",
      subject: "",
      body: ""
    })

    this.props.toggleShow();
  }

  onAccept = () => {
    let body = {
      name: this.state.name,
      subject: this.state.subject,
      body: this.state.body,
    }

    request(`/api/courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/feedback/templates`
      , "POST", body)
      .then(async (response) => {
        let data = await response.json();

        this.setState({
          isLoaded: false,
        })

        this.props.toggleShow();
        this.props.updateTemplates(data);
      }).catch(error => {
        console.error(error);
      })
  }

  body = () => {
    return (
      <>
        <TextField
          className={styles.modalRow}
          label="Name"
          placeholder="Enter name"
          variant="outlined"
          fullWidth
          value={this.state.name}
          onChange={(event) => this.setState({name: event.target.value})}
        />

        <TextField
          className={styles.modalRow}
          label="Subject"
          placeholder="Enter subject"
          variant="outlined"
          fullWidth
          value={this.state.subject}
          onChange={(event) => this.setState({subject: event.target.value})}
        />

        <TextField
          className={styles.modalRow}
          label="Body"
          placeholder="Enter body text"
          multiline
          variant="outlined"
          fullWidth
          rows={3}
          value={this.state.body}
          onChange={(event) => this.setState({body: event.target.value})}
        />
      </>
    )
  }


  render() {
    return(
      <CustomModal
        show={this.props.show}
        onClose={this.props.toggleShow}
        onShow={this.onShow}
        onAccept={this.onAccept}
        title={"Create template"}
        description={"Create an email message body template that can be used to send feedback to students."}
        body={this.body()}
        isLoaded={this.state.isLoaded}
      />
    )
  }
}

export default connect(null, null)(CreateTemplateModal)