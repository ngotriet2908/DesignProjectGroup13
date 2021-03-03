import React, { Component } from 'react'

import styles from './rubric.module.css'
import RubricBlock from "./RubricBlock";
import {connect} from "react-redux";
import {
  appendBlock,
  saveRubric,
  moveCriterion,
  removeRubric,
  reorderCriterion,
  setEditingRubric,
  saveRubricBackup
} from "../../redux/rubric/actions";

import { DragDropContext } from 'react-beautiful-dnd';
import Button from "react-bootstrap/Button";
import {request} from "../../services/request";
import {BASE, RUBRIC_CURRENT} from "../../services/endpoints";

import { v4 as uuidv4 } from 'uuid';
import cloneDeep from 'lodash/cloneDeep';

import {FaPencilAlt, FaPlus, FaSave} from "react-icons/fa";
import Card from "react-bootstrap/Card";
import {Breadcrumb} from "react-bootstrap";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {URL_PREFIX} from "../../services/config";


class Rubric extends Component {
  constructor (props) {
    super(props);

    this.state = {
      loaded: false,
    }
  }

  componentDidMount() {
    request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId + "/rubric")
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);

        this.props.saveRubric(data.rubric);

        this.setState({
          loaded: true
        });
      })
      .catch(error => {
        console.error(error.message)

        this.setState({
          loaded: true
        });
      });
  }

  componentWillUnmount() {
    // TODO ask to save changes
    if (this.props.isEditing) {
      let rubricBackup = this.props.rubricCopy;
      this.props.saveRubricBackup(null);
      this.props.saveRubric(rubricBackup);
      this.props.setEditingRubric(false);
    }
  }

   onDragEnd = (result) => {
     const { source, destination } = result;

     // dropped outside the list
     if (!destination) {
       return;
     }
     const sBlockId = source.droppableId;
     const dBlockId = destination.droppableId;

     if (sBlockId === dBlockId) {
       this.props.reorderCriterion(sBlockId, source.index, destination.index);
     } else {
       this.props.moveCriterion(sBlockId, dBlockId, source.index, destination.index);
     }
   }

  onClickAddBlock = () => {
    let newBlock = {
      id: uuidv4(),
      criteria: [],
      title: "New Block #" + Math.floor(Math.random() * 99 + 1),
    }

    this.props.appendBlock(newBlock);
  }

  onClickEditRubric = () => {
    // create rubric backup
    let rubricCopy = cloneDeep(this.props.rubric);
    // store it
    this.props.saveRubricBackup(rubricCopy);
    // allow editing
    this.props.setEditingRubric(true);
  }

  onClickSaveRubric = () => {
    // send request
    request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId + "/rubric", "POST", this.props.rubric)
      .then(data => {
        console.log(data);
      })
      .catch(error => {
        console.error(error.message);
      });

    // change state
    this.props.saveRubricBackup(null);
    this.props.setEditingRubric(false);
  }

  onClickCancelSaveRubric = () => {
    // get rubric backup
    let rubricBackup = this.props.rubricCopy;
    this.props.saveRubricBackup(null);
    this.props.saveRubric(rubricBackup);
    this.props.setEditingRubric(false);
  }

  render () {
    return (
      <div className={styles.container}>
        {!this.state.loaded ?
          (<div>
            Loading...
          </div>)
          :
        // standard mode
          <div>
            {!this.props.isEditing ?
              <div>
                <div id={styles.rubricTitleContainer}>
                  <h1>Rubric</h1>
                  <Button variant="primary" onClick={this.onClickEditRubric}>Edit Rubric</Button>
                </div>
                <div id={styles.blocksContainer}>
                  {this.props.rubric.blocks.length > 0 ?
                    <DragDropContext onDragEnd={this.onDragEnd}>
                      {this.props.rubric.blocks.map((block, index) => {
                        return (
                          <RubricBlock
                            index={index}
                            data={block}
                            key={block.id}
                            droppableId={`${block.id}`}
                            onRemoveCriterion={this.removeCriterion}
                          />
                        )
                      })}
                    </DragDropContext>
                    :
                    <div>
                      <Card className={`${styles.criterionCard} ${styles.noCriteriaPlaceholder}`}>
                        <Card.Body>
                          No blocks
                        </Card.Body>
                      </Card>
                    </div>
                  }
                </div>
              </div>
              :
              // edit mode
              <div>
                <div id={styles.rubricTitleContainer}>
                  <h1>Rubric</h1>
                  <div>
                    <Button variant="success" onClick={this.onClickSaveRubric}>Save</Button>
                    <Button variant="danger" onClick={this.onClickCancelSaveRubric}>Cancel</Button>
                  </div>
                </div>
                <div id={styles.blocksContainer}>
                  {this.props.rubric.blocks.length > 0 ?
                    <DragDropContext onDragEnd={this.onDragEnd}>
                      {this.props.rubric.blocks.map((block, index) => {
                        return (
                          <RubricBlock
                            index={index}
                            data={block}
                            key={block.id}
                            droppableId={`${block.id}`}
                            onRemoveCriterion={this.removeCriterion}
                          />
                        )
                      })}
                    </DragDropContext>
                    :
                    <div>
                      <Card className={`${styles.criterionCard} ${styles.noCriteriaPlaceholder}`}>
                        <Card.Body>
                          No blocks
                        </Card.Body>
                      </Card>
                    </div>
                  }
                </div>
                <div id={styles.addBlockButtonContainer}>
                  <Button variant="primary" onClick={this.onClickAddBlock}>Add block</Button>
                </div>
              </div>
            }
          </div>
        }
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
    rubricCopy: state.rubric.rubricCopy,
    isEditing: state.rubric.isEditing,
  };
};

// TODO: dispatch two actions at once
// const mapDispatchToProps = dispatch => ({
//   action1: some_payload => dispatch(action1(some_payload))
//   action2: some_payload => dispatch(action2(some_payload))
// })

const actionCreators = {
  saveRubric,
  removeRubric,
  appendBlock,
  reorderCriterion,
  moveCriterion,
  setEditingRubric,
  saveRubricBackup
}

export default connect(mapStateToProps, actionCreators)(Rubric)


















