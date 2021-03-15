import React, {Component} from "react";
import StudentFeedbackLine from "./StudentFeedbackLine";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";

class StudentList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      students: [],
    }
  }

    loadCourseStudentsData = () => {
      request(BASE + 'courses' + '/' + this.props.courseId + '/' + 'participants?role=students').then(response => {
        return response.json();
      }).then(data => {
        this.setState({
          students: data.map((student, i) => <StudentFeedbackLine key={i} student={student}/>)
        });
      });
    }

    componentDidMount() {
      this.loadCourseStudentsData();
    }

    render() {
      return (
        <React.Fragment>
          <div>Students List:</div>
          {this.state.students}
        </React.Fragment>
      )
    }
}

export default StudentList;
