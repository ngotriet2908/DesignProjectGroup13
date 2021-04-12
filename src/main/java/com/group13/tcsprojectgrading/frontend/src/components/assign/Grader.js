import React, {Component} from "react";
import styles from "./assign.module.css";
import classnames from 'classnames';
import globalStyles from "../helpers/global.module.css";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableFilter from "../helpers/TableFilter";
import TableBody from "@material-ui/core/TableBody";
import Avatar from "@material-ui/core/Avatar";
import Link from "@material-ui/core/Link";
import store from "../../redux/store";
import {push} from "connected-react-router";
import TableContainer from "@material-ui/core/TableContainer";
import IconButton from "@material-ui/core/IconButton";
import MoreVertIcon from '@material-ui/icons/MoreVert';
import KeyboardArrowDownIcon from '@material-ui/icons/KeyboardArrowDown';
import Pluralize from 'pluralize';
import {Can} from "../permissions/ProjectAbility";
import AssignmentReturnIcon from '@material-ui/icons/AssignmentReturn';
import withTheme from "@material-ui/core/styles/withTheme";
import Tooltip from "@material-ui/core/Tooltip";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";


class Grader extends Component {
  constructor (props) {
    super(props)

    this.state = {
      collapsed: this.props.grader != null,
    }
  }

  toggleCollapsed = () => {
    this.setState(prevState => ({
      collapsed: !prevState.collapsed
    }))
  }

  disassociateSubmission = (submissionId) => {
    request(`${BASE}courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${submissionId}/dissociate`,
      "POST",
    ).then(async () => {
      this.props.reloadPage();
    })
  }

  render() {
    return (
      <Card className={classnames(styles.graderContainer, globalStyles.cardShadow)}>
        <CardContent className={classnames(styles.graderBodyContainer, this.state.collapsed && styles.graderBodyContainerCollapsed)}>
          <div className={styles.graderHeader}>
            <div className={styles.graderHeaderTitle}>
              <h4>{this.props.name}
                {/* TODO */}
                {/*{this.props.grader != null && (isTeacher(this.props.grader.role[0].name) ? "(Teacher)" : "(TA)")}*/}
              </h4>

              <span className={styles.graderHeaderSecondary}>{Pluralize( 'submission', this.props.submissions.length, true)}</span>
            </div>
            <div className={styles.graderHeaderButtonContainer}>
              {/*{this.props.grader == null &&*/}
              {/*  <Can I="edit" a="ManageGraders">*/}
              {/*    <Button variant={"yellow"}*/}
              {/*        onClick={this.props.toggleShowBulk}>*/}
              {/*        Bulk assign*/}
              {/*    </Button>*/}
              {/*  </Can>*/}
              {/*}*/}

              <IconButton className={classnames(styles.collapseButton)} onClick={this.toggleCollapsed}>
                <KeyboardArrowDownIcon/>
              </IconButton>
            </div>
          </div>

          <div className={styles.graderContent}>
            {/* submissions */}

            <TableContainer className={classnames(styles.table)}>
              <Table aria-label="table" size="small">
                <TableHead className={styles.tableHeader}>
                  <TableRow>
                    <TableCell>Group/Individual name</TableCell>
                    <TableCell align="right">
                      {""}
                    </TableCell>
                  </TableRow>
                </TableHead>
                <TableBody className={styles.tableBody}>
                  {this.props.submissions.map(submission => {
                    return (
                      <TableRow key={submission.id} hover>
                        <TableCell component="td" scope="row">
                          <Link href="#" color="primary" onClick={(event) => {
                            event.preventDefault();
                            store.dispatch(push("/app/courses/" +
                              this.props.routeParams.courseId +
                              "/projects/" +
                              this.props.routeParams.projectId +
                              "/submissions/" +
                              submission.id));
                          }}>
                            {submission.name}
                          </Link>
                        </TableCell>
                        <TableCell align="right">

                          <Can I="edit" a="ManageGraders">
                            <IconButton onClick={() => this.props.toggleShow(
                              submission,
                              this.props.grader,
                            )}>
                              <MoreVertIcon/>
                            </IconButton>
                          </Can>

                          <Can not I="edit" a="ManageGraders">
                            {this.props.grader != null && this.props.grader.id === this.props.user.id &&
                              <Tooltip title={"Return submission to 'unassigned"}>
                                <IconButton onClick={() => this.disassociateSubmission(submission.id)}>
                                  <AssignmentReturnIcon style={{color: this.props.theme.palette.error.main}}/>
                                </IconButton>
                              </Tooltip>
                            }
                          </Can>
                        </TableCell>
                      </TableRow>
                    )
                  })}
                </TableBody>
              </Table>
            </TableContainer>


            {this.props.submissions.length === 0 &&
            <div className={classnames(styles.emptyTable)}>
              {this.props.grader != null ? "No submissions assigned to the grader" : "No unassigned submissions"}
            </div>
            }
          </div>
        </CardContent>
      </Card>
    )
  }
}

export default withTheme(Grader);