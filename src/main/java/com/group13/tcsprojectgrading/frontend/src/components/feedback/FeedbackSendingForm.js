import React, {Component} from "react";
import {request} from "../../services/request";
import {toast} from 'react-toastify'
import styles from './feedback.module.css';
import classnames from "classnames";
import CardContent from "@material-ui/core/CardContent";
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import Select from "@material-ui/core/Select";
import MenuItem from "@material-ui/core/MenuItem";
import withTheme from "@material-ui/core/styles/withTheme";
import Card from "@material-ui/core/Card";
import ImportExportIcon from "@material-ui/icons/ImportExport";
import Button from "@material-ui/core/Button";
import GroupIcon from '@material-ui/icons/Group';
import PersonIcon from '@material-ui/icons/Person';
import Badge from "@material-ui/core/Badge";


class FeedbackSendingForm extends Component {
  constructor(props) {
    super(props);


    this.state = {
      gmailModalShow : false,

      template: "",
    }
  }

  handleSendFeedback = (isAll, feedbackType) => {
    let templateId = this.state.template;

    if (templateId === "") {
      // toast.error("choose one template", {
      //   position: "top-center",
      //   autoClose: 5000,
      //   hideProgressBar: false,
      //   closeOnClick: true,
      //   pauseOnHover: true,
      //   draggable: true,
      //   progress: undefined,
      // });
      return;
    }

    request(`/api/courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/feedback/send/${templateId}?isAll=${isAll}&type=${feedbackType}`
      , "GET", undefined, undefined, false)
      .then(async (response) => {
        let data = await response.json();

        // if (data.hasOwnProperty("error")) {
        //   if (data.status === 401) {
        //     this.handleGmailModalShow()
        //     return
        //   }
        //
        //   // alert(data.message)
        //   toast.error(data.message, {
        //     position: "top-center",
        //     autoClose: 5000,
        //     hideProgressBar: false,
        //     closeOnClick: true,
        //     pauseOnHover: true,
        //     draggable: true,
        //     progress: undefined,
        //   });
        //   return
        // }

        // toast.success("sent emails", {
        //   position: "top-center",
        //   autoClose: 5000,
        //   hideProgressBar: false,
        //   closeOnClick: true,
        //   pauseOnHover: true,
        //   draggable: true,
        //   progress: undefined,
        // });

      }).catch(error => {
        // toast.error(error.message, {
        //   position: "top-center",
        //   autoClose: 5000,
        //   hideProgressBar: false,
        //   closeOnClick: true,
        //   pauseOnHover: true,
        //   draggable: true,
        //   progress: undefined,
        // });
        console.error(error.message)
      })
  }

  toggleGmailModal = () => {
    this.setState(prevState => ({
      gmailModalShow: !prevState.gmailModalShow,
    }))
  }

  render() {
    return (
      <div className={classnames(styles.sendFeedbackContainer)}>
        <Card
          className={styles.sendFeedbackCard}
          style={{backgroundColor: this.props.theme.palette.additionalColors.lightBlue}}>
          <CardContent>
            <div className={styles.submissionCardTitleWithButtons}>
              <h4>Send feedback</h4>
            </div>

            <div className={styles.cardBodyContent}>

              <FormControl
                variant="outlined"
                className={styles.cardBodyContentRow}
                fullWidth
              >
                <InputLabel>Template</InputLabel>
                <Select
                  value={this.state.template}
                  onChange={event => this.setState({
                    template: event.target.value
                  })}
                  label="Template"
                >
                  <MenuItem value="">
                    <em>None</em>
                  </MenuItem>
                  {this.props.templates.map(template => {
                    return(
                      <MenuItem
                        key={template.id}
                        value={template.id}
                      >
                        {template.name}
                      </MenuItem>
                    )
                  })}
                </Select>
              </FormControl>

              <div className={classnames(styles.cardBodyContentRow, styles.buttonGroup)}>
                <Badge
                  badgeContent={this.props.all.length}
                  color="secondary"
                  showZero
                >
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={() => this.handleSendFeedback(true, "canvasString")}
                    startIcon={<GroupIcon/>}
                    disableElevation
                    fullWidth
                  >
                  Send to all
                  </Button>
                </Badge>
              </div>

              <div className={classnames(styles.cardBodyContentRow, styles.buttonGroup)}>
                <Badge
                  badgeContent={this.props.notSent.length}
                  color="primary"
                  showZero
                >
                  <Button
                    variant="contained"
                    color="secondary"
                    style={{color: "white"}}
                    onClick={() => this.handleSendFeedback(false, "canvasString")}
                    startIcon={<PersonIcon/>}
                    disableElevation
                    fullWidth
                  >
                  Send to not yet sent
                  </Button>
                </Badge>
              </div>

            </div>
          </CardContent>
        </Card>
      </div>

    //   <Modal show={this.state.gmailModalShow} onHide={this.handleGmailModalClose}>
    //     <Modal.Header closeButton>
    //       <Modal.Title>Gmail authentication needed</Modal.Title>
    //     </Modal.Header>
    //     <Modal.Body>This step requires Gmail authentication, Do you want to continue?</Modal.Body>
    //     <Modal.Footer>
    //       <Button variant="secondary" onClick={this.handleGmailModalClose}>
    //         Close
    //       </Button>
    //       <Button href={"/api/gmail/auth"}>
    //         Continue
    //       </Button>
    //     </Modal.Footer>
    //   </Modal>
    // </>
    );
  }
}

export default withTheme(FeedbackSendingForm);