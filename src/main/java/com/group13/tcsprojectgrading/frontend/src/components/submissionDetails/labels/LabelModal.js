import React, {Component} from "react";
import {request} from "../../../services/request";
import globalStyles from "../../helpers/global.module.css";
import styles from "../submissionDetails.module.css";
import classnames from "classnames";
import LabelModalCreateLabel from "./LabelModalCreateLabel";
import withTheme from "@material-ui/core/styles/withTheme";
import Button from "@material-ui/core/Button";
import AddIcon from '@material-ui/icons/Add';
import Dialog from "@material-ui/core/Dialog";
import Card from "@material-ui/core/Card";
import {CardContent} from "@material-ui/core";
import IconButton from "@material-ui/core/IconButton";
import CloseIcon from "@material-ui/icons/Close";
import CircularProgress from "@material-ui/core/CircularProgress";
import DoneIcon from '@material-ui/icons/Done';


class LabelModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      labels: [],
      isLoaded: false,
      selected: [],

      isCreating: false,
    }
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

  body = () => {
    return (
      <>



      </>
    )
  }


  render() {
    return(
      <Dialog
        onClose={this.onClose}
        aria-labelledby="customized-dialog-title"
        open={this.props.show}
        PaperComponent={Card}
        scroll={"paper"}
        fullWidth={true}
        onEntered={this.fetchLabels}
        disableBackdropClick={true}
        disableEscapeKeyDown={true}
      >
        {!this.state.isCreating ?
          <CardContent>
            <div className={globalStyles.modalHeaderContainer}>
              <h2>Labels</h2>

              <IconButton aria-label="close" className={globalStyles.modalCloseButton} onClick={this.onClose}>
                <CloseIcon/>
              </IconButton>
            </div>

            {!this.state.isLoaded ?
              <div className={globalStyles.modalEmptyBodyContainer}>
                <CircularProgress className={globalStyles.spinner}/>
              </div>
              :
              (
                <>
                  {/* description */}
                  <div className={globalStyles.modalDescriptionContainer}>
                    <div>
                        Select from existing labels or create a new one
                    </div>
                  </div>

                  {/* body */}
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
                        <div
                          key={label.id}
                          className={classnames(styles.labelModalColorRow)}
                          style={{
                            backgroundColor: this.props.theme.palette.labels[label.color]
                          }}
                          onClick={() => this.handleColorClick(label, isSelected)}>
                          <span>{label.name}</span>
                          {isSelected &&
                            <DoneIcon/>
                          }
                        </div>
                      )
                    })}
                  </div>

                  {/* footer */}
                  <div
                    className={classnames(globalStyles.modalFooterContainer, globalStyles.modalFooterContainerSpaceBetween)}>

                    <Button
                      disableElevation
                      onClick={this.toggleIsCreating}
                      style={{backgroundColor: this.props.theme.palette.additionalColors.yellow, color: "white"}}
                      startIcon={<AddIcon/>}
                    >
                        Create
                    </Button>

                    <div className={classnames(globalStyles.modalFooterContainerButtonGroup)}>
                      <Button disableElevation onClick={this.onClose}>
                          Cancel
                      </Button>

                      <Button variant="contained" color="primary" disableElevation onClick={this.onAccept}>
                          Save
                      </Button>
                    </div>
                  </div>
                </>
              )
            }
          </CardContent>

          :
          // create label
          <CardContent>
            <LabelModalCreateLabel
              closeModal={this.onClose}
              isCreating={this.state.isCreating}
              toggleIsCreating={this.toggleIsCreating}
              appendCreatedLabel={this.appendCreatedLabel}
              routeParams={this.props.routeParams}
            />
          </CardContent>
        }
      </Dialog>
    )
  }
}

export default withTheme(LabelModal);