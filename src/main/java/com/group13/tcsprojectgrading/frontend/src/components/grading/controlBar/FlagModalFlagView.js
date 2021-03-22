import {Button, Modal, Form, InputGroup, FormControl, Card, Alert, ListGroup} from 'react-bootstrap'
import React, {Component} from "react";
import styles from "../grading.module.css"

class FlagModalFlagView extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isExpand: false,
    }
  }

  onClickHandler = () => {
    this.setState((prevState) => {
      return {isExpand: !prevState.isExpand}
    })
  }

  render() {
    return(
      <div className={styles.flagModalFlagView}>
        {
          (!this.state.isExpand)?
            <h6 className={styles.flagModalFlagViewH6}>{this.props.flag.name}</h6>
            :
            <div >
              <h6>name: {this.props.flag.name}</h6>
              <h6>description: {this.props.flag.description}</h6>
            </div>
        }

        <div className={styles.flagModalFlagViewButtonGroup}>
          <Button className={styles.flagModalFlagViewButton} size="sm" onClick={this.onClickHandler}> {(this.state.isExpand)? "collapse" : "expand"} </Button>
          {(this.props.current)?
            (!this.props.flag.changeable)?
              null :
              <Button className={styles.flagModalFlagViewButton} variant="danger" size="sm" onClick={this.props.removeFlag}> set off </Button>
            :
            (<>
              <Button className={styles.flagModalFlagViewButton} size="sm" onClick={this.props.addFlag}> add </Button>
              <Button className={styles.flagModalFlagViewButton} variant="danger" size="sm" onClick={this.props.removeFlagPermanently}> remove </Button>
            </>)
          }
        </div>
      </div>
    )
  }
}

export default FlagModalFlagView