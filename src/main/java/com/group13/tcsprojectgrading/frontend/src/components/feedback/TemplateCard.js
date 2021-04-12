import React, {Component} from "react";
import withTheme from "@material-ui/core/styles/withTheme";
import classnames from "classnames";
import globalStyles from "../helpers/global.module.css";
import styles from "./feedback.module.css";
import CardContent from "@material-ui/core/CardContent";
import IconButton from "@material-ui/core/IconButton";
import EditIcon from "@material-ui/icons/Edit";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import Card from "@material-ui/core/Card";
import KeyboardArrowDownIcon from '@material-ui/icons/KeyboardArrowDown';


class TemplateCard extends Component {
  constructor(props) {
    super(props);

    this.state = {
      collapsed: true,
    }
  }

  toggleCollapsed = () => {
    this.setState(prevState => ({
      collapsed: !prevState.collapsed,
    }))
  }

  render() {
    return (
      <Card key={this.props.template.id} className={classnames(globalStyles.cardShadow, styles.templateCard)}>
        <CardContent>
          <div className={styles.submissionCardTitleWithButtons}>
            <h4>{this.props.template.name}</h4>

            <div>
              <IconButton
                aria-label="edit"
                // onClick={this.toggleShowCreateTemplateModal}
              >
                <EditIcon/>
              </IconButton>

              <IconButton
                aria-label="edit"
                // onClick={this.toggleShowCreateTemplateModal}
              >
                <DeleteOutlineIcon style={{color: this.props.theme.palette.error.main}}/>
              </IconButton>

              <IconButton
                aria-label="edit"
                onClick={this.toggleCollapsed}
              >
                <KeyboardArrowDownIcon className={classnames(styles.collapseIcon, !this.state.collapsed && styles.collapseIconRotated)}/>
              </IconButton>

            </div>
          </div>

          {!this.state.collapsed &&
          <div className={styles.cardBodyContent}>
            <div className={styles.cardBodyContentRow}>
              <h5>Subject</h5>
              <p>{this.props.template.subject}</p>
            </div>

            <h5>Body</h5>
            <p>{this.props.template.body}</p>

          </div>
          }
        </CardContent>
      </Card>
    );
  }
}

export default withTheme(TemplateCard);