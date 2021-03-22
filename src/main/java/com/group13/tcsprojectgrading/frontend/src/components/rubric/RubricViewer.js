import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import Card from "react-bootstrap/Card";
import {findById} from "../../redux/rubric/functions";
import RubricEditorElement from "./RubricEditorElement";
import RubricViewerElement from "./RubricViewerElement";


class RubricViewer extends Component {
  constructor (props) {
    super(props);
  }

  render () {
    let element = findById(this.props.rubric, this.props.selectedElement);

    return (
      <Card className={styles.editorCard}>
        <Card.Body className={styles.editorCardBody}>
          <RubricViewerElement data={element}/>
        </Card.Body>
      </Card>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
    selectedElement: state.rubric.selectedElement
  };
};

const actionCreators = {

}

export default connect(mapStateToProps, actionCreators)(RubricViewer)


















