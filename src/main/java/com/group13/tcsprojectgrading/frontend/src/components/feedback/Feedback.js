import React, {Component} from "react";
import StudentList from "./StudentList";

class Feedback extends Component {
    constructor(props) {
        super(props);
    }

    render() {
    return (
      <div>
        Place to generate and send feedbac
        <StudentList courseId={this.props.match.params.courseId}/>
      </div>
    )
  }
}

export default Feedback;
