import React, {Component} from "react";
import styles from "../submissionDetails.module.css";
import {ListGroupItem} from "react-bootstrap";
import OverlayTrigger from "react-bootstrap/OverlayTrigger";
import Badge from "react-bootstrap/Badge";
import Tooltip from "react-bootstrap/Tooltip";
import Button from "react-bootstrap/Button";
import {IoAddOutline, IoPencilOutline} from "react-icons/io5";
import globalStyles from "../../helpers/global.module.css";
import classnames from 'classnames';
import {Can} from "../../permissions/ProjectAbility";
import { subject } from '@casl/ability';

export const colors = [
  "primary",
  "secondary",
  "ternary",
  "quaternary",
  "quinary",
  "senary",
  "darkishGray"
]

export const colorStyles = [
  styles.labelModalCreateColorPrimary,
  styles.labelModalCreateColorSecondary,
  styles.labelModalCreateColorTernary,
  styles.labelModalCreateColorQuaternary,
  styles.labelModalCreateColorQuinary,
  styles.labelModalCreateColorSenary,
  styles.labelModalCreateColorDarkishGray,
]

export const colorToStyles = {}
colors.forEach((color, index) => colorToStyles[color] = colorStyles[index])

class LabelRow extends Component {
  render() {
    // console.log(this.props.labels);

    return (
      <div className={styles.labelsContainer}>
        {this.props.labels.map((label) => {
          return (
            <Badge key={label.id} className={classnames(globalStyles.label, colorToStyles[label.color])} variant={label.color}>{label.name}</Badge>

          // <OverlayTrigger
          //   key={flag.id}
          //   placement="bottom"
          //   delay={{ show: 250, hide: 400 }}
          //   overlay={(props) => (
          //     <Tooltip id={flag.id} {...props}>
          //       {flag.description}
          //     </Tooltip>)
          //   }>
          //   <Badge className={globalStyles.label} variant={flag.variant}>{flag.name}</Badge>
          // </OverlayTrigger>
          )
        })}
        <Can I="edit" this={subject('Submission', (this.props.submission.grader === null)? {id: -1}:this.props.submission.grader)}>
        <Button variant="dashed" onClick={this.props.toggleShow}>
            <IoPencilOutline size={20}/> Edit labels
          </Button>
        </Can>
      </div>
    );
  }
}

export default LabelRow