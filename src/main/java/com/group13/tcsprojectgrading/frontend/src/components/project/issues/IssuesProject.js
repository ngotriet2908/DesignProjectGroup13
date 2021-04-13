import React, {Component} from "react";
import styles from "../../project/project.module.css";
import globalStyles from "../../helpers/global.module.css";
import classnames from "classnames";
import IssueProjectCard from "./IssueProjectCard";


class IssuesProject extends Component {
  constructor(props) {
    super(props);
    this.state = {
      filterStatusChoice: "All",
      filterOriginChoice: "All",
    }
  }


  filterIssueStateDropDown = (issue) => {
    let filter = this.state.filterStatusChoice;

    if (filter === "All") {
      return true;
    }
    return issue.status === filter;
  }

  filterIssueOriginDropDown = (issue) => {
    let filter = this.state.filterOriginChoice;

    if (filter === "All") {
      return true;
    } else if (filter === "Creator") {
      return issue.creator.id === this.props.user.id
    } else if (filter === "Addressee") {
      return issue.addressee.id === this.props.user.id
    }
  }

  onFilterStatusSelectHandler = (eventKey, event) => {
    this.setState({
      filterStatusChoice: eventKey
    })
  }

  onFilterOriginSelectHandler = (eventKey) => {
    this.setState({
      filterOriginChoice: eventKey
    })
  }

  render() {
    return (
      <div className={classnames(globalStyles.sectionContainer)}>
        <div className={classnames(globalStyles.sectionTitle, globalStyles.sectionTitleWithButton)}>
          <h3 className={globalStyles.sectionTitleH}>
            Issues

            <div className={styles.buttonGroup}>

              {/*<DropdownButton*/}
              {/*  as={ButtonGroup}*/}
              {/*  key={"primary"}*/}
              {/*  id={`dropdown-Primary`}*/}
              {/*  variant={"lightGreen"}*/}
              {/*  title={"Filter"}*/}
              {/*  onSelect={this.onFilterStatusSelectHandler}*/}
              {/*>*/}
              {/*  {["All", "divider", "Resolved", "Open"].map((filterS) => {*/}
              {/*    if (filterS === "divider") {*/}
              {/*      return <Dropdown.Divider key={filterS}/>*/}
              {/*    } else if (filterS === this.state.filterStatusChoice) {*/}
              {/*      return <Dropdown.Item key={filterS} eventKey={filterS} active>{filterS}</Dropdown.Item>*/}
              {/*    } else {*/}
              {/*      return <Dropdown.Item key={filterS} eventKey={filterS}>{filterS}</Dropdown.Item>*/}
              {/*    }*/}
              {/*  })}*/}
              {/*</DropdownButton>*/}

              {/*<DropdownButton*/}
              {/*  as={ButtonGroup}*/}
              {/*  key={"primary1"}*/}
              {/*  id={`dropdown-Primary1`}*/}
              {/*  variant={"lightGreen"}*/}
              {/*  title={"Filter"}*/}
              {/*  onSelect={this.onFilterOriginSelectHandler}*/}
              {/*>*/}
              {/*  {["All", "divider", "Creator", "Addressee"].map((filterS) => {*/}
              {/*    if (filterS === "divider") {*/}
              {/*      return <Dropdown.Divider key={filterS}/>*/}
              {/*    } else if (filterS === this.state.filterOriginChoice) {*/}
              {/*      return <Dropdown.Item key={filterS} eventKey={filterS} active>{filterS}</Dropdown.Item>*/}
              {/*    } else {*/}
              {/*      return <Dropdown.Item key={filterS} eventKey={filterS}>{filterS}</Dropdown.Item>*/}
              {/*    }*/}
              {/*  })}*/}
              {/*</DropdownButton>*/}

            </div>

          </h3>
        </div>
        <div className={globalStyles.sectionFlexContainer}>
          {this.props.issues.filter((issue) => {
            return this.filterIssueStateDropDown(issue) &&
                    this.filterIssueOriginDropDown(issue)

          }).length === 0 &&
            <div>d</div>
            // <EmptyCard data={"No issues"} icon={IoCheckboxOutline} className={styles.stretchHeight}/>
          }

          {this.props.issues
            .filter((issue) => {
              return this.filterIssueStateDropDown(issue) &&
                      this.filterIssueOriginDropDown(issue)

            })
            .map((issue) => {
              return (
                <IssueProjectCard
                  key={issue.id}
                  issue={issue}
                  user={this.props.user}
                  routeMatch={this.props.routeMatch}
                  updateIssue={this.props.updateIssue}/>
              )
            })}
        </div>
      </div>


    );
  }
}

export default IssuesProject