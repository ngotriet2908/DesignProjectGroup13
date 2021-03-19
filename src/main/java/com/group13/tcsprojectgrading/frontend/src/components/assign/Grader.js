import React, {Component} from "react";
import styles from "./assign.module.css";
import {ListGroup, ListGroupItem} from "react-bootstrap";
import TaskCard from "./TaskCard";
import {Button, Card, FormControl} from 'react-bootstrap'
import {IoEllipsisVerticalOutline, IoChevronDownOutline} from "react-icons/io5";
import classnames from 'classnames';
import {isTeacher} from "../permissions/functions";

class Grader extends Component {
  constructor (props) {
    super(props)

    this.state = {
      // groupsFiltered : props.grader.groups,
      // filterString: "",
      // hideSearch : true

      collapsed: true,
    }
  }

  // handleHideSearch = (event) => {
  //   this.setState((prevState) => {
  //     return {hideSearch : !prevState.hideSearch}
  //   })
  // }
  //
  // handleSearchChange = (event) => {
  //   this.setState({
  //     filterString: event.target.value
  //   })
  // }

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
            <h4>{this.props.name} {this.props.grader && (isTeacher(this.props.grader.role[0].name) ? "(Teacher)" : "(TA)")}</h4>
            <div className={styles.graderHeaderButtonContainer}>
              <div className={classnames(styles.outlineButton, styles.collapseButton)} onClick={this.toggleCollapsed}>
                <IoChevronDownOutline/>
              </div>
            </div>
          </div>

          {/*<Button className={styles.graderToolBarButton}*/}
          {/*        variant="primary"*/}
          {/*        size="sm"*/}
          {/*        onClick={this.handleHideSearch}>*/}
          {/*  search*/}
          {/*</Button>*/}
          {/*{(this.state.hideSearch) ? null :*/}
          {/*  <FormControl className={styles.graderToolBarSearch}*/}
          {/*               size="sm"*/}
          {/*               type="text"*/}
          {/*               placeholder="Normal text"*/}
          {/*               onChange={this.handleSearchChange}/>*/}
          {/*}*/}
          {/*<Button className={styles.graderToolBarButton}*/}
          {/*        variant="primary"*/}
          {/*        size="sm"*/}
          {/*        onClick={this.props.onReturnClicked}>*/}
          {/*  return tasks*/}
          {/*</Button>*/}

          <div className={styles.graderContent}>
            {this.props.submissions
              // .filter((group) => {
              //   return group.name.toLowerCase().includes(this.state.filterString.toLowerCase())
              // })
              .map(group => {
                return (
                  <div key={group.id}
                    className={styles.graderSubmissionContainer}
                  >
                    <div>{group.name}</div>
                    <div>
                      <div className={styles.outlineButton} onClick={() => this.props.openAssignModal(
                        this.props.grader,
                        group
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

export default Grader