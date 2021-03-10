import React, {Component} from "react";
import styles from "./helpers.module.css";

class CustomCard extends Component {
  static Body = CustomCardBody;

  constructor(props) {
    super(props);
  }

  render() {
    let style = `${styles.cardContainer}`;

    if (this.props.className) {
      style += ` ${this.props.className}`;
    }

    console.log(this.props.children);

    return(
      <div className={style}>
        {this.props.children}
      </div>
    )
  }
}

export class CustomCardBody extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    return(
      <div className={styles.cardBody}>
        {this.props.children}
      </div>
    )
  }
}

export default CustomCard;