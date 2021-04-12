import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import {findById} from "../../redux/rubric/functions";
import RubricEditorElement from "./RubricEditorElement";
import RubricViewerElement from "./RubricViewerElement";
import globalStyles from "../helpers/global.module.css";
import classnames from "classnames";


class RubricPaper extends Component {
  constructor (props) {
    super(props);
  }

  render () {
    let element = findById(this.props.rubric, this.props.selectedElement);

    return(
      <Card className={classnames(globalStyles.cardShadow, styles.viewer)}>
        <CardContent className={classnames(styles.viewerContainer)}>
          {this.props.isEditing ?
            <RubricEditorElement data={element}/>
            :
            <RubricViewerElement data={element}/>
          }
        </CardContent>
      </Card>
    )
  }
}

const mapStateToProps = state => {
  return {
    isEditing: state.rubric.isEditing,
    rubric: state.rubric.rubric,
    selectedElement: state.rubric.selectedElement
  };
};

const actionCreators = {

}

export default connect(mapStateToProps, actionCreators)(RubricPaper)


















