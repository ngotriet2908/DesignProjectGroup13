import React, {Component} from "react";
import {IoCloseOutline} from "react-icons/io5";
import classnames from "classnames";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import globalStyles from '../helpers/global.module.css';
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import {Form} from "react-bootstrap";


class RubricUploadModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isLoaded: false,
    }

    this.fileUploaderRef = React.createRef()
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
    let formData = new FormData();
    formData.append("rubric", this.fileUploaderRef.current.files[0]);

    request(BASE + "courses/" + this.props.courseId + "/projects/" + this.props.projectId + "/rubric/uploadFile", "POST",
      undefined, undefined, undefined, undefined, formData)
      .then(async response => {
        let data = await response.json();

        this.props.updateRubric(data);
        this.props.toggleShow();
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  render() {
    return(
      <Modal
        show={this.props.show}
        onHide={this.onClose}
        animation={false}
      >
        <div className={globalStyles.modalContainer}>
          <div className={globalStyles.modalHeaderContainer}>
            <h2>Import rubric</h2>
            <div className={classnames(globalStyles.modalHeaderContainerButton)} onClick={this.onClose}>
              <IoCloseOutline size={30}/>
            </div>
          </div>

          <div className={globalStyles.modalBodyContainer}>
            <Form>
              <Form.Group>
                <Form.File ref={this.fileUploaderRef}
                  name="file"
                  // label="Upload a rubric"
                  id="fileUploadForm"
                />
              </Form.Group>
            </Form>
          </div>

          <div className={classnames(globalStyles.modalFooterContainer)}>
            <div className={globalStyles.modalFooterContainerButtonGroup}>
              <Button variant="linkLightGray" onClick={this.onClose}>Cancel</Button>
              <Button variant="lightGreen" onClick={this.onAccept}>Upload</Button>
            </div>
          </div>
        </div>
      </Modal>
    )
  }
}

export default (RubricUploadModal);