import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";
import RubricOutline from "./RubricOutline";
import RubricViewer from "./RubricViewer";
import {
  addBlock, addCriterion,
  deleteAllElements, deleteElement,
  resetUpdates,
  saveRubric,
  saveRubricTemp,
  setCurrentPath,
  setEditingRubric,
  setSelectedElement
} from "../../../redux/rubric/actions";
import Card from "@material-ui/core/Card";
import classnames from "classnames";
import globalStyles from "../../helpers/global.module.css";
import CardContent from "@material-ui/core/CardContent";
import {isCriterion} from "../../rubric/helpers";
import KeyboardArrowLeftIcon from '@material-ui/icons/KeyboardArrowLeft';
import KeyboardArrowRightIcon from '@material-ui/icons/KeyboardArrowRight';
import {IconButton} from "@material-ui/core";
import KeyboardBackspaceIcon from '@material-ui/icons/KeyboardBackspace';


class RubricPanel extends Component {
  constructor (props) {
    super(props);

    this.state = {
      isOutlineHidden: false,
    }
  }

  toggleOutlineHidden = () => {
    this.setState(prevState => ({
      isOutlineHidden: !prevState.isOutlineHidden
    }))
  }

  handlePrevCriterion = (id) => {
    let nextId = -1

    this.props.rubricCriteria.forEach((criterion, i) => {
      if (criterion.id === id) {
        nextId = i - 1
      }
    })

    if (nextId >= 0) {
      this.props.setSelectedElement(this.props.rubricCriteria[nextId].id);
    }
  }

  handleNextCriterion = (id) => {
    if (id === this.props.rubric.id) {
      if (!this.outlineHidden) {
        this.toggleOutlineHidden()
      }

      this.props.setSelectedElement(this.props.rubricCriteria[0].id);
      return;
    }

    let nextId = -1

    this.props.rubricCriteria.forEach((criterion, i) => {
      if (criterion.id === id) {
        nextId = i + 1
      }
    })

    if (nextId >= 0 && nextId < this.props.rubricCriteria.length) {
      this.props.setSelectedElement(this.props.rubricCriteria[nextId].id);
    }
  }

  render () {
    return (
      <div className={styles.rubricPanelContainer}>
        <Card className={classnames(styles.panelCard, globalStyles.cardShadow)}>
          <CardContent className={styles.gradeViewerBody}>
            <div className={styles.gradingCardTitle}>
              {!this.state.isOutlineHidden ?
                <h4>Rubric</h4>
                :
                <IconButton
                  onClick={() => this.toggleOutlineHidden()}
                >
                  <KeyboardBackspaceIcon/>
                </IconButton>
              }

              <div className={styles.buttonGroup}>
                <IconButton
                  onClick={() => this.handlePrevCriterion(this.props.selectedElement)}
                  disabled={
                    this.props.selectedElement == null ||
                    this.props.rubricCriteria.length === 0 ||
                    this.props.rubricCriteria[0].id === this.props.selectedElement ||
                    this.props.selectedElement === this.props.rubric.id
                  }
                >
                  <KeyboardArrowLeftIcon/>
                </IconButton>

                <IconButton
                  onClick={() => this.handleNextCriterion(this.props.selectedElement)}
                  disabled={
                    this.props.selectedElement == null ||
                    this.props.rubricCriteria.length === 0 ||
                    this.props.rubricCriteria[this.props.rubricCriteria.length - 1].id === this.props.selectedElement
                  }
                >
                  <KeyboardArrowRightIcon/>
                </IconButton>
              </div>
            </div>

            <div className={styles.rubricPanelInnerContainer}>
              <RubricOutline
                outlineHidden={this.state.isOutlineHidden}
                toggleOutlineHidden={this.toggleOutlineHidden}
              />

              <RubricViewer
                outlineHidden={this.state.isOutlineHidden}
                toggleOutlineHidden={this.toggleOutlineHidden}
              />
            </div>
          </CardContent>
        </Card>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
    selectedElement: state.rubric.selectedElement,
  };
};

const actionCreators = {
  setSelectedElement,
  setCurrentPath,
  setEditingRubric,
  saveRubric,
  saveRubricTemp,
  resetUpdates,
  deleteAllElements,
  addBlock,
  addCriterion,
  deleteElement,
}

export default connect(mapStateToProps, actionCreators)(RubricPanel)
