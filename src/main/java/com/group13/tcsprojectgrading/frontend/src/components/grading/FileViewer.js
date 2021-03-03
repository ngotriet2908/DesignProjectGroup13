import React, { Component } from 'react'
import styles from './grading.module.css'
import { Document, Page } from 'react-pdf/dist/esm/entry.webpack';
import {request} from "../../services/request";
import Card from "react-bootstrap/Card";

const options = {
  cMapUrl: 'cmaps/',
  cMapPacked: true,
};

class FileViewer extends Component {
  constructor (props) {
    super(props);

    this.state = {
      numPages: null,
      pageNumber: 1
    }
  }

  componentDidMount() {
    // request("/api/courses/120/projects/158/submissions/sample")
    //   .then(response => {
    //     console.log(response);
    //   })
    //   .catch(error => {
    //     console.error(error.message)
    //   })
  }

  onDocumentLoadSuccess = ({ numPages }) => {
    this.setState({
      numPages: numPages
    });
  }

  render () {
    return (
      <Card>
        <Card.Body>
          <div>
            <Document
              file="/api/courses/120/projects/158/submissions/sample"
              onLoadSuccess={this.onDocumentLoadSuccess}
              options={options}
            >
              {
                Array.from(
                  new Array(this.state.numPages),
                  (el, index) => (
                    <Page
                      key={`page_${index + 1}`}
                      pageNumber={index + 1}
                    />
                  ),
                )
              }
            </Document>
            <p>Page {this.state.pageNumber} of {this.state.numPages}</p>
          </div>
        </Card.Body>
      </Card>
    )
  }
}

export default FileViewer
