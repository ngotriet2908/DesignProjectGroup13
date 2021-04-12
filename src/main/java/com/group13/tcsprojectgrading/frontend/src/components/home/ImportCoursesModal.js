import React, {Component} from "react";
import styles from "./home.module.css";
import globalStyles from '../helpers/global.module.css';
import classnames from "classnames";
import {request} from "../../services/request";
import {BASE, PROJECT, USER_COURSES} from "../../services/endpoints";
import {isTeacher} from "../permissions/functions";
import CustomModal from "../helpers/CustomModal";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import {IconButton} from "@material-ui/core";
import CheckBoxIcon from "@material-ui/icons/CheckBox";
import CheckBoxOutlineBlankIcon from "@material-ui/icons/CheckBoxOutlineBlank";
import ListItem from "@material-ui/core/ListItem";
import List from "@material-ui/core/List";
import withTheme from "@material-ui/core/styles/withTheme";

class ImportCoursesModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      courses: [],
      isLoaded: false,
      selected: [],
    }
  }

  fetchCourses = () => {
    request(`${BASE}courses/all`)
      .then(async response => {
        let courses = await response.json();

        this.setState({
          courses: courses,
          isLoaded: true
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  toggleSelected = (course) => {
    // course is selected
    if (this.state.selected.find((selectedCourse) => selectedCourse.id === course.id)) {
      this.setState(prevState => ({
        selected: prevState.selected.filter((selectedCourse) => selectedCourse.id !== course.id)
      }))
    } else {
      // course is not selected
      this.setState(prevState => ({
        selected: [...prevState.selected, course]
      }))
    }
  }

  onClose = () => {
    this.props.toggleShow();
  }

  onAccept = () => {
    this.setState({
      isLoaded: false,
    })


    let body = this.state.selected.map((selectedCourse) => {
      // console.log(selectedCourse);
      return {
        id: selectedCourse.id,
      }
    })

    request(`${BASE}courses/`, "POST", body)
      .then(async response => {
        this.setState({
          isLoaded: true,
        })

        this.props.toggleShow();
        this.props.refresh();
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  body = () => {
    return(
      <>
        {(this.state.courses.length === 0 ||
          this.state.courses.filter((course) => {
            return !this.props.imported.find((importedCourse) => importedCourse.id === course.id);
          }).length === 0) ?
          <div className={classnames(globalStyles.modalBodyContainerRow, globalStyles.modalBodyContainerRowEmpty)}>
            No courses available for import
          </div>
          :
          <List>
            {
              this.state.courses
                .filter((course) => {
                  return !this.props.imported.find((importedCourse) => importedCourse.id === course.id);
                })
                .map((course) => {
                  const selected = this.state.selected.find((selectedCourse) => selectedCourse.id === course.id);

                  return (
                    <ListItem key={course.id}>
                      <ListItemText
                        primary={course.name}
                      />
                      <ListItemSecondaryAction>
                        <IconButton edge="end" aria-label="delete" onClick={() => this.toggleSelected(course)}>
                          {selected ?
                            <CheckBoxIcon style={{color: this.props.theme.palette.success.main}}/>
                            :
                            <CheckBoxOutlineBlankIcon/>
                          }
                        </IconButton>
                      </ListItemSecondaryAction>
                    </ListItem>
                  )
                })
            }
          </List>
        }
      </>
    )
  }

  render() {
    return(
      <CustomModal
        show={this.props.show}
        onClose={this.props.toggleShow}
        onShow={this.fetchCourses}
        onAccept={this.onAccept}
        title={"Import courses"}
        description={"Choose courses to import from the list below"}
        body={this.body()}
        isLoaded={this.state.isLoaded}
      />
    )
  }
}

export default withTheme(ImportCoursesModal);