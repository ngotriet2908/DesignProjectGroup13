import React, {Component} from 'react'

import rubricStyles from '../../rubric/rubric.module.css';
import styles from '../grading.module.css'
import {connect} from "react-redux";
import {setSelectedElement} from "../../../redux/rubric/actions";
import RubricOutlineGroup from "../../rubric/RubricOutlineGroup";


class RubricViewerOutline extends Component {
  constructor (props) {
    super(props);
    this.padding = 0;
  }

  onClickElement = (id) => {
    this.props.hiddenCallback(true);
    this.props.setSelectedElement(id);
  }

  render () {
    let classNames = `${rubricStyles.outlineElementContainer}`;
    if (this.props.selectedElement != null) {
      classNames += " " + (this.props.rubric.id === this.props.selectedElement &&
        `${rubricStyles.outlineElementContainerSelected}`);
    }

    let hidden = this.props.isOutlineHidden ? ` ${styles.rubricViewerOutlineHidden}` : "";

    return (
      <div className={`${rubricStyles.outlineContainer} ${styles.rubricViewerOutline}${hidden}`}>
        <div className={rubricStyles.outlineHeader} onClick={() => this.onClickElement(this.props.rubric.id)}>
          <div className={classNames}>
            <h4>Rubric</h4>
            <div>{this.props.isEditing && (<span>Editing</span>)}</div>
          </div>
        </div>

        {this.props.rubric != null ?
          <div>
            <RubricOutlineGroup onClickElement={this.onClickElement} padding={this.padding} data={this.props.rubric.children}/>
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
  setSelectedElement
}

export default connect(mapStateToProps, actionCreators)(RubricViewerOutline)