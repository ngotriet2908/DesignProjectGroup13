import React, {Component} from "react";
import {Breadcrumb, Button, Spinner, InputGroup, Form, Card, Modal, ListGroup} from "react-bootstrap";
import styles from "../submissionDetails/submissionDetails.module.css";
import globalStyles from "../helpers/global.module.css";
import SubmissionDetailsAssessmentItemContainer
  from "../submissionDetails/SubmissionDetailsAssessmentsContainer/SubmissionDetailsAssessmentItemContainer";
import classnames from 'classnames';
import {IoPencilOutline} from "react-icons/io5";
import TemplateContainer from "./TemplateContainer";

class TemplatesContainer extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <>
        <ListGroup>
          {this.props.templates.map((template) => {
            return (
              <ListGroup.Item key={template.id}>
                <TemplateContainer template={template}/>
              </ListGroup.Item>)
          })}
        </ListGroup>
      </>
    );
  }
}

export default TemplatesContainer