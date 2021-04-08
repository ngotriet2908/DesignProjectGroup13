import {Button, Spinner, Modal, Alert, Form} from 'react-bootstrap'
import React, {Component} from "react";
import {request} from "../../services/request";
import {BASE, PROJECT, USER_COURSES} from "../../services/endpoints";
import {connect} from "react-redux";
import {IoCloseOutline, IoCheckboxOutline, IoSquareOutline, IoReturnUpBack, IoAddOutline} from "react-icons/io5";
import classnames from 'classnames';
import globalStyles from "../helpers/global.module.css";
import {Can} from "../permissions/ProjectAbility";
import {toast} from "react-toastify";


class CreateTemplateModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isLoaded: false,
    }

    this.formRef = React.createRef();
  }

  onShow = () => {
    // this.setState({
    //   selected: this.props.currentGrader,
    // })
  }

  onClose = () => {
    this.setState({
      isLoaded: false,
      selected: {},
    })

    this.props.toggleShow();
  }

  onAccept = () => {
    let body = {
      "name": this.formRef.current.formName.value,
      "subject": this.formRef.current.formSubject.value,
      "body": this.formRef.current.formBody.value,
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

  render() {
    return(
      <Modal
        centered
        backdrop="static"
        size="lg"
        onShow={this.onShow}
        show={this.props.show}
        onHide={this.onClose}
        animation={false}
      >

        <div className={globalStyles.modalContainer}>
          <div className={globalStyles.modalHeaderContainer}>
            <h2>Create template</h2>
            <div className={classnames(globalStyles.modalHeaderContainerButton)} onClick={this.onClose}>
              <IoCloseOutline size={30}/>
            </div>
          </div>
          <div className={globalStyles.modalDescriptionContainer}>
            <div>
                Create an email message body template that can be used to send feedback to students.
            </div>
          </div>

          {/* body */}
          <div className={globalStyles.modalBodyContainer}>
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
            </Form>
          </div>

          <div className={classnames(globalStyles.modalFooterContainer, this.props.currentGrader && globalStyles.modalFooterContainerSpaceBetween)}>
            <div className={globalStyles.modalFooterContainerButtonGroup}>
              <Button variant="linkLightGray" onClick={this.onClose}>Cancel</Button>
              <Button variant="lightGreen" onClick={this.onAccept}>Save</Button>
            </div>
          </div>
        </div>
      </Modal>
    )
  }
}

export default connect(null, null)(CreateTemplateModal)