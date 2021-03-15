import React, {Component} from "react";
import {connect} from "react-redux";
import {deleteRubric, saveRubric} from "../../redux/rubric/actions";

class RubricContainer extends Component {
  constructor(props) {
    super(props)
  }

  componentDidMount() {

  }

  render () {
    return(
      <div>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric
  };
};

const actionCreators = {
  saveRubric,
  deleteRubric,
}

export default connect(mapStateToProps, actionCreators)(RubricContainer)