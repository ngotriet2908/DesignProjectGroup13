import React, { Component } from 'react'
import styles from './grading.module.css'
import {connect} from "react-redux";


class GradingPanel extends Component {
  constructor (props) {
    super(props);
  }


  render () {
    return (
      <div>
        Hoi
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

export default connect(mapStateToProps, actionCreators)(GradingPanel)
