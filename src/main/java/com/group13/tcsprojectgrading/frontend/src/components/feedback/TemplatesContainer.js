import React, {Component} from "react";
import {Breadcrumb, Button, Spinner, InputGroup, Form, Card, Modal, ListGroup} from "react-bootstrap";
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