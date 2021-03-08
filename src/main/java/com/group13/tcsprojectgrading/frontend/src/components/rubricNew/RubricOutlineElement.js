import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import {addBlock, addCriterion, setSelectedElement} from "../../redux/rubricNew/actions";
import {FaChevronDown, FaTrashAlt, FaMinus, FaPlus, FaChevronRight, FaHandPointRight} from "react-icons/fa";
import {isBlock} from "./helpers";
import Button from "react-bootstrap/Button";

import { v4 as uuidv4 } from 'uuid';
import RubricOutlineAddElementMenu from "./RubricOutlineAddElementMenu";

class RubricOutlineElement extends Component {
  constructor (props) {
    super(props);

    this.state = {
      selected: false,
      showMenu: false,
    }
  }

  onClickElement = () => {
    this.setState({
      selected: true,
    })

    this.props.setSelectedElement(this.props.data.id);
  }

  render () {
    let classNames = `${styles.outlineElementContainer}`;
    if (this.props.selectedElement != null) {
      classNames += " " + (this.props.data.id === this.props.selectedElement &&
        `${styles.outlineElementContainerSelected}`);
    }

    return (
      <div>
        <div onClick={this.onClickElement}>
          <div className={classNames}>
            <div className={styles.outlineElement} style={{paddingLeft: `${this.props.padding}rem`}}>
              <div className={styles.outlineElementLeft}>
                <div className={styles.outlineElementIcon}>
                  {isBlock(this.props.data.type) ?
                    (this.props.collapsed ?
                      <FaChevronRight onClick={this.props.onClickBlockCollapse}/>
                      :
                      <FaChevronDown onClick={this.props.onClickBlockCollapse}/>
                    )
                    :
                    <FaHandPointRight/>
                  }
                </div>
                <div>
                  {this.props.data.title}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    )
  }
}


const mapStateToProps = state => {
  return {
    selectedElement: state.rubricNew.selectedElement
  };
};

const actionCreators = {
  setSelectedElement,
  addBlock,
  addCriterion
}

export default connect(mapStateToProps, actionCreators)(RubricOutlineElement)