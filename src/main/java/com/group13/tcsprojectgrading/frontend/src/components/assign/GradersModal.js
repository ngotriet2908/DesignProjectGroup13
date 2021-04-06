import React, {Component} from "react";
import {IoCheckboxOutline, IoCloseOutline, IoSquareOutline} from "react-icons/io5";
import classnames from "classnames";
import {request} from "../../services/request";
import {BASE, PROJECT, USER_COURSES} from "../../services/endpoints";
import {isTeacher} from "../permissions/functions";
import globalStyles from '../helpers/global.module.css';
import Modal from "react-bootstrap/Modal";
import Spinner from "react-bootstrap/Spinner";
import {IoCheckmarkSharp} from "react-icons/io5";
import Button from "react-bootstrap/Button";
import {colorToStyles} from "../submissionDetails/labels/LabelRow";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {connect} from "react-redux";


class GradersModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      graders: [],
      isLoaded: false,
      selected: [],

      initial: [],

      add: [],
      remove: []
    }
  }

  fetchGraders = () => {
    Promise.all([
      request(`/api/courses/${this.props.routeParams.courseId}/graders`)
    ])
      .then(async([res1]) => {
        const graders = await res1.json();

        let selected = graders.filter(grader => {
          return this.props.currentGraders.find(currentGrader => {return currentGrader.id === grader.id})
        })

        // let selected = labels.filter(label => {
        //   return this.props.currentLabels.find(currentLabel => {return currentLabel.id === label.id})
        // })

        // load submission
        this.setState({
          graders: graders,
          // labels: labels,
          isLoaded: true,
          selected: selected,
          initial: selected,
        });
      })
      .catch(error => {
        console.error(error.message);
      });
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
        onShow={this.fetchGraders}
        show={this.props.show}
        onHide={this.onClose}
        animation={false}
      >
        <div className={globalStyles.modalContainer}>
          <div className={globalStyles.modalHeaderContainer}>
            <h2>Graders</h2>
            <div className={classnames(globalStyles.modalHeaderContainerButton)} onClick={this.onClose}>
              <IoCloseOutline size={30}/>
            </div>
          </div>

          <div className={globalStyles.modalDescriptionContainer}>
            <div>Select people responsible for grading. All assigned submissions of the removed graders will be moved to the 'unassigned' list.</div>
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

              {this.state.graders.length === 0 &&
              <div className={classnames(globalStyles.modalBodyContainerRow, globalStyles.modalBodyContainerRowEmpty)}>
                No graders available in this project
              </div>
              }

              {this.state.graders.map(grader => {
                let isSelected = this.state.selected.find(selectedGrader => {
                  return selectedGrader.id === grader.id;
                })

                let isUser = this.props.user.id === grader.id;

                return(
                  <div className={classnames(globalStyles.modalBodyContainerRow,
                    isSelected && globalStyles.modalBodyContainerRowActive,
                    isUser && globalStyles.modalBodyContainerRowActiveDisabled)
                  } key={grader.id}
                  onClick={() => this.handleGraderClick(grader, isSelected, isUser)}>
                    {isSelected ?
                      <IoCheckboxOutline size={16}/>
                      :
                      <IoSquareOutline size={16}/>
                    }
                    <span>{grader.name}</span>
                  </div>
                )
              })}

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

export default connect(mapStateToProps, actionCreators)(GradersModal)