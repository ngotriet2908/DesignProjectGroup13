import {Button, Modal, Form, InputGroup, FormControl, Card, Alert, ListGroup, Spinner} from 'react-bootstrap'
import React, {Component} from "react";
import {request} from "../../../services/request";
import globalStyles from "../../helpers/global.module.css";
import {IoCheckmark, IoCheckmarkSharp, IoCloseOutline, IoAddOutline} from "react-icons/io5";
import styles from "../submissionDetails.module.css";
import classnames from "classnames";
import LabelModalCreateLabel from "./LabelModalCreateLabel";
import {colorToStyles, colorStyles, colors} from "./LabelRow";


class LabelModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      labels: [],
      isLoaded: false,
      selected: [],

      isCreating: false,
    }

    this.formRef = React.createRef()
  }

  fetchLabels = () => {
    Promise.all([
      request(`/api/courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/labels`)
    ])
      .then(async([res1]) => {
        const labels = await res1.json();

        let selected = labels.filter(label => {
          return this.props.currentLabels.find(currentLabel => {return currentLabel.id === label.id})
        })

        // load submission
        this.setState({
          labels: labels,
          isLoaded: true,
          selected: selected,
        });
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  toggleIsCreating = () => {
    this.setState(prevState => ({
      isCreating: !prevState.isCreating
    }))
  }

  onClose = () => {
    this.setState({
      labels: [],
      isLoaded: false,
      selected: [],
      isCreating: false,
    })

    this.props.toggleShow();
  }

  onAccept = () => {
    console.log(this.state.selected);

    request(`/api/courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.routeParams.submissionId}/labels`,
      "PUT",
      this.state.selected)
      .then(async () => {
        this.props.replaceLabels(this.state.selected);

        this.setState({
          labels: [],
          isLoaded: false,
          selected: [],
        })

        this.props.toggleShow();
      })
  }

  handleColorClick = (label, prevSelectedState) => {
    if (prevSelectedState) {
      this.setState(prevState => ({
        selected: prevState.selected.filter(selectedLabel => {return selectedLabel.id !== label.id}),
      }))
    } else {
      this.setState(prevState => ({
        selected:[...prevState.selected, label],
      }))
    }
  }

  appendCreatedLabel = (label) => {
    this.setState(prevState => ({
      labels: [...prevState.labels, label]
    }))
  }

  render() {
    return(
      <Modal
        centered
        backdrop="static"
        size="lg"
        onShow={this.fetchLabels}
        show={this.props.show}
        onHide={this.onClose}
        animation={false}
      >
        {!this.state.isCreating ?
          <div className={globalStyles.modalContainer}>
            <div className={globalStyles.modalHeaderContainer}>
              <h2>Labels</h2>
              <div className={classnames(globalStyles.modalHeaderContainerButton)} onClick={this.onClose}>
                <IoCloseOutline size={30}/>
              </div>
            </div>

            <div className={globalStyles.modalDescriptionContainer}>
              <div>Select from existing labels or create a new one</div>
            </div>

            {/* TODO : here should go the search bar */}

            {!this.state.isLoaded ?
              <div className={globalStyles.modalSpinnerContainer}>
                <Spinner className={globalStyles.modalSpinner} animation="border" role="status">
                  <span className="sr-only">Loading...</span>
                </Spinner>
              </div>
              :
              //body
              <div className={globalStyles.modalBodyContainer}>
                {this.state.labels.length === 0 &&
                <div className={classnames(globalStyles.modalBodyContainerRow, globalStyles.modalBodyContainerRowEmpty)}>
                  No labels available in this project
                </div>
                }

                {this.state.labels.map(label => {
                  let isSelected = this.state.selected.find(selectedLabel => {
                    return selectedLabel.id === label.id;
                  })

                  return(
                    <div key={label.id} className={classnames(styles.labelModalColorRow, colorToStyles[label.color])}
                      onClick={() => this.handleColorClick(label, isSelected)}>
                      <span>{label.name}</span>
                      {isSelected &&
                        <IoCheckmarkSharp size={20}/>
                      }
                    </div>
                  )
                })}
              </div>
            }

            <div className={classnames(globalStyles.modalFooterContainer, globalStyles.modalFooterContainerSpaceBetween)}>
              <div>
                <Button variant="yellow" onClick={this.toggleIsCreating}><IoAddOutline size={20}/> Create</Button>
              </div>
              <div className={globalStyles.modalFooterContainerButtonGroup}>
                <Button variant="linkLightGray" onClick={this.onClose}>Cancel</Button>
                <Button variant="lightGreen" onClick={this.onAccept}>Save</Button>
              </div>
            </div>
          </div>
          :
          <div className={globalStyles.modalContainer}>
            <LabelModalCreateLabel
              closeModal={this.onClose}
              isCreating={this.state.isCreating}
              toggleIsCreating={this.toggleIsCreating}
              appendCreatedLabel={this.appendCreatedLabel}
              routeParams={this.props.routeParams}
            />
          </div>
        }
      </Modal>
    )
  }
}

export default LabelModal;