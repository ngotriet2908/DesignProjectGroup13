import React, {Component} from "react";
import styles from "../submissionDetails.module.css";
import {Form, ListGroupItem} from "react-bootstrap";
import OverlayTrigger from "react-bootstrap/OverlayTrigger";
import Badge from "react-bootstrap/Badge";
import Tooltip from "react-bootstrap/Tooltip";
import Button from "react-bootstrap/Button";
import {IoAdd, IoChevronBack, IoCheckmark, IoCloseOutline, IoCheckmarkSharp} from "react-icons/io5";
import globalStyles from "../../helpers/global.module.css";
import classnames from 'classnames';
import {request} from "../../../services/request";
import {colors, colorStyles} from "./LabelRow";


class LabelModalCreateLabel extends Component {
  constructor(props) {
    super(props);
    this.formRef = React.createRef()

    this.state = {
      selectedColor: 0,
    }
  }

  handleColorClick = (index) => {
    this.setState({
      selectedColor: index,
    })
  }

  onAccept = () => {
    let label = {
      name: this.formRef.current.nameInput.value,
      description: this.formRef.current.descriptionInput.value,
      color: colors[this.state.selectedColor],
    }

    request(`/api/courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/labels`
      , "POST", label)
      .then(async (response) => {
        let createdLabel = await response.json();

        this.props.appendCreatedLabel(createdLabel);
        this.props.toggleIsCreating();
      })
  }

  render() {
    return (
      <>
        <div className={globalStyles.modalHeaderContainer}>
          <div>
            <div className={classnames(globalStyles.iconButton, styles.gradingCardTitleButton)}
              onClick={this.props.toggleIsCreating}>
              <IoChevronBack size={26}/>
            </div>

            <h2>Create label</h2>
          </div>
          <div className={globalStyles.modalHeaderContainerButton} onClick={this.props.closeModal}>
            <IoCloseOutline size={30}/>
          </div>
        </div>

        <div className={globalStyles.modalBodyContainer}>
          <Form ref={this.formRef}>
            <Form.Group controlId="nameInput">
              <Form.Label>Name</Form.Label>
              <Form.Control type="text" placeholder="Name" />
            </Form.Group>

            <Form.Group controlId="descriptionInput">
              <Form.Label>Description</Form.Label>
              <Form.Control as="textarea" rows={3} placeholder="Enter a description here"/>
            </Form.Group>

            <Form.Group controlId="colorSelector">
              <Form.Label>Color</Form.Label>

              <div className={styles.labelModalCreateColorsContainer}>
                {colors.map((color, index) => {
                  const isSelected = this.state.selectedColor === index;

                  return(
                    <div key={index} className={classnames(styles.labelModalCreateColor, colorStyles[index])} onClick={() => this.handleColorClick(index)}>
                      {isSelected &&
                        <IoCheckmarkSharp size={20}/>
                      }
                    </div>
                  )
                })}
              </div>

            </Form.Group>

          </Form>
        </div>

        <div className={globalStyles.modalFooterContainer}>
          <div className={globalStyles.modalFooterContainerButtonGroup}>
            <Button variant="lightGreen" onClick={this.onAccept}>Create</Button>
          </div>
        </div>
      </>
    );
  }
}

export default LabelModalCreateLabel