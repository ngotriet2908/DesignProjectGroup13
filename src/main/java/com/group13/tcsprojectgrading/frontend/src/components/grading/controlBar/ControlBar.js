import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";
import {Badge} from 'react-bootstrap'
import {request} from "../../../services/request";


class ControlBar extends Component {
  constructor (props) {
    super(props)

  }

  componentDidMount() {
  }

  render () {
    return (
      <div className={styles.controlBarContainer}>
        <div className={styles.controlBarBody}>
          <div>
            <h4>
              {this.props.data.submission.name}
            </h4>

          </div>
        </div>
        {/*</Card.Body>*/}
        {/*</Card>*/}

      </div>
    )
  }
}

const mapStateToProps = state => {
  return {

  };
};

const actionCreators = {

}

export default connect(mapStateToProps, actionCreators)(ControlBar)
