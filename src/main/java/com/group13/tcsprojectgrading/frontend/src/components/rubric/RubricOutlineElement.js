import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import {addBlock, addCriterion, deleteElement, setCurrentPath, setSelectedElement} from "../../redux/rubric/actions";
import {IoEllipsisVerticalOutline, IoChevronForwardOutline, IoPricetagOutline} from "react-icons/io5";
import {createNewBlock, createNewCriterion, isBlock, removeElement} from "./helpers";
import classnames from "classnames";
import globalStyles from "../helpers/global.module.css";
import Dropdown from "react-bootstrap/Dropdown";
import {CustomToggle} from "./RubricOutline";

class RubricOutlineElement extends Component {
  constructor (props) {
    super(props);

    this.state = {
      showMenu: false,
    }
  }

  onClick = () => {
    console.log(this.props.path);
    this.props.onClickElement(this.props.data.content.id, this.props.path);
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
              <div className={classnames(styles.outlineElementIcon,
                !this.props.collapsed && styles.outlineElementIconRotated)}>
                <IoChevronForwardOutline onClick={this.props.onClickBlockCollapse}/>
              </div>
              :
              <div className={classnames(styles.outlineElementIcon)}>
                <IoPricetagOutline/>
              </div>
            }
            <div>
              {this.props.data.content.title}
            </div>
          </div>

          {this.props.isEditing &&
              <div className={styles.outlineElementRight}>
                <Dropdown onClick={(event) => {event.stopPropagation();}}>
                  <Dropdown.Toggle as={CustomToggle}>
                    <IoEllipsisVerticalOutline size={26}/>
                  </Dropdown.Toggle>

                  <Dropdown.Menu>
                    {isBlock(this.props.data.content.type) &&
                      <>
                        <Dropdown.Item onClick={() => createNewCriterion(
                          this.props,
                          this.props.path,
                          this.props.data.content.id,
                          this.props.data.children.length
                        )}>
                        Add criterion
                        </Dropdown.Item>
                        <Dropdown.Item onClick={() => createNewBlock(
                          this.props,
                          this.props.path,
                          this.props.data.content.id,
                          this.props.data.children.length)}>
                        Add section
                        </Dropdown.Item>
                      </>
                    }

                    <Dropdown.Item onClick={() => removeElement(this.props)}>
                      Delete
                    </Dropdown.Item>
                  </Dropdown.Menu>
                </Dropdown>
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

export default connect(mapStateToProps, actionCreators)(RubricOutlineElement)