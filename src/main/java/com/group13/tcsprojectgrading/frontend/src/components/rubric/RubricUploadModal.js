import React, {Component} from "react";
import classnames from "classnames";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import globalStyles from '../helpers/global.module.css';
import CustomModal from "../helpers/CustomModal";
import {DropzoneArea} from "material-ui-dropzone";


class RubricUploadModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isLoaded: false,

      files: []
    }

    // this.fileUploaderRef = React.createRef()
  }

  componentDidMount() {
    this.setState({
      isLoaded: true,
    })
  }

  onClose = () => {
    this.setState({
      graders: [],
      isLoaded: false,
      selected: [],
    })

    this.props.toggleShow();
  }

  onAccept = () => {
    if (this.state.files.length !== 1) {
      return;
    }

    console.log(this.state.files[0])

    let formData = new FormData();
    formData.append("rubric",this.state.files[0]);

    request(BASE + "courses/" + this.props.courseId + "/projects/" + this.props.projectId + "/rubric/uploadFile", "POST",

      undefined, undefined, undefined, undefined,
      formData
    )
      .then(async response => {
        let data = await response.json();

        this.props.updateRubric(data);
        this.props.toggleShow();
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  handleChange(event){
    console.log(event.target.files);

    this.setState({
      files: event.target.files
    });
  }

  body = () => {
    return (
      <>
        <input type="file" id="input" multiple onChange={(event) => this.handleChange(event)}/>
      </>
    )
  }

  render() {
    return(
      <CustomModal
        show={this.props.show}
        onClose={this.props.toggleShow}
        onShow={() => {}}
        onAccept={this.onAccept}
        title={"Upload rubric"}
        description={"Select a rubric and upload it."}
        body={this.body()}
        isLoaded={this.state.isLoaded}
      />
    )
  }
}

export default RubricUploadModal;