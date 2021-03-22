import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";
import {request} from "../../../services/request";
import {Breadcrumb, Button, Dropdown, DropdownButton, ButtonGroup, Spinner, InputGroup, Form, Card, Modal} from "react-bootstrap";
import IssueCard from "./IssueCard";

class IssuesView extends Component {
  constructor(props) {
    super(props);
    this.state = {
      filterChoice: "all"
    }
  }

  onFilterSelectHandler = (eventKey, event) => {
    this.setState({
      filterChoice: eventKey
    })
  }

  filterIssueDropDown = (issue) => {
    let filter = this.state.filterChoice
    if (filter === "all") return true;
    return issue.status === filter;
  }

  render() {
    return (
      <div>
        <Card>
          <Card.Body>
            <Card.Title>
              <div>
                <h5>Issues View</h5>
                <Button variant="primary" onClick={this.props.createIssue}>
                  Create Issue
                </Button>
                <DropdownButton
                  as={ButtonGroup}
                  key={"primary"}
                  id={`dropdown-Primary`}
                  variant={"primary"}
                  title={"Filter"}
                  onSelect={this.onFilterSelectHandler}
                >
                  {["all", "divider", "resolved", "unresolved"].map((filterS) => {
                    if (filterS === "divider") {
                      return <Dropdown.Divider key={filterS}/>
                    } else if (filterS === this.state.filterChoice) {
                      return <Dropdown.Item key={filterS} eventKey={filterS} active>{filterS}</Dropdown.Item>
                    } else {
                      return <Dropdown.Item key={filterS} eventKey={filterS}>{filterS}</Dropdown.Item>
                    }
                  })}
                </DropdownButton>
                {this.props.issues
                  .filter((issue) => {
                    return this.filterIssueDropDown(issue)
                  })
                  .map((issue) => {
                  return (
                    <IssueCard issue={issue} params={this.props.params} updateIssues={this.props.updateIssues}/>
                  )
                })}
              </div>
            </Card.Title>
          </Card.Body>
        </Card>

      </div>
    );
  }
}

export default IssuesView