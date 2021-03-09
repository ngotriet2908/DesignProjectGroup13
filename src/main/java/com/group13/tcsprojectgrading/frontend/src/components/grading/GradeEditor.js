import React, { Component } from 'react'
import styles from './grading.module.css'
import Card from "react-bootstrap/Card";


class GradeEditor extends Component {
  constructor (props) {
    super(props)

    this.state = {
      comment: "",
      grade: 0,
    }
  }

  onChangeComment = (event) => {
    this.setState({
      comment: event.target.value
    })
  }

  onChangeGrade = (event) => {
    this.setState({
      grade: event.target.value
    })
  }


  render () {
    return (
      <div className={styles.gradeEditorContainer}>
        <Card className={styles.gradeEditorCard}>
          <Card.Body>
            <div>
              Grade:
              <input type="number" value={this.state.grade} onChange={this.onChangeGrade}/>
            </div>
            <div>
              Comment:
              <input type="text" value={this.state.comment} onChange={this.onChangeComment}/>
            </div>
          </Card.Body>
        </Card>
      </div>
    )
  }
}

export default GradeEditor
