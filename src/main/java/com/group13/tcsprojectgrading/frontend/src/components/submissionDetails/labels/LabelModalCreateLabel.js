import React, {Component} from "react";
import styles from "../submissionDetails.module.css";
import globalStyles from "../../helpers/global.module.css";
import classnames from 'classnames';
import {request} from "../../../services/request";
import {colors} from "./LabelRow";
import withTheme from "@material-ui/core/styles/withTheme";
import IconButton from "@material-ui/core/IconButton";
import CloseIcon from "@material-ui/icons/Close";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import DoneIcon from '@material-ui/icons/Done';
import KeyboardArrowLeftIcon from '@material-ui/icons/KeyboardArrowLeft';


class LabelModalCreateLabel extends Component {
  constructor(props) {
    super(props);
    this.formRef = React.createRef()

    this.state = {
      selectedColor: 0,
      name: "",
      description: ""
    }
  }

  handleColorClick = (index) => {
    this.setState({
      selectedColor: index,
    })
  }

  onAccept = () => {
    let label = {
      name: this.state.name,
      description: "",
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
          <IconButton onClick={this.props.toggleIsCreating}>
            <KeyboardArrowLeftIcon/>
          </IconButton>

          <h2>Create label</h2>

          <IconButton aria-label="close" className={globalStyles.modalCloseButton} onClick={this.onClose}>
            <CloseIcon/>
          </IconButton>
        </div>

        <div className={globalStyles.modalDescriptionContainer}>
          <div>
                  Create a new shiny label
          </div>
        </div>

        {/* body */}
        <div className={globalStyles.modalBodyContainer}>

          <TextField
            className={styles.modalRow}
            label="Name"
            placeholder="Enter name"
            variant="outlined"
            fullWidth
            value={this.state.name}
            onChange={(event) => this.setState({name: event.target.value})}
          />

          {/*<TextField*/}
          {/*  className={styles.modalRow}*/}
          {/*  label="Description"*/}
          {/*  placeholder="Enter description of the issue"*/}
          {/*  multiline*/}
          {/*  variant="outlined"*/}
          {/*  fullWidth*/}
          {/*  rows={3}*/}
          {/*  value={this.state.description}*/}
          {/*  onChange={(event) => this.setState({description: event.target.value})}*/}
          {/*/>*/}

          <div className={styles.labelModalCreateColorsContainer}>
            {colors.map((color, index) => {
              const isSelected = this.state.selectedColor === index;

              return(
                <div
                  key={index}
                  className={classnames(styles.labelModalCreateColor)}
                  style={{
                    backgroundColor: this.props.theme.palette.labels[color]
                  }}
                  onClick={() => this.handleColorClick(index)}>
                  {isSelected &&
                  <DoneIcon/>
                  }
                </div>
              )
            })}
          </div>

        </div>

        {/* footer */}
        <div className={classnames(globalStyles.modalFooterContainer)}>

          <Button variant="contained" color="primary" disableElevation onClick={this.onAccept}>
                Create
          </Button>
        </div>
      </>
    )
  }
}

export default withTheme(LabelModalCreateLabel);