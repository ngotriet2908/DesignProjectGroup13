import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import Card from "react-bootstrap/Card";
import {findById} from "../../redux/rubric/functions";
import RubricEditorElement from "./RubricEditorElement";


class RubricEditor extends Component {
  constructor (props) {
    super(props);
  }

  render () {
    let element = findById(this.props.rubric, this.props.selectedElement);

    return (
      <div className={styles.editorContainer}>
        <Card className={styles.editorCard}>
          <Card.Body>
            {this.props.rubric != null && this.props.selectedElement != null ?
              <div>
                <RubricEditorElement data={element}/>
              </div>
              :
              <div>
                Open something in the outline panel to the left.
              </div>
            }
          </Card.Body>
        </Card>
      </div>
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

export default connect(mapStateToProps, actionCreators)(RubricEditor)


















