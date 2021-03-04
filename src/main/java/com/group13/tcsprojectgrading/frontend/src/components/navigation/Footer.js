import React from "react";
import styles from "./navigation.module.css";

class Footer extends React.Component {
  render() {
    return (
      <div id={styles.footer}>
        <div>
          Created with love ❤️
        </div>
        <div id={styles.footerLogo}>
          <a href="https://www.utwente.nl/"/>
        </div>
      </div>
    );
  }
}

export default Footer;