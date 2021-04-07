import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import RubricOutlineGroup from "./RubricOutlineGroup";
import {
  addBlock, addCriterion,
  deleteAllElements, deleteElement,
  resetUpdates,
  saveRubric,
  saveRubricTemp,
  setCurrentPath,
  setEditingRubric,
  setSelectedElement,
} from "../../redux/rubric/actions";
import {Can} from "../permissions/ProjectAbility";
import {
  IoSaveOutline,
  IoCloseOutline,
  IoCodeDownloadOutline,
  IoPencil,
  IoEllipsisVerticalOutline,
  IoCloudDownload,
  IoCloudUploadSharp

} from "react-icons/io5";
import globalStyles from "../helpers/global.module.css";
import classnames from "classnames";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {Dropdown, Modal, Button} from "react-bootstrap";
import {createNewBlock, createNewCriterion, removeAll} from "./helpers";
import {Form} from 'react-bootstrap'


class RubricOutline extends Component {
  constructor (props) {
    super(props);

    this.padding = 0;
    this.path = "";

    this.data = {
      content: {
        id: this.props.rubric.id,
      },
      children: this.props.rubric.children
    }

    this.state = {
      show: false,
    }

    this.fileUploaderRef = React.createRef()
  }

  onClickElement = (id, path) => {
    this.props.setSelectedElement(id);
    this.props.setCurrentPath(path);
  }

  openUploadModal = () => {
    this.setState({
      show: true
    })
  }

  handleClose = () => {
    this.setState({
      show: false
    })
  }

  onClickEdit = () => {
    // get rubric backup
    let rubricBackup = this.props.rubric;
    this.props.saveRubricTemp(rubricBackup);
    this.props.setEditingRubric(true);
  }

  downloadRubric = () => {
    this.props.downloadRubric();
  }

  handleImportRubric = () => {
    console.log(this.fileUploaderRef.current.files[0])
    let formData = new FormData();
    console.log(this.fileUploaderRef.current.files[0].size)
    formData.append("rubric", this.fileUploaderRef.current.files[0]);

    request(BASE + "courses/" + this.props.courseId + "/projects/" + this.props.projectId + "/rubric/uploadFile", "POST",
      undefined, undefined, undefined, undefined, formData)
      .then(response => {
          return response.json()
      })
      .then((data) => {
        this.props.saveRubric(data);
        this.setState({
          show: false
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  onClickCancelButton = () => {
    console.log("Cancel save.")

    // get rubric backup
    let rubricBackup = this.props.rubricTemp;

    this.props.setSelectedElement(rubricBackup.id);
    this.props.saveRubricTemp(null);
    this.props.saveRubric(rubricBackup);
    this.props.setEditingRubric(false);
  }

  onClickSaveButton = () => {
    console.log("Save rubric.");
    console.log(this.props.updates);

    if (this.props.updates.length === 0) {
      this.props.saveRubricTemp(null);
      this.props.setEditingRubric(false);
      this.props.resetUpdates();
    } else {
      request(BASE + "courses/" + this.props.courseId + "/projects/" + this.props.projectId + "/rubric", "PATCH", this.props.updates)
        .then(data => {
          console.log(data);

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

  render () {
    return (
      <div className={styles.outlineContainer}>
        <div className={classnames(styles.outlineHeaderContainer)}>
          <div className={
            classnames(styles.outlineHeader)}>
            <h3>Rubric</h3>
            {!this.props.isEditing ?
              (<div className={styles.outlineHeaderButtonContainer}>
                <Can I="write" a="Rubric">
                  <div className={classnames(globalStyles.iconButton, styles.viewerHeaderIconGreen)} onClick={this.onClickEdit}>
                    <IoPencil size={26}/>
                  </div>
                </Can>
                <Can I="download" a="Rubric">
                  <div className={classnames(globalStyles.iconButton, styles.viewerHeaderIconGreen)} onClick={this.downloadRubric}>
                    <IoCodeDownloadOutline size={34}/>
                  </div>
                </Can>
                <Can I="write" a="Rubric">
                  <div className={classnames(globalStyles.iconButton, styles.viewerHeaderIconGreen)} onClick={this.openUploadModal}>
                    <IoCloudUploadSharp size={34}/>
                  </div>
                </Can>
                <Can I="download" a="Rubric">
                  <div className={classnames(globalStyles.iconButton, styles.viewerHeaderIconGreen)} onClick={this.props.exportRubricFile}>
                    <IoCloudDownload size={34}/>
                  </div>
                </Can>
              </div>)
              :
              (<div className={styles.outlineHeaderButtonContainer}>
                {/*<Can I="write" a="Rubric">*/}
                <div className={classnames(globalStyles.iconButton, styles.viewerHeaderIconGreen)} onClick={this.onClickSaveButton}>
                  <IoSaveOutline size={26}/>
                </div>
                {/*</Can>*/}
                <div className={classnames(globalStyles.iconButton, styles.viewerHeaderIconGray)} onClick={this.onClickCancelButton}>
                  <IoCloseOutline size={34}/>
                </div>
              </div>)
            }
          </div>

          {this.props.isEditing &&
            <div className={styles.outlineHeaderContainerBottom}>
              <div>
                <h5>Default main section</h5>
              </div>
              <Dropdown onClick={(event) => {event.stopPropagation();}}>
                <Dropdown.Toggle as={CustomToggle}>
                  <IoEllipsisVerticalOutline size={26}/>
                </Dropdown.Toggle>

                <Dropdown.Menu>
                  <Dropdown.Item onClick={() => createNewCriterion(
                    this.props,
                    this.path,
                    this.props.rubric.id,
                    this.props.rubric.children.length
                  )}>
                    Add criterion
                  </Dropdown.Item>
                  <Dropdown.Item onClick={() => createNewBlock(
                    this.props,
                    this.path,
                    this.props.rubric.id,
                    this.props.rubric.children.length
                  )}>
                    Add section
                  </Dropdown.Item>
                  <Dropdown.Item onClick={() => removeAll(this.props)}>
                    Clear
                  </Dropdown.Item>
                </Dropdown.Menu>
              </Dropdown>
            </div>
          }
        </div>

        {this.props.rubric != null ?
          <RubricOutlineGroup path={this.path + "/children"} onClickElement={this.onClickElement} padding={this.padding} data={this.props.rubric.children}/>
          :
          <div>
            Empty
          </div>
        }

        <Modal show={this.state.show} onHide={this.handleClose}>
          <Modal.Header closeButton>
            <Modal.Title>Import rubric</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Form>
              <Form.Group>
                <Form.File ref={this.fileUploaderRef}
                  name="file"
                  label="Upload your rubric to avoid spending time creating them"
                  id="fileUploadForm"
                />
              </Form.Group>
            </Form>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={this.handleClose}>
              Close
            </Button>
            <Button variant="primary" onClick={this.handleImportRubric}>
              Upload
            </Button>
          </Modal.Footer>
        </Modal>
      </div>
    )
  }
}

/*
  Custom toggle button for contextual dropdown menu.
 */
export const CustomToggle = React.forwardRef(({ children, onClick }, ref) => (
  <div ref={ref} onClick={(e) => {e.preventDefault(); onClick(e); }}
    className={classnames(globalStyles.iconButton)}
  >
    {children}
  </div>
));

CustomToggle.displayName = "CustomToggle"

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
    selectedElement: state.rubric.selectedElement,
    isEditing: state.rubric.isEditing,
    rubricTemp: state.rubric.rubricTemp,
    updates: state.rubric.updates
  };
};

const actionCreators = {
  setSelectedElement,
  setCurrentPath,
  setEditingRubric,
  saveRubric,
  saveRubricTemp,
  resetUpdates,
  deleteAllElements,
  addBlock,
  addCriterion,
  deleteElement,
}

export default connect(mapStateToProps, actionCreators)(RubricOutline)