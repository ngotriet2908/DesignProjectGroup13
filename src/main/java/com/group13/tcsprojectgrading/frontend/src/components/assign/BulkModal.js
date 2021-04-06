import React, {Component} from "react";
import {IoCheckboxOutline, IoCloseOutline, IoSquareOutline} from "react-icons/io5";
import classnames from "classnames";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import globalStyles from '../helpers/global.module.css';
import Modal from "react-bootstrap/Modal";
import Spinner from "react-bootstrap/Spinner";
import Button from "react-bootstrap/Button";
import {connect} from "react-redux";


class BulkModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      // submissions: [],
      // graders: [],
      isLoaded: false,
    }
  }

  onShow = () => {
    // set the selected grader
    this.setState({
      selected: this.props.currentGrader,
    })
  }

  onClose = () => {
    this.setState({
      graders: [],
      isLoaded: false,
      selected: [],
    })

    this.props.toggleShow();
  }

  onAccept = () => {
    let body = this.state.selected.map(grader => {
      return grader.id
    })

    request(`${BASE}courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/graders`,
      "PUT",
      body
    ).then(async (response) => {
      let data = await response.json();
      console.log(data);

      this.setState({
        labels: [],
        isLoaded: false,
        selected: [],
      })

      this.props.toggleShow();
      this.props.reloadPage();
    })
  }

  handleGraderClick = (grader, prevSelectedState, isUser) => {
    if (isUser) {
      return;
    }

    if (prevSelectedState) {
      this.setState(prevState => ({
        selected: prevState.selected.filter(selectedGrader => {return selectedGrader.id !== grader.id}),
      }))
    } else {
      this.setState(prevState => ({
        selected: [...prevState.selected, grader],
      }))
    }
  }

  render() {
    return(
      <Modal
        centered
        backdrop="static"
        size="lg"
        onShow={this.onShow}
        show={this.props.show}
        onHide={this.onClose}
        animation={false}
      >
        <div className={globalStyles.modalContainer}>
          <div className={globalStyles.modalHeaderContainer}>
            <h2>Bulk assign</h2>
            <div className={classnames(globalStyles.modalHeaderContainerButton)} onClick={this.onClose}>
              <IoCloseOutline size={30}/>
            </div>
          </div>

          <div className={globalStyles.modalDescriptionContainer}>
            <div>Text here.</div>
          </div>

          {!this.state.isLoaded ?
            <div className={globalStyles.modalSpinnerContainer}>
              <Spinner className={globalStyles.modalSpinner} animation="border" role="status">
                <span className="sr-only">Loading...</span>
              </Spinner>
            </div>
            :
            //body
            <div className={globalStyles.modalBodyContainer}>

              {this.props.graders.length === 0 &&
              <div className={classnames(globalStyles.modalBodyContainerRow, globalStyles.modalBodyContainerRowEmpty)}>
                No graders available in the project
              </div>
              }

            </div>
          }

          <div className={classnames(globalStyles.modalFooterContainer)}>
            <div className={globalStyles.modalFooterContainerButtonGroup}>
              <Button variant="linkLightGray" onClick={this.onClose}>Cancel</Button>
              <Button variant="lightGreen" onClick={this.onAccept}>Save</Button>
            </div>
          </div>
        </div>
      </Modal>
    )
  }
}

const actionCreators = {

}

const mapStateToProps = state => {
  return {
    user: state.users.self
  };
};

export default connect(mapStateToProps, actionCreators)(BulkModal)