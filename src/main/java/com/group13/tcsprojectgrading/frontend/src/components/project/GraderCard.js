import React, {Component} from "react";
import styles from "./project.module.css";
import {ListGroup, ListGroupItem} from "react-bootstrap";
import TaskCard from "./TaskCard";
import {Button, Card, FormControl} from 'react-bootstrap'

class GraderCard extends Component {
  constructor (props) {
    super(props)
    this.state = {
      // groupsFiltered : props.grader.groups,
      filterString: "",
      hideGroup : false,
      hideSearch : true
    }
  }

  // static getDerivedStateFromProps(nextProps, prevState) {
  //   console.log("update graderCard state")
  //   return {
  //     grader: nextProps.data,
  //     groupsFiltered : nextProps.data.groups,
  //   };
  // }

  componentDidMount() {
    console.log(this.props)
  }

  handleHideGroup = (event) => {
    this.setState((prevState) => {
      return {hideGroup : !prevState.hideGroup}
    })
  }

  handleHideSearch = (event) => {
    this.setState((prevState) => {
      return {hideSearch : !prevState.hideSearch}
    })
  }

  handleSearchChange = (event) => {
    // console.log(event.target.value)
    // let list = [...this.props.grader.groups]
    // // let list = Arrays.from(this.state.grader.groups)
    // console.log(list)
    // let filteredList = list.filter((group) => {
    //   return group.name.toLowerCase().includes(event.target.value.toLowerCase())
    // })
    // console.log(filteredList)
    //
    // this.setState({
    //     groupsFiltered: filteredList
    //   }
    // )
    this.setState({
      filterString: event.target.value
    })
  }

  render() {
    return (
      <Card border="secondary" className={styles.graderCard}>
        <div className={styles.graderToolBar}>
          <h4 className={styles.graderToolBarText}> {this.props.grader.name} </h4>
          <Button className={styles.graderToolBarButton}
                  variant="primary"
                  size="sm"
                  onClick={this.handleHideGroup}>
            {(this.state.hideGroup)? "show groups" : "hide groups"}
          </Button> {" "}
          <Button className={styles.graderToolBarButton}
                  variant="primary"
                  size="sm"
                  onClick={this.handleHideSearch}>
            search
          </Button>
          {(this.state.hideSearch) ? null :
            <FormControl className={styles.graderToolBarSearch}
                         size="sm"
                         type="text"
                         placeholder="Normal text"
                         onChange={this.handleSearchChange}/>
          }
          <Button className={styles.graderToolBarButton}
                  variant="primary"
                  size="sm"
                  onClick={this.props.onReturnClicked}>
            return tasks
          </Button>
          <h6 className={styles.notTasksCount}> Tasks: {this.props.grader.groups.length}</h6>
        </div>
        {(this.state.hideGroup)? null:
          <ListGroup className={styles.graderGroupList}>
            {/*{this.state.groupsFiltered.map(group => {*/}
            {this.props.grader.groups
              .filter((group) => {
                  return group.name.toLowerCase().includes(this.state.filterString.toLowerCase())
              })
              .map(group => {
              // console.log(group)
              return (
                <ListGroupItem key={group.id} className={styles.listGroupItemCustom}>
                  {<TaskCard
                    // groupProp={group}
                    data={group}/>}
                  {/*{group.name}*/}
                </ListGroupItem>
              )
            })}
          </ListGroup>
        }

      </Card>

    )
  }

}

export default GraderCard