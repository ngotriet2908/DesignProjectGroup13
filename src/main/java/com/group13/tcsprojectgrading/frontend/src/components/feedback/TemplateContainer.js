import React, {Component} from "react";
import {Breadcrumb, Button, Spinner, InputGroup, Form, Card, Modal, ListGroup} from "react-bootstrap";
import globalStyles from "../helpers/global.module.css";
import styles from "../submissionDetails/submissionDetails.module.css";
import {IoCopyOutline, IoTrashOutline, IoSwapHorizontal, IoPencilOutline, IoEye, IoEyeOff} from "react-icons/io5";
import classnames from "classnames";
class TemplateContainer extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isExpand: false,
    }
  }

  toggleExpand = () => {
    this.setState(prev => {
      return {
        isExpand: !prev.isExpand
      }
    })
  }

  render() {
    return (
      <div>

        <div className={styles.memberAssessmentHeader}>
          <h6>{this.props.template.name}</h6>
          <div className={styles.buttonGroup}>
            <div className={classnames(globalStyles.iconButton, styles.primaryButton)} onClick={this.toggleExpand}>
              {(this.state.isExpand)?
                <IoEyeOff size={26}/>
                :
                <IoEye size={26}/>
              }
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

export default TemplateContainer