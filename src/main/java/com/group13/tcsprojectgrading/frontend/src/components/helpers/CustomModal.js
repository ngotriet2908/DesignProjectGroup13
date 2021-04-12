import React, {Component} from "react";
import globalStyles from '../helpers/global.module.css';
import classnames from "classnames";
import Dialog from "@material-ui/core/Dialog";
import Button from "@material-ui/core/Button";
import IconButton from "@material-ui/core/IconButton";
import CloseIcon from '@material-ui/icons/Close';

import Card from "@material-ui/core/Card";
import {CardContent} from "@material-ui/core";
import withTheme from "@material-ui/core/styles/withTheme";
import CircularProgress from "@material-ui/core/CircularProgress";


class ImportCourseModal extends Component {
  constructor(props) {
    super(props);

    this.state = this.props.state;
  }

  onShow = () => {
    this.props.onShow();
  }

  onClose = () => {
    this.props.onClose();
  }

  onAccept = () => {
    this.props.onAccept();
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
        onEntered={this.props.onShow}
        disableBackdropClick={true}
        disableEscapeKeyDown={true}
      >
        <CardContent>
          {/* header */}
          <div className={globalStyles.modalHeaderContainer}>
            <h2>{this.props.title}</h2>

            {this.props.isLoaded &&
            <IconButton aria-label="close" className={globalStyles.modalCloseButton} onClick={this.onClose}>
              <CloseIcon/>
            </IconButton>
            }
          </div>

          {!this.props.isLoaded ?
            <div className={globalStyles.modalEmptyBodyContainer}>
              <CircularProgress className={globalStyles.spinner}/>
            </div>
            :
            (
              <>
                {/* description */}
                <div className={globalStyles.modalDescriptionContainer}>
                  <div>
                    {this.props.description}
                  </div>
                </div>

                {/* body */}
                <div className={globalStyles.modalBodyContainer}>
                  {this.props.body}
                </div>

                {/* footer */}
                <div className={classnames(globalStyles.modalFooterContainer,
                  this.props.additionalAction && globalStyles.modalFooterContainerSpaceBetween)}>

                  {this.props.additionalAction &&
                    this.props.additionalAction
                  }

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
      </Dialog>
    )
  }
}

export default withTheme(ImportCourseModal);