import React, { Component } from 'react'
import styles from './grading.module.css'
import {connect} from "react-redux";


class IssuesPanel extends Component {
  constructor (props) {
    super(props);
  }


  render () {
    return (
      <div>
        Hai
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {

  };
};

const actionCreators = {

}

export default connect(mapStateToProps, actionCreators)(IssuesPanel)
