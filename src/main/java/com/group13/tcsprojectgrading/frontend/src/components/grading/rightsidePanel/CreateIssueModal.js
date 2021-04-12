import React, {Component} from "react";
import globalStyles from '../../helpers/global.module.css';
import {request} from "../../../services/request";
import {connect} from "react-redux";
import classnames from "classnames";
import styles from "../grading.module.css";
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import Select from "@material-ui/core/Select";
import MenuItem from "@material-ui/core/MenuItem";
import CustomModal from "../../helpers/CustomModal";
import TextField from "@material-ui/core/TextField";


class CreateIssueModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isLoaded: false,

      reference: "",
      subject: "",
      description: "",
      addressee: "",

      users: [],
    }
  }

  fetchGraders = () => {
    Promise.all([
      request(`/api/courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/graders`)
    ])
      .then(async([res1]) => {
        const users = await res1.json();

        // load submission
        this.setState({
          users: users,
          isLoaded: true,
        });
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  visitTree(node, result) {
    let obj = {
      id: node.content.id,
      type: node.content.type,
      name: (node.content.type === "0")? "B: " + node.content.title : "C: " + node.content.title
    }
    result.push(obj)
    if (node.hasOwnProperty("children")) {
      node.children.forEach((node) => {
        this.visitTree(node, result)
      })
    }
  }

  getAllElements(rubric) {
    let result = []
    rubric.children.forEach((node) => {
      this.visitTree(node, result)
    })
    return result
  }

  onClose = () => {
    this.props.toggleShow();

    this.setState({
      isLoaded: false,

      reference: "",
      subject: "",
      description: "",
      addressee: "",

      users: [],
    })
  }

  onAccept = () => {
    let issue = {
      reference: this.state.reference,
      subject: this.state.subject === "" ? "No title" : this.state.subject,
      description: this.state.description,
      addressee: Number(this.state.addressee),
    }

    this.setState({
      isLoaded: false,
    })

    request(`/api/courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.routeParams.submissionId}/assessments/${this.props.routeParams.assessmentId}/issues`,
      "POST",
      issue)
      .then(async (response) => {
        let createdIssue = await response.json();

        // save data
        this.props.appendIssue(createdIssue);
        this.props.toggleShow();
      })
  }

  body = () => {
    return (
      <>
        <FormControl
          variant="outlined"
          className={styles.modalRow}
          fullWidth
        >
          <InputLabel>Reference issue</InputLabel>
          <Select
            value={this.state.reference}
            onChange={event => this.setState({
              reference: event.target.value
            })}
            label="Reference issue"
          >
            <MenuItem value="">
              <em>None</em>
            </MenuItem>
            {this.props.issues.map(issue => {
              return(
                <MenuItem
                  key={issue.id}
                  value={issue.id}
                >
                  {issue.subject}
                </MenuItem>
              )
            })}
          </Select>
        </FormControl>

        <FormControl
          variant="outlined"
          className={styles.modalRow}
          fullWidth
        >
          <InputLabel>Addressee</InputLabel>
          <Select
            value={this.state.addressee}
            onChange={event => this.setState({
              addressee: event.target.value
            })}
            label="Addressee"
          >
            <MenuItem value="">
              <em>None</em>
            </MenuItem>
            {this.state.users.map((grader) => {
              let p1 = (this.props.submission.grader != null && this.props.submission.grader.id === grader.id)? "(grader)": null
              let p2 = (this.props.user.id === grader.id)? "(you)": null

              return(
                <MenuItem
                  key={grader.id}
                  value={grader.id}
                >
                  {grader.name} {p1} {p2}
                </MenuItem>
              )

            })}
          </Select>
        </FormControl>

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
          label="Description"
          placeholder="Enter description of the issue"
          multiline
          variant="outlined"
          fullWidth
          rows={3}
          value={this.state.description}
          onChange={(event) => this.setState({description: event.target.value})}
        />
      </>
    )
  }

  render() {
    return(
      <CustomModal
        show={this.props.show}
        onClose={this.props.toggleShow}
        onShow={this.fetchGraders}
        onAccept={this.onAccept}
        title={"Create issue"}
        description={"Initiate a new issue"}
        body={this.body()}
        isLoaded={this.state.isLoaded}
      />
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
  };
};

const actionCreators = {

}

export default connect(mapStateToProps, null)(CreateIssueModal)