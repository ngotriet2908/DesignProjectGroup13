import React, {Component} from "react"
import {Tooltip, Button, OverlayTrigger} from "react-bootstrap";

class ButtonTooltip extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <OverlayTrigger
        placement=auto
        overlay={
          <Tooltip>
            {this.props.tooltip.content}
          </Tooltip>
        }
      >
        <Button variant={this.props.button.variant} onClick={this.props.button.onClick}>{this.props.button.content}</Button>
      </OverlayTrigger>
    );
  }
}