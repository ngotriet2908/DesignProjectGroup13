import React, { Component } from 'react'
import styles from './grading.module.css'
import FileViewer from "./FileViewer";
import Card from "react-bootstrap/Card";
import RubricViewer from "./RubricViewer";


class Grading extends Component {
  constructor (props) {
    super(props)
  }

  render () {
    return (
      <div className={styles.container}>
        <FileViewer/>
        <RubricViewer/>
      </div>
    )
  }
}

export default Grading
