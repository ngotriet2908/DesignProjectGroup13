import React, {Component} from "react";
import {Breadcrumb, Button, Spinner, InputGroup, Form, Card, Modal, ListGroup} from "react-bootstrap";
import styles from "../submissionDetails/submissionDetails.module.css";
import globalStyles from "../helpers/global.module.css";
import {IoCopyOutline, IoTrashOutline, IoSwapHorizontal, IoPencilOutline, IoEye, IoEyeOff} from "react-icons/io5";
import classnames from "classnames";
import {request} from "../../services/request";
import {toast} from 'react-toastify'

class TemplateEditContainer extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isExpand: false,
      isEditing: false,
    }
    this.formRef = React.createRef()
  }

  toggleExpand = () => {
    this.setState(prev => {
      return {
        isExpand: !prev.isExpand
      }
    })
  }

  handleCloseEdit = () => {
    this.setState({
      isEditing: false
    })
  }

  handleOpenEdit = () => {
    this.setState({
      isEditing: true
    })
  }

  handleDelete = () => {
    request(`/api/courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/feedback/templates/${this.props.template.id}`
      , "DELETE")
      .then((response) => {
        return response.json()
      })
      .then((data) => {
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
        this.props.updateTemplates(data)
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

  handleSubmitUpdateTemplate = () => {
    let obj = {
      "name": this.formRef.current.formName.value,
      "subject": this.formRef.current.formSubject.value,
      "body": this.formRef.current.formBody.value,
    }
    request(`/api/courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/feedback/templates/${this.props.template.id}`
      , "PUT", obj)
      .then((response) => {
        return response.json()
      })
      .then((data) => {
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
        this.props.updateTemplates(data)
        this.setState({
          isEditing: false
        })
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

  render() {
    return (
        (this.state.isEditing)?
              <Form ref={this.formRef}>
                <Form.Group controlId="formName">
                  <Form.Label>Name</Form.Label>
                  <Form.Control type="text" placeholder="Enter template name" defaultValue={this.props.template.name}/>
                </Form.Group>

                <Form.Group controlId="formSubject">
                  <Form.Label>Subject</Form.Label>
                  <Form.Control type="text" placeholder="Enter template subject" defaultValue={this.props.template.subject}/>
                </Form.Group>

                <Form.Group controlId="formBody">
                  <Form.Label>Body</Form.Label>
                  <Form.Control as="textarea" rows={3} placeholder="Enter template body" defaultValue={this.props.template.body}/>
                </Form.Group>

                <Button variant="primary" onClick={this.handleSubmitUpdateTemplate}>
                  Save
                </Button>

                <Button variant="danger" onClick={this.handleCloseEdit}>
                  Cancel
                </Button>
              </Form>
          :
          <div>
            <div className={styles.memberAssessmentHeader}>
              <h6>{this.props.template.name}</h6>
              <div className={styles.buttonGroup}>
                <div className={classnames(globalStyles.iconButton, styles.primaryButton)} onClick={this.toggleExpand}>
                  {(this.state.isExpand) ?
                    <IoEyeOff size={26}/>
                    :
                    <IoEye size={26}/>
                  }
                </div>
                <div className={classnames(globalStyles.iconButton, styles.primaryButton)}
                     onClick={this.handleOpenEdit}>
                  <IoPencilOutline size={26}/>
                </div>

                <div className={classnames(globalStyles.iconButton, styles.dangerButton)}
                     onClick={this.handleDelete}>
                  <IoTrashOutline size={26}/>
                </div>
              </div>
          </div>

          {(this.state.isExpand) ?
            <>
            <h6>subject: {this.props.template.subject}</h6>
            <h6>body: {this.props.template.body}</h6>
            </>
            : null}

        </div>
    );
  }
}

export default TemplateEditContainer