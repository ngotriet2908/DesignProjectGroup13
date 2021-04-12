import React, {Component} from "react";
import globalStyles from '../helpers/global.module.css';
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {connect} from "react-redux";
import classnames from "classnames";
import CustomModal from "../helpers/CustomModal";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import {IconButton} from "@material-ui/core";
import withTheme from "@material-ui/core/styles/withTheme";
import CheckBoxOutlineBlankIcon from '@material-ui/icons/CheckBoxOutlineBlank';
import CheckBoxIcon from '@material-ui/icons/CheckBox';


class ImportProjectsModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      projects: [],
      isLoaded: false,

      selected: []
    }
  }

  fetchProjects = () => {
    request(`${BASE}courses/${this.props.currentCourse.id}/projects/all`)
      .then(async(response) => {
        let projects = await response.json();

        this.setState({
          isLoaded: true,
          projects: projects
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  toggleSelected = (project) => {
    // project is selected
    if (this.state.selected.find((selectedProject) => selectedProject.id === project.id)) {
      this.setState(prevState => ({
        selected: prevState.selected.filter((selectedProject) => selectedProject.id !== project.id)
      }))
    } else {
      // project is not selected
      this.setState(prevState => ({
        selected: [...prevState.selected, project]
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

    let body = this.state.selected.map((selectedProject) => {
      console.log(selectedProject);

      return {
        id: selectedProject.id,
      }
    })

    request(
      `${BASE}courses/${this.props.currentCourse.id}/projects`,
      "POST",
      body
    )
      .then((response) => {
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
        <div className={globalStyles.modalBodyContainer}>
          {/* courses */}
          {(this.state.projects.length === 0 ||
            this.state.projects.filter((course) => {
              return !this.props.imported.find((importedProject) => importedProject.id === course.id);
            }).length === 0) ?
            <div className={classnames(globalStyles.modalBodyContainerRow, globalStyles.modalBodyContainerRowEmpty)}>
              No projects available for import
            </div>
            :

            <List>
              {this.state.projects
                .filter((project) => {
                  return !this.props.imported.find((importedProject) => importedProject.id === project.id);
                })
                .map((project) => {
                  const selected = this.state.selected.find((selectedProject) => selectedProject.id === project.id);

                  return (
                    <ListItem key={project.id}>
                      <ListItemText
                        primary={project.name}
                      />
                      <ListItemSecondaryAction>
                        <IconButton edge="end" aria-label="delete" onClick={() => this.toggleSelected(project)}>
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
        </div>
      </>
    )
  }

  render() {
    return(
      <CustomModal
        show={this.props.show}
        onClose={this.props.toggleShow}
        onShow={this.fetchProjects}
        onAccept={this.onAccept}
        title={"Import projects"}
        description={"Choose projects to import from the list below"}
        body={this.body()}
        isLoaded={this.state.isLoaded}
      />
    )
  }
}

const mapStateToProps = state => {
  return {
    currentCourse: state.courses.currentCourse
  };
};

export default connect(mapStateToProps, null)(withTheme(ImportProjectsModal))