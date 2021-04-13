import React, {Component} from "react";
import classnames from "classnames";
import {request} from "../../services/request";
import {BASE, PROJECT, USER_COURSES} from "../../services/endpoints";
import globalStyles from '../helpers/global.module.css';
import {connect} from "react-redux";
import CustomModal from "../helpers/CustomModal";
import ListItem from "@material-ui/core/ListItem";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Avatar from "@material-ui/core/Avatar";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import {IconButton} from "@material-ui/core";
import CheckCircleIcon from "@material-ui/icons/CheckCircle";
import RadioButtonUncheckedIcon from "@material-ui/icons/RadioButtonUnchecked";
import List from "@material-ui/core/List";
import withTheme from "@material-ui/core/styles/withTheme";


class GradersModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      graders: [],
      tas: [],

      isLoaded: false,
      selected: [],

      initial: [],

      add: [],
      remove: []
    }
  }

  // todo, quick fix, needs to be changed
  fetchGraders = () => {
    Promise.all([
      request(`/api/courses/${this.props.routeParams.courseId}/graders?ta=true`),
      request(`/api/courses/${this.props.routeParams.courseId}/graders`)
    ])
      .then(async([res1, res2]) => {
        const tas = await res1.json();
        const graders = await res2.json();

        let selected = graders.filter(grader => {
          return this.props.currentGraders.find(currentGrader => {return currentGrader.id === grader.id})
        })

        // load submission
        this.setState({
          graders: graders,
          tas: tas,

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

  body = () => {
    return (
      <>
        {this.state.graders.length === 0 &&
        <div className={classnames(globalStyles.modalBodyContainerRow, globalStyles.modalBodyContainerRowEmpty)}>
          No TAs participating in this project
        </div>
        }

        <List>
          {
            this.state.graders.filter(grader => {
              return this.state.tas.find(ta => {
                return ta.id === grader.id
              })
            }).map(grader => {
              let isSelected = this.state.selected.find(selectedGrader => {
                return selectedGrader.id === grader.id;
              })

              let isUser = this.props.user.id === grader.id;

              return(
                <ListItem key={grader.id} disabled={isUser}>
                  <ListItemAvatar>
                    <Avatar
                      alt={grader.name}
                      src={grader.avatar.includes("avatar-50") ? "" : grader.avatar}
                    />
                  </ListItemAvatar>

                  <ListItemText
                    primary={
                      <span>{grader.name} {isUser && " (you)"}</span>
                    }
                  />

                  <ListItemSecondaryAction>
                    <IconButton edge="end" aria-label="delete" onClick={() => this.handleGraderClick(grader, isSelected, isUser)}>
                      {isSelected ?
                        <CheckCircleIcon style={{color: this.props.theme.palette.success.main}}/>
                        :
                        <RadioButtonUncheckedIcon/>
                      }
                    </IconButton>
                  </ListItemSecondaryAction>
                </ListItem>
              )
            })}
        </List>
      </>
    )
  }

  render() {
    return(

      <CustomModal
        show={this.props.show}
        onClose={this.props.toggleShow}
        onShow={this.fetchGraders}
        onAccept={this.onAccept}
        title={"Graders"}
        description={"Select people responsible for grading. All assigned submissions of the removed graders will be moved to the 'unassigned' list."}
        body={this.body()}
        isLoaded={this.state.isLoaded}
      />
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

export default connect(mapStateToProps, actionCreators)(withTheme(GradersModal))