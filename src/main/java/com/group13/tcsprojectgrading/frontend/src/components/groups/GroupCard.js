import React, {Component} from "react";
import {Breadcrumb, Button, ListGroup, ListGroupItem, Card, Badge} from "react-bootstrap";

import styles from "./groups.module.css";
import {Link} from "react-router-dom";
class GroupCard extends Component {
  render() {
    return(
      <Card>
        <Card.Body>
          <Card.Title>
            <h5>
              {this.props.group.name}

              {(this.props.group.isGroup)?
              <Badge className={styles.badge} variant="success">Group</Badge> :
              <Badge className={styles.badge} variant="danger">Individual</Badge>
              }
            </h5>
          </Card.Title>
          <div>
            <div className={styles.groupInfo}>
              <h6>Status: {this.props.group.status}</h6>
            </div>

            {(this.props.group.isGroup)?
              <div>
                <h6>Members: {this.props.group.members.length}</h6>
                {/*<h6>Members List</h6>*/}
                <ListGroup>
                  {this.props.group.members
                    .map((member) => {
                      return (
                        <ListGroupItem key={member.sid}>
                          <div className={styles.userInfo}>
                            <h6>name: {member.name}</h6>
                            <h6>sid: {member.sid}</h6>
                            <h6>email: {member.email}</h6>
                          </div>
                        </ListGroupItem>
                      )
                    })}
                </ListGroup>
              </div>
              :
              <div>
                <h6>sid: {this.props.group.sid}</h6>
              </div>
            }
          </div>
        </Card.Body>
      </Card>
    )
  }
}

export default GroupCard