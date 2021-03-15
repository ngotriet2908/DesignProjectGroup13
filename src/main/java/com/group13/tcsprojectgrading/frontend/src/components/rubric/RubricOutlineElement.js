import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import {addBlock, addCriterion, setSelectedElement} from "../../redux/rubric/actions";
import {FaChevronDown, FaChevronRight, FaHandPointRight} from "react-icons/fa";
import {isBlock} from "./helpers";

class RubricOutlineElement extends Component {
  constructor (props) {
    super(props);

    this.state = {
      showMenu: false,
    }
  }

  render () {
    let classNames = `${styles.outlineElementContainer}`;
    if (this.props.selectedElement != null) {
      classNames += " " + (this.props.data.id === this.props.selectedElement &&
        `${styles.outlineElementContainerSelected}`);
    }

    return (
      <div>
        <div onClick={() => this.props.onClickElement(this.props.data.id)}>
          <div className={classNames}>
            <div className={styles.outlineElement} style={{paddingLeft: `${this.props.padding}rem`}}>
              <div className={styles.outlineElementLeft}>
                <div className={[styles.outlineElementIcon, !this.props.collapsed && styles.outlineElementIconRotated].join(" ")}>
                  {isBlock(this.props.data.type) ?
                  // (this.props.collapsed ?
                  //   <FaChevronRight onClick={this.props.onClickBlockCollapse}/>
                  //   :
                  //   <FaChevronDown onClick={this.props.onClickBlockCollapse}/>
                  // )
                    
                    // (this.props.collapsed ?
                    <FaChevronRight onClick={this.props.onClickBlockCollapse}/>


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
    selectedElement: state.rubric.selectedElement
  };
};

const actionCreators = {
  setSelectedElement,
  addBlock,
  addCriterion
}

export default connect(mapStateToProps, actionCreators)(RubricOutlineElement)