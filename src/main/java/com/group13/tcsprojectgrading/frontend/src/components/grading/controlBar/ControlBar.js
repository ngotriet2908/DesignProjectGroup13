import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";
import {IoFlagOutline} from "react-icons/io5";
import FlagModal from "./FlagModal";
import {Badge} from 'react-bootstrap'
import {request} from "../../../services/request";


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

  // handleAddFlag = (flag) => {
  //   request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}/flag`
  //     , "POST", flag)
  //     .then((response) => {
  //       return response.json()
  //     })
  //     .then((flags) => {
  //       let tmp = {...this.state.data}
  //       tmp.submission.flags = flags
  //       this.setState({
  //         data : tmp
  //       })
  //     })
  // }
  //
  // handleRemoveFlag = (flag) => {
  //   request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}/flag/${flag.id}`
  //     , "DELETE")
  //     .then((response) => {
  //       return response.json()
  //     })
  //     .then((data) => {
  //       let tmp = {...this.state.data}
  //       tmp.submission.flags = data
  //       this.setState({
  //         data : tmp
  //       })
  //     })
  // }

  // createFlagHandler = async (name, description, variant) => {
  //   let object = {
  //     "name": name,
  //     "description": description,
  //     "variant": variant,
  //   }
  //   let data = await request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}/flag/create`
  //     , "POST", object)
  //     .then((response) => {
  //       return response.json()
  //     })
  //     .then((data) => {
  //       return data
  //     })
  //
  //   console.log(data)
  //   if (data.error !== undefined) {
  //     return "error: " + data.error
  //   } else {
  //     let tmp = {...this.state.data}
  //     tmp.user.flags = data.data
  //     this.setState({
  //       data : tmp
  //     })
  //     return "ok";
  //   }
  // }
  //
  // removeFlagHandler = async (id) => {
  //   let data = await request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/flag/${id}`
  //     , "DELETE")
  //     .then((response) => {
  //       return response.json()
  //     })
  //     .then((data) => {
  //       return data
  //     })
  //
  //   console.log(data)
  //   if (data.error !== undefined) {
  //     return "error: " + data.error
  //   } else {
  //     let tmp = {...this.state.data}
  //     tmp.user.flags = data.data
  //     this.setState({
  //       data : tmp
  //     })
  //     return "ok";
  //   }
  // }

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
