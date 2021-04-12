import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";

import RubricOutline from "./RubricOutline";

import {request} from "../../services/request";
import {BASE } from "../../services/endpoints";

import {
  resetUpdates,
  saveRubric,
  saveRubricTemp,
  setCurrentPath,
  setEditingRubric,
  setSelectedElement
} from "../../redux/rubric/actions";

import globalStyles from '../helpers/global.module.css';
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import {setCurrentLocation} from "../../redux/navigation/actions";

import {Can, ability, updateAbility} from "../permissions/ProjectAbility";
import classnames from 'classnames';
import * as FileSaver from 'file-saver';
import CircularProgress from "@material-ui/core/CircularProgress";
import Breadcrumbs from "../helpers/Breadcrumbs";
import StickyHeader from "../helpers/StickyHeader";
import Grid from "@material-ui/core/Grid";
import RubricPaper from "./RubricPaper";
import Button from "@material-ui/core/Button";
import IconButton from "@material-ui/core/IconButton";
import EditIcon from '@material-ui/icons/Edit';
import GetAppIcon from '@material-ui/icons/GetApp';
import PictureAsPdfIcon from '@material-ui/icons/PictureAsPdf';
import PublishIcon from '@material-ui/icons/Publish';
import SaveIcon from '@material-ui/icons/Save';
import withTheme from "@material-ui/core/styles/withTheme";
import RubricUploadModal from "./RubricUploadModal";


class Rubric extends Component {
  constructor (props) {
    super(props);

    this.state = {
      isLoaded: false,
      project: {},

      showUploadModal: false,
    }
  }

  componentDidMount() {
    this.props.setCurrentLocation(LOCATIONS.rubric);
    this.props.resetUpdates();
    this.props.setCurrentPath("");

    // if no ability is found
    if (ability.rules.length === 0) {
      Promise.all([
        request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId),
        request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId + "/rubric")
      ])
        .then(async([res1, res2]) => {
          const project = await res1.json();
          const rubric = await res2.json();

          // get rubric
          this.props.saveRubric(rubric);
          this.props.setSelectedElement(rubric.id);

          // get permissions
          if (project.privileges !== null) {
            updateAbility(ability, project.privileges, this.props.user)
            this.setState({
              project: project,
              isLoaded: true
            })
          } else {
            console.log("No privileges found.")
            this.setState({
              isLoaded: true,
            });
          }
        })
        .catch(error => {
          console.error(error.message);
          this.setState({
            isLoaded: true
          });
        });
    } else {
      request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId + "/rubric")
        .then(response => {
          return response.json();
        })
        .then(data => {
          this.props.saveRubric(data);
          this.props.setSelectedElement(data.id);

          this.setState({
            isLoaded: true
          });
        })
        .catch(error => {
          console.error(error.message)

          this.setState({
            isLoaded: true
          });
        });
    }
  }

  downloadRubric = () => {
    console.log("download")
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/downloadRubric`, "GET", null, "application/octet-stream")
      .then(response => {
        if (response.status === 200) {
          return response.blob()
        }
      })
      .then((blob) => {
        console.log(blob)
        const file = new Blob([blob], {type: "application/pdf;charset=utf-8"});
        let file_name = "Rubric " + this.state.project.name + ", " + Date().toLocaleString();
        FileSaver.saveAs(file, file_name + ".pdf");
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  exportRubricFile = () => {
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/rubric/downloadFile`, "GET", null ,"application/octet-stream")
      .then(response => {
        if (response.status === 200) {
          return response.blob()
        }
      })
      .then((blob) => {
        const file = new Blob([blob], {type: "text/plain;charset=utf-8"});
        let fileName = "Rubric " + this.state.project.name + ", " + Date().toLocaleString();
        FileSaver.saveAs(file, fileName + ".txt");
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  onClickEdit = () => {
    // get rubric backup
    let rubricBackup = this.props.rubric;
    this.props.saveRubricTemp(rubricBackup);
    this.props.setEditingRubric(true);
  }

  onClickSaveButton = () => {
    if (this.props.updates.length === 0) {
      this.props.saveRubricTemp(null);
      this.props.setEditingRubric(false);
      this.props.resetUpdates();
    } else {
      request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId + "/rubric?version=" + this.props.rubric.version, "PATCH", this.props.updates)
        .then(data => {
          if (data.status === 200) {
            this.props.setEditingRubric(false);
            this.props.saveRubricTemp(null);
            this.props.resetUpdates();
          } else {
            console.log("Error updating rubric.")
          }
        })
        .catch(error => {
          console.error(error.message);
        });
    }
  }

  onClickCancelButton = () => {
    // get rubric backup
    let rubricBackup = this.props.rubricTemp;

    this.props.setSelectedElement(rubricBackup.id);
    this.props.saveRubricTemp(null);
    this.props.saveRubric(rubricBackup);
    this.props.setEditingRubric(false);
  }

  controlButtons = () => {
    return(
      <div className={classnames(styles.outlineHeaderContainer)}>
        {!this.props.isEditing ?
          (<div className={styles.outlineHeaderButtonContainer}>
            <Can I="write" a="Rubric">
              <IconButton aria-label="edit" onClick={this.onClickEdit}>
                <EditIcon />
              </IconButton>
            </Can>

            <Can I="download" a="Rubric">
              <IconButton aria-label="export" onClick={this.exportRubricFile}>
                <GetAppIcon />
              </IconButton>
            </Can>

            <Can I="write" a="Rubric">
              <IconButton aria-label="import" onClick={this.toggleShowUploadModal}>
                <PublishIcon />
              </IconButton>
            </Can>

            <Can I="download" a="Rubric">
              <IconButton aria-label="pdf" onClick={this.downloadRubric}>
                <PictureAsPdfIcon />
              </IconButton>
            </Can>
          </div>)
          :
          (<div className={styles.outlineHeaderButtonContainer}>
            <Can I="write" a="Rubric">
              <Button
                variant="contained"
                onClick={this.onClickCancelButton}
                disableElevation
              >
                Cancel
              </Button>

              <Button
                variant="contained"
                color="primary"
                onClick={this.onClickSaveButton}
                startIcon={<SaveIcon/>}
                disableElevation
              >
                Save
              </Button>
            </Can>
          </div>)
        }
      </div>
    )
  }

  // upload rubric modal

  toggleShowUploadModal = () => {
    this.setState(prevState => ({
      showUploadModal: !prevState.showUploadModal
    }))
  }

  render () {
    if (!this.state.isLoaded) {
      return (
        <div className={globalStyles.screenContainer}>
          <CircularProgress className={globalStyles.spinner}/>
        </div>
      )
    }

    return (
      <>
        <Breadcrumbs>
          <Breadcrumbs.Item active>Home</Breadcrumbs.Item>
        </Breadcrumbs>

        <StickyHeader
          title={"Rubric"}
          buttons={
            this.controlButtons()
          }
        />

        <Grid className={classnames(globalStyles.innerScreenContainer, styles.mainContainer)} container>
          <Grid item xs={5}>
            <RubricOutline
              courseId={this.props.match.params.courseId}
              projectId={this.props.match.params.projectId}
              handleEdit={this.onClickEdit}
            />
          </Grid>

          <Grid item xs={7}>
            <RubricPaper/>
          </Grid>
        </Grid>

        <RubricUploadModal
          show={this.state.showUploadModal}
          toggleShow={this.toggleShowUploadModal}
          updateRubric={this.props.saveRubric}
          courseId={this.props.match.params.courseId}
          projectId={this.props.match.params.projectId}
        />
      </>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
    selectedElement: state.rubric.selectedElement,
    isEditing: state.rubric.isEditing,
    rubricTemp: state.rubric.rubricTemp,
    updates: state.rubric.updates,
    user: state.users.self,
  };
};

const actionCreators = {
  saveRubric,
  saveRubricTemp,
  setSelectedElement,
  setCurrentLocation,
  resetUpdates,
  setCurrentPath,
  setEditingRubric
}

export default connect(mapStateToProps, actionCreators)(withTheme(Rubric));


















