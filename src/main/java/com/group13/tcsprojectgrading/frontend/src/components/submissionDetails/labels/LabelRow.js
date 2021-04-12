import React, {Component} from "react";
import styles from "../submissionDetails.module.css";
import globalStyles from "../../helpers/global.module.css";
import classnames from 'classnames';
import {Can} from "../../permissions/ProjectAbility";
import { subject } from '@casl/ability';

import Chip from "@material-ui/core/Chip";
import EditIcon from '@material-ui/icons/Edit';
import withTheme from "@material-ui/core/styles/withTheme";

export const colors = [
  "green",
  "yellow",
  "red",
  "blue",
]

// export const colorStyles = [
//   styles.labelModalCreateColorPrimary,
//   styles.labelModalCreateColorSecondary,
//   styles.labelModalCreateColorTernary,
//   styles.labelModalCreateColorQuaternary,
//   styles.labelModalCreateColorQuinary,
//   styles.labelModalCreateColorSenary,
//   styles.labelModalCreateColorDarkishGray,
// ]

// export const colorToStyles = {}
// colors.forEach((color, index) => colorToStyles[color] = colorStyles[index])

class LabelRow extends Component {
  render() {
    return (
      <div className={styles.labelsContainer}>

        {this.props.labels.map((label) => {
          return (
            <Chip
              key={label.id}
              className={classnames(globalStyles.label)}
              label={label.name}
              style={{
                backgroundColor: this.props.theme.palette.labels[label.color]
              }}
            />
          )
        })}

        <Can I="edit" this={subject('Submission', (this.props.submission.grader === null)? {id: -1}:this.props.submission.grader)}>
          <Chip
            icon={<EditIcon fontSize="small"/>}
            label="Edit labels"
            clickable
            onClick={
              this.props.toggleShow
            }
            className={styles.dashedClip}
          />

        </Can>
      </div>
    );
  }
}

export default withTheme(LabelRow)