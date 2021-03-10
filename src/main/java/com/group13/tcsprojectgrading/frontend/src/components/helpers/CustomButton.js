import React, {Component} from "react";
import styles from "./helpers.module.css";

class CustomButton extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    let style = `${styles.buttonContainer}`;

    if (this.props.variant === "lightGreen") {
      style += ` ${styles.lightGreen}`;
    } else if (this.props.variant === "darkGreen") {
      style += ` ${styles.lightGreen}`;
    } else {
      style += ` ${styles.lightGreen}`;
    }

    if (this.props.size === "lg") {
      style += ` ${styles.large}`;
    }

    if (this.props.className) {
      style += ` ${this.props.className}`;
    }

    return(
      this.props.href ?
        <a className={style} href={this.props.href}>{this.props.children}</a>
        :
        (
          this.props.onClick ?
            <button className={style} onClick={this.props.onClick}>
              {this.props.children}
            </button>
            :
            <button className={style}>
              {this.props.children}
            </button>
        )
      
    )
  }
}

export default CustomButton;