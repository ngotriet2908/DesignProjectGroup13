import React, {Component} from "react"
import {Tooltip, Button, OverlayTrigger} from "react-bootstrap";

class ButtonTooltip extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <OverlayTrigger
        placement={this.props.placement ?? "auto"}
        delay={{ show: 500, hide: 0}}
        overlay={
          <Tooltip>
            {this.props.content}
          </Tooltip>
        }
      >
        <div key = {this.props.key} className={this.props.className} onClick={this.props.onClick}>
          {this.props.children}
        </div>
      </OverlayTrigger>
    );
  }
}

export default ButtonTooltip;