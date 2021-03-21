import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import {addBlock, addCriterion, setSelectedElement} from "../../redux/rubric/actions";
import {FaChevronDown, FaChevronRight, FaHandPointRight} from "react-icons/fa";
import {IoCheckmarkOutline} from "react-icons/io5";
import {isBlock, isCriterion} from "./helpers";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";

class RubricOutlineElement extends Component {
  constructor (props) {
    super(props);

    this.state = {
      showMenu: false,
    }
  }

  getIcon = () => {
    // if (this.props.isInGrading && this.props.isGraded) {
    //   return <IoCheckmarkOutline/>
    // } else {
    return <FaHandPointRight/>
    // }
  }

  onClick = () => {
    console.log(this.props.path);
    this.props.onClickElement(this.props.data.id, this.props.path);
  }

  render () {
    let classNames = `${styles.outlineElementContainer}`;
    if (this.props.selectedElement != null) {
      classNames += " " + (this.props.data.id === this.props.selectedElement &&
        `${styles.outlineElementContainerSelected}`);
    }

    return (
      <div>
        <div onClick={this.onClick}>
          <div className={classNames}>
            <div className={styles.outlineElement} style={{paddingLeft: `${this.props.padding}rem`}}>
              <div className={styles.outlineElementLeft}>
                <div className={[styles.outlineElementIcon, !this.props.collapsed && styles.outlineElementIconRotated].join(" ")}>
                  {isBlock(this.props.data.type) ?
                    <FaChevronRight onClick={this.props.onClickBlockCollapse}/>
                    :
                    this.getIcon()
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
  let isInGrading = state.navigation.location === LOCATIONS.grading;

  let isGraded = isInGrading && state.grading.assessment.grades[state.rubric.selectedElement] &&
    state.grading.assessment.grades[state.rubric.selectedElement].history.length > 0

  if (isInGrading) {
    return {
      isInGrading: isInGrading,
      selectedElement: state.rubric.selectedElement,
      isGraded: isGraded
    };
  } else {
    return {
      isInGrading: false,
      selectedElement: state.rubric.selectedElement,
    };
  }
};

const actionCreators = {
  setSelectedElement,
  addBlock,
  addCriterion
}

export default connect(mapStateToProps, actionCreators)(RubricOutlineElement)