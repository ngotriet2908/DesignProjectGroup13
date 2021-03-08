import React, {Component} from "react";
import {FaEnvelope, FaFilePdf, FaSignOutAlt} from "react-icons/fa";
import Button from "react-bootstrap/Button";
import styles from "./feedback.module.css";

class StudentFeedbackLine extends Component {
    generatePdfAction = () => {
        alert(1);
    }

    sendEmailAction = () => {
        alert(2);
    }

    render() {
        return (
            <div>
                <span className={styles.studentName}>{this.props.student.name}</span>
                <Button
                    className={styles.studentButton}
                    variant="primary"
                    onClick={this.generatePdfAction}>
                    <FaFilePdf size={20}/>
                </Button>
                <Button
                    className={styles.studentButton}
                    variant="primary"
                    onClick={this.sendEmailAction}>
                    <FaEnvelope size={20}/>
                </Button>
            </div>
        )
    }
}

export default StudentFeedbackLine;
