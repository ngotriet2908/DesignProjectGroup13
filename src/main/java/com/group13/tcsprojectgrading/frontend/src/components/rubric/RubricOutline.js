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
  setSelectedElement
} from "../../redux/rubric/actions";
import {Can} from "../permissions/ProjectAbility";
import {
  IoSaveOutline,
  IoCloseOutline,
  IoCodeDownloadOutline,
  IoPencil,
  IoEllipsisVerticalOutline
} from "react-icons/io5";
import globalStyles from "../helpers/global.module.css";
import classnames from "classnames";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import Dropdown from "react-bootstrap/Dropdown";
import {createNewBlock, createNewCriterion, removeAll} from "./helpers";


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
  }

  onClickElement = (id, path) => {
    this.props.setSelectedElement(id);
    this.props.setCurrentPath(path);
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
                <div className={classnames(globalStyles.iconButton, styles.viewerHeaderIconGreen)} onClick={this.downloadRubric}>
                  <IoCodeDownloadOutline size={34}/>
                </div>
              </div>)
              :
              (<div className={styles.outlineHeaderButtonContainer}>
                <Can I="write" a="Rubric">
                  <div className={classnames(globalStyles.iconButton, styles.viewerHeaderIconGreen)} onClick={this.onClickSaveButton}>
                    <IoSaveOutline size={26}/>
                  </div>
                </Can>
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