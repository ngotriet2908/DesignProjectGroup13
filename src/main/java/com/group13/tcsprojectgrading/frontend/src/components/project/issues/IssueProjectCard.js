import React, {Component} from "react";
import styles from "../../project/project.module.css";
import globalStyles from "../../helpers/global.module.css";
import IssueCard from "../../grading/rightsidePanel/IssueCard";
import classnames from "classnames";
import store from "../../../redux/store";
import {push} from "connected-react-router";


class IssueProjectCard extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isExpanded: false,
    }
  }

  expandHandler = () => {
    this.setState((prevState) => {
      return {
        isExpanded: !prevState.isExpanded
      }
    })
  }

  componentDidMount() {
    console.log(`${this.props.routeMatch.url}/submissions/${this.props.issue.submission.id}/assessments/${this.props.issue.assessment}/grading`)
    console.log(this.props.routeMatch)
  }

  render() {
    return (
      <div></div>

    // <Card className={classnames(styles.card, this.state.isExpanded && styles.cardExpanded)}>
    //   <Card.Body className={styles.cardBodyContainer}>
    //     <div className={styles.cardBodyTitle}>
    //       <h5>{this.props.issue.subject}</h5>
    //
    //       <div className={styles.cardBodyTitleButtonGroup}>
    //         <div className={classnames(globalStyles.iconButton, styles.cardButtonContainer, styles.issuesCardExpandButton)}
    //           onClick={this.expandHandler}>
    //           <IoChevronDownOutline size={26}/>
    //         </div>
    //
    //         <div className={classnames(globalStyles.iconButton, styles.cardButtonContainer)}
    //           onClick={() => store.dispatch(push(`${this.props.routeMatch.url}/submissions/${this.props.issue.submission.id}/assessments/${this.props.issue.assessment}/grading`))}>
    //           <IoArrowForward size={26}/>
    //         </div>
    //       </div>
    //
    //     </div>
    //
    //     <div className={styles.cardBodyContent}>
    //       <div className={styles.issueCardBadges}>
    //         {(this.props.issue.status === "Resolved")?
    //           <Badge className={styles.badge} variant="success">Resolved</Badge> :
    //           <Badge className={styles.badge} variant="danger">Open</Badge>
    //         }
    //       </div>
    //
    //       {(this.state.isExpanded)?
    //         (
    //           <div>
    //             <div>
    //               Opened by <b>{this.props.issue.creator.name}</b>
    //               {/*about <b>{this.props.issue.subject}</b>.*/}
    //             </div>
    //
    //             {(this.props.issue.hasOwnProperty("reference")) &&
    //             <div>Refers to <b>{this.props.issue.reference.subject}</b></div>
    //             }
    //
    //             {(this.props.issue.hasOwnProperty("addressee")) &&
    //             <div>Addressee: {this.props.issue.addressee.name}</div>
    //             }
    //
    //             {(this.props.issue.hasOwnProperty("submission")) &&
    //             <div>Submission: {this.props.issue.submission.name}</div>
    //             }
    //
    //             {/*<div>Subject: {this.props.issue.subject}</div>*/}
    //             <div>Description: {this.props.issue.description}</div>
    //
    //             {(this.props.issue.hasOwnProperty("solution")) &&
    //             <div>Solution: {this.props.issue.solution}</div>
    //             }
    //           </div>
    //         ):
    //         (
    //           <div>
    //             <div>
    //               {/* todo: target vs subject */}
    //               Opened by <b>{this.props.issue.creator.name}</b>
    //               {/*about <b>{this.props.issue.subject}</b>.*/}
    //             </div>
    //             <div>Addressee: {this.props.issue.addressee.name}</div>
    //
    //           </div>
    //         )
    //       }
    //     </div>
    //   </Card.Body>
    // </Card>
    )
  }
}

export default IssueProjectCard