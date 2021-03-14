import React, {Component} from "react";
import {v4 as uuidv4} from "uuid";
import styles from "./rubric.module.css";
import Button from "react-bootstrap/Button";
import {connect} from "react-redux";

class RubricOutlineAddElementMenu extends Component {
  constructor (props) {
    super(props);

    this.wrapperRef = React.createRef();
  }

  componentDidMount() {
    document.addEventListener('mousedown', this.handleClickOutside);
  }

  componentWillUnmount() {
    document.removeEventListener('mousedown', this.handleClickOutside);
  }

  handleClickOutside = (event) => {
    console.log('You clicked.');
    if (this.wrapperRef && !this.wrapperRef.current.contains(event.target)) {
      console.log('You clicked outside of me!');
      this.props.handler(false);
    }
  }

  onClickNewBlock = () => {
    let newBlock = {
      content: {
        id: uuidv4(),
        type: "b",
        title: "Block" + Math.floor(Math.random() * Math.floor(100)),
      },
      children: []
    }

    this.props.handler(false);
    this.props.addBlockHandler(this.props.elementId, newBlock);
  }

  onClickNewCriterion = () => {
    let newCriterion = {
      content: {
        id: uuidv4(),
        type: "c",
        title: "Criterion" + Math.floor(Math.random() * Math.floor(100)),
        text: "Hello, I'm a new Criterion."
      }
    }

    this.props.handler(false);
    this.props.addCriterionHandler(this.props.elementId, newCriterion);
  }

  render () {
    return(
      <div className={styles.addElementMenu} ref={this.wrapperRef}>
        <Button variant="primary" onClick={this.onClickNewBlock}>
          New Section
        </Button>
        <Button variant="primary" onClick={this.onClickNewCriterion}>
          New Criterion
        </Button>
      </div>
    );
  }
}

export default RubricOutlineAddElementMenu;