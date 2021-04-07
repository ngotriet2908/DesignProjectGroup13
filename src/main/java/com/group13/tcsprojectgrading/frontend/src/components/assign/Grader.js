import React, {Component} from "react";
import styles from "./assign.module.css";
import {Button, Card, FormControl} from 'react-bootstrap'
import {IoEllipsisVerticalOutline, IoChevronDownOutline, IoArrowBackOutline, IoArrowForward} from "react-icons/io5";
import classnames from 'classnames';
import {isTeacher} from "../permissions/functions";
import globalStyles from "../helpers/global.module.css";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {Can} from "../permissions/ProjectAbility";


class Grader extends Component {
  constructor (props) {
    super(props)

    this.state = {
      collapsed: false,
    }
  }

  toggleCollapsed = () => {
    this.setState(prevState => ({
      collapsed: !prevState.collapsed
    }))
  }

  render() {
    return (
      <Card className={styles.graderContainer}>
        <Card.Body className={classnames(styles.graderBodyContainer, this.state.collapsed && styles.graderBodyContainerCollapsed)}>
          <div className={styles.graderHeader}>
            <h4>{this.props.name}
              {/*{this.props.grader != null && (isTeacher(this.props.grader.role[0].name) ? "(Teacher)" : "(TA)")}*/}
            </h4>
            <div className={styles.graderHeaderButtonContainer}>
              {this.props.grader == null &&
                <Can I="edit" a="ManageGraders">
                  <Button variant={"yellow"}
                      onClick={this.props.toggleShowBulk}>
                      Bulk assign
                  </Button>
                </Can>
              }

              <div className={classnames(globalStyles.iconButton, styles.collapseButton)} onClick={this.toggleCollapsed}>
                <IoChevronDownOutline size={26}/>
              </div>
            </div>
          </div>

          <div className={styles.graderContent}>
            {this.props.submissions.length === 0 &&
              <div className={classnames(globalStyles.modalBodyContainerRow, globalStyles.modalBodyContainerRowEmpty)}>
                {this.props.grader != null ? "No submissions assigned to the grader" : "No unassigned submissions"}
              </div>
            }

            {this.props.submissions.map(submission => {
              return (
                <div key={submission.id}
                  className={styles.graderSubmissionContainer}
                >
                  <div>{submission.name}</div>
                  <div>
                    <div className={styles.outlineButton}
                      onClick={() => this.props.toggleShow(
                        submission,
                        this.props.grader,
                      )}>
                      <IoEllipsisVerticalOutline/>
                    </div>
                  </div>
                </div>
              )
            })}
          </div>
        </Card.Body>
      </Card>
    )
  }
}

export default Grader;