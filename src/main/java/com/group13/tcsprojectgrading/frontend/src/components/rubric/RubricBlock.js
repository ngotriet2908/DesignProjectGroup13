import React, { Component } from 'react'

import styles from './rubric.module.css'
import RubricCriterion from "./RubricCriterion";

import { Droppable } from "react-beautiful-dnd";
import Button from "react-bootstrap/Button";

import { v4 as uuidv4 } from 'uuid';
import {addCriterion, alterBlockTitle, removeBlock} from "../../redux/rubric/actions";
import {connect} from "react-redux";

import { FaPencilAlt, FaSave, FaPlus, FaTimes, FaTrashAlt } from "react-icons/fa";
import Card from "react-bootstrap/Card";

class RubricBlock extends Component {
  constructor (props) {
    super(props)
    this.state = {
      // tempTitle: this.props.data.title,
      showEditor: false,
    }
  }

  onClickRemoveComponent = (id) => {
    this.setState(prevState => ({
      data: prevState.data.filter(el => el !== id )
    }));
  }

  onClickEditTitle = () => {
    this.setState(prevState => ({
      showEditor: true,
    }))
  }

  // onClickSaveTitle = () => {
  //   this.setState(prevState => ({
  //     showEditor: false,
  //   }))
  //
  //   this.props.alterBlockTitle(this.state.tempTitle, this.props.data.id);
  // }
  //
  // onClickCancelTitle = () => {
  //   this.setState(prevState => ({
  //     showEditor: false,
  //     tempTitle: this.props.data.title,
  //   }))
  // }

  onChangeTitle = (event) => {
    // this.setState({
    //   tempTitle: event.target.value,
    // })
    this.props.alterBlockTitle(event.target.value, this.props.data.id);
  }

  onClickAddCriterion = () => {
    let newCriterion = {
      id: uuidv4(),
      title: "Criterion New",
      text: "Hey, I'm a new Criterion!"
    }

    this.props.addCriterion(newCriterion, this.props.data.id);
  }

  onClickRemoveBlock = () => {
    this.props.removeBlock(this.props.data.id);
  }

  render () {
    if (!this.props.isEditing) {
      // standard mode
      return (
        <div>
          <Card className={styles.blockCard}>
            <Card.Body>
              <h3>{this.props.data.title}</h3>
            </Card.Body>
          </Card>
          <div className={styles.blockContentContainer}>
            {this.props.data.criteria.length > 0 ?
              (this.props.data.criteria.map((criterion, index) => {
                return (
                  <RubricCriterion
                    blockId={this.props.data.id}
                    key={criterion.id}
                    data={criterion}
                    index={index}
                  />
                )
              }))
              :
            // no criteria in the block
              <div>
                <Card className={[styles.criterionCard, styles.noCriteriaPlaceholder]}>
                  <Card.Body>
                  No criteria
                  </Card.Body>
                </Card>
              </div>
            }
          </div>
        </div>
      );
    } else {
      // editing mode
      return (
        <div>
          <Card className={styles.blockCard}>
            <Card.Body>
              <div className={styles.blockCardTitle}>
                <input type="text" name="name" value={this.props.data.title} onChange={this.onChangeTitle}/>
                <div>
                  <Button variant="primary" onClick={this.onClickAddCriterion}><FaPlus/></Button>
                  <Button variant="danger" onClick={this.onClickRemoveBlock}><FaTrashAlt/></Button>
                </div>
              </div>
            </Card.Body>
          </Card>

          <div className={styles.blockContentContainer}>
            <Droppable
              key={this.props.data.id}
              droppableId={`${this.props.data.id}`}>
              {(provided, snapshot) => (
                <div
                  ref={provided.innerRef}
                  {...provided.droppableProps}
                >
                  {this.props.data.criteria.length > 0 ?
                    (this.props.data.criteria.map((criterion, index) => {
                      return (
                        <RubricCriterion
                          blockId={this.props.data.id}
                          key={criterion.id}
                          data={criterion}
                          index={index}
                        />
                      )
                    }))
                    :
                    <div>
                      <Card className={[styles.criterionCard, styles.noCriteriaPlaceholder]}>
                        <Card.Body>
                          No criteria
                        </Card.Body>
                      </Card>
                    </div>
                  }
                  {provided.placeholder}
                </div>
              )}
            </Droppable>
          </div>
        </div>
      )
    }
  }
}

const mapStateToProps = state => {
  return {
    isEditing: state.rubric.isEditing,
  };
};

const actionCreators = {
  addCriterion,
  alterBlockTitle,
  removeBlock
}

export default connect(mapStateToProps, actionCreators)(RubricBlock)
