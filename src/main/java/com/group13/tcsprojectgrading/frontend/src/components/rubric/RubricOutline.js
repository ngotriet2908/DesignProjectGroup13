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
import globalStyles from "../helpers/global.module.css";
import classnames from "classnames";
import RubricUploadModal from "./RubricUploadModal";
import Button from "@material-ui/core/Button";
import {createNewBlock, createNewCriterion, removeAll} from "./helpers";
import MenuItem from "@material-ui/core/MenuItem";
import Menu from "@material-ui/core/Menu";
import IconButton from "@material-ui/core/IconButton";
import MoreVertIcon from "@material-ui/icons/MoreVert";
import withTheme from "@material-ui/core/styles/withTheme";
import TocIcon from '@material-ui/icons/Toc';
import EditIcon from '@material-ui/icons/Edit';


class RubricOutline extends Component {
  constructor (props) {
    super(props);

    this.padding = -2;
    this.path = "";

    this.data = {
      content: {
        id: this.props.rubric.id,
      },
      children: this.props.rubric.children
    }

    this.state = {
      anchorElement: null
    }
  }

  onClickElement = (id, path) => {
    this.props.setSelectedElement(id);
    this.props.setCurrentPath(path);
  }

  // contextual menu
  handleMenuClose = () => {
    this.setState({
      anchorElement: null
    })
  };

  handleMenuOpen = (event) => {
    this.setState({
      anchorElement: event.currentTarget,
    })
  }

  render () {
    return (
      <div className={classnames(styles.outlineContainer,
        this.props.rubric.children.length === 0 && styles.outlineContainerEmpty)}>

        {this.props.rubric.children.length > 0 &&
          <div className={styles.outlineContainerEditingToolbar}>
            {this.props.isEditing ?
              <>
                <div className={styles.outlineContainerEditingToolbarBody}>
                  <EditIcon fontSize="large" style={{color: this.props.theme.palette.primary.main}}/>
                  <h3>Outline</h3>
                </div>

                <div className={styles.outlineElementRight}>
                  <IconButton aria-label="more" onClick={this.handleMenuOpen}>
                    <MoreVertIcon fontSize="small"/>
                  </IconButton>

                  <Menu
                    id="rubric-element-menu"
                    anchorEl={this.state.anchorElement}
                    keepMounted
                    open={Boolean(this.state.anchorElement)}
                    onClose={this.handleMenuClose}
                  >
                    <MenuItem
                      key={"addCriterion"}
                      onClick={() => {
                        this.handleMenuClose()
                        createNewCriterion(
                          this.props,
                          this.path,
                          this.props.rubric.id,
                          this.props.rubric.children.length
                        )
                      }}
                    >
                      Add criterion
                    </MenuItem>
                    <MenuItem
                      key={"addSection"}
                      onClick={() => {
                        this.handleMenuClose()
                        createNewBlock(
                          this.props,
                          this.path,
                          this.props.rubric.id,
                          this.props.rubric.children.length
                        )
                      }}
                    >
                      Add section
                    </MenuItem>

                    {/* TODO doesn't work yet */}
                    {/*<MenuItem*/}
                    {/*  onClick={() => removeAll(this.props)}>*/}
                    {/*  Clear*/}
                    {/*</MenuItem>*/}
                  </Menu>
                </div>
              </>
              :
              <div className={styles.outlineContainerEditingToolbarBody}>
                <TocIcon fontSize="large" style={{color: this.props.theme.palette.primary.main}}/>
                <h3>Outline</h3>
              </div>
            }
          </div>
        }

        {this.props.rubric.children.length > 0 &&
          <RubricOutlineGroup
            path={this.path + "/children"}
            onClickElement={this.onClickElement}
            padding={this.padding}
            data={this.props.rubric.children}
          />
        }

        {this.props.rubric.children.length === 0 && this.props.isEditing &&
          <div className={styles.outlineBodyEmpty}>
            <h3>Rubric is empty</h3>
            <p>Create a criterion or a add a section</p>
            <p>
              <Button
                variant="contained"
                color="primary"
                onClick={() => createNewCriterion(
                  this.props,
                  this.path,
                  this.data.content.id,
                  this.data.children.length
                )}
                disableElevation
              >
                  Criterion
              </Button>
                or
              <Button
                variant="contained"
                color="primary"
                onClick={() => createNewBlock(
                  this.props,
                  this.path,
                  this.data.content.id,
                  this.data.children.length)}
                disableElevation
              >
                Section
              </Button>
            </p>
          </div>
        }

        {this.props.rubric.children.length === 0 && !this.props.isEditing &&
          <div className={styles.outlineBodyEmpty}>
            <h3>Rubric is empty</h3>
            <p>Add criteria and sections in the edit mode</p>
            <p>
              <Button
                variant="contained"
                color="primary"
                onClick={this.props.handleEdit}
                disableElevation
              >
                Edit
              </Button>
            </p>
          </div>
        }
      </div>
    )
  }
}

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

export default connect(mapStateToProps, actionCreators)(withTheme(RubricOutline));