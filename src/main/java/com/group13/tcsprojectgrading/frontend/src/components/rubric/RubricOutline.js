import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import RubricOutlineGroup from "./RubricOutlineGroup";
import {FaMinus, FaPlus} from "react-icons/fa";
import {setCurrentPath, setSelectedElement} from "../../redux/rubric/actions";


class RubricOutline extends Component {
  constructor (props) {
    super(props);

    this.padding = 0;

    this.path = "";
  }

  onClickElement = (id, path) => {
    this.props.setSelectedElement(id);
    this.props.setCurrentPath(path);
  }

  render () {
    let classNames = `${styles.outlineElementContainer}`;
    if (this.props.selectedElement != null) {
      classNames += " " + (this.props.rubric.id === this.props.selectedElement &&
        `${styles.outlineElementContainerSelected}`);
    }

    return (
      <div className={styles.outlineContainer}>
        <div className={styles.outlineHeader} onClick={() => this.onClickElement(this.props.rubric.id, "")}>
          <div className={classNames}>
            <h4>Rubric</h4>
            <div>{this.props.isEditing && (<span>Editing</span>)}</div>
          </div>
        </div>

        {this.props.rubric != null ?
          <div>
            <RubricOutlineGroup path={this.path + "/children"} onClickElement={this.onClickElement} padding={this.padding} data={this.props.rubric.children}/>
          </div>
          :
          <div>
            Empty
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
    isEditing: state.rubric.isEditing
  };
};

const actionCreators = {
  setSelectedElement,
  setCurrentPath
}

export default connect(mapStateToProps, actionCreators)(RubricOutline)