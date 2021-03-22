import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";
import {IoFlagOutline} from "react-icons/io5";
import FlagModal from "./FlagModal";
import {Badge} from 'react-bootstrap'


class ControlBar extends Component {
  constructor (props) {
    super(props)
    this.state = {
      flagModalShow: false
    }
  }

  componentDidMount() {
    this.setState({
      flagModalShow: false
    })
  }

  onFlagModalClose = () => {
    this.setState({
      flagModalShow: false
    })
  }

  onFlagModalShow = () => {
    this.setState({
      flagModalShow: true
    })
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
          <div>
            <div className={styles.controlBarButton} onClick={this.onFlagModalShow}>
              <IoFlagOutline size={26}/>
            </div>
          </div>
        </div>
        {/*</Card.Body>*/}
        {/*</Card>*/}
        <FlagModal show={this.state.flagModalShow}
          onClose = {this.onFlagModalClose}
          flags = {this.props.data.submission.flags}
          user = {this.props.data.user}
          addFlag = {this.props.addFlag}
          removeFlag = {this.props.removeFlag}
          createFlagHandler = {this.props.createFlagHandler}
          removeFlagHandler = {this.props.removeFlagHandler}
        />
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
