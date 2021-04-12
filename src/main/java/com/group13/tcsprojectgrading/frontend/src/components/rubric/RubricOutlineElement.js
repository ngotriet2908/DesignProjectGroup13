import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import {addBlock, addCriterion, deleteElement, setCurrentPath, setSelectedElement} from "../../redux/rubric/actions";
import {createNewBlock, createNewCriterion, isBlock, removeElement} from "./helpers";
import classnames from "classnames";
import globalStyles from "../helpers/global.module.css";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import IconButton from "@material-ui/core/IconButton";
import MoreVertIcon from '@material-ui/icons/MoreVert';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import GradeIcon from '@material-ui/icons/Grade';
import withTheme from "@material-ui/core/styles/withTheme";


class RubricOutlineElement extends Component {
  constructor (props) {
    super(props);

    this.state = {
      anchorElement: null
    }
  }

  onClick = () => {
    console.log(this.props.path);
    this.props.onClickElement(this.props.data.content.id, this.props.path);
  }

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
      <div className={classnames(
        styles.outlineElementContainer,
        (this.props.selectedElement != null &&
        this.props.data.content.id === this.props.selectedElement) && styles.outlineElementContainerSelected,
        this.props.isEditing && styles.outlineElementContainerEditing
      )} onClick={this.onClick}>

        <div className={classnames(styles.outlineElement)} style={{paddingLeft: `${this.props.padding}rem`}}>
          <div className={classnames(styles.outlineElementLeft, isBlock(this.props.data.content.type) && styles.outlineElementLeftBlock)}>
            {isBlock(this.props.data.content.type) ?
              <ChevronRightIcon
                className={classnames(styles.outlineElementIcon,
                  !this.props.collapsed && styles.outlineElementIconRotated)}
                onClick={this.props.onClickBlockCollapse}
              />
              :
              <GradeIcon
                className={classnames(styles.outlineElementIcon)}
                style={{color: this.props.theme.palette.secondary.main}}
              />
            }
            <div className={styles.outlineElementText}>
              {this.props.data.content.title}
            </div>
          </div>

          {this.props.isEditing &&
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
              {isBlock(this.props.data.content.type) && [(
                <MenuItem
                  key={"addCriterion"}
                  onClick={() => createNewCriterion(
                    this.props,
                    this.props.path,
                    this.props.data.content.id,
                    this.props.data.children.length
                  )}
                >
                  Add criterion
                </MenuItem>),
              (
                <MenuItem
                  key={"addSection"}
                  onClick={() => createNewBlock(
                    this.props,
                    this.props.path,
                    this.props.data.content.id,
                    this.props.data.children.length)}
                >
                    Add section
                </MenuItem>)
              ]}

              <MenuItem
                onClick={() => removeElement(this.props)}>
                Delete
              </MenuItem>
            </Menu>
          </div>
          }
        </div>
      </div>
    )
  }
}


const mapStateToProps = state => {
  return {
    isEditing: state.rubric.isEditing,
    selectedElement: state.rubric.selectedElement,
    rubric: state.rubric.rubric,
  };
};

const actionCreators = {
  setSelectedElement,
  addBlock,
  addCriterion,
  setCurrentPath,
  deleteElement,
}

export default connect(mapStateToProps, actionCreators)(withTheme(RubricOutlineElement))