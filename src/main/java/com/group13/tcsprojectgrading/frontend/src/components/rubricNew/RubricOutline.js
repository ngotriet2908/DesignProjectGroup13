import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import RubricOutlineGroup from "./RubricOutlineGroup";
import {FaMinus, FaPlus} from "react-icons/fa";
import {setSelectedElement} from "../../redux/rubricNew/actions";


class RubricOutline extends Component {
  constructor (props) {
    super(props);

    this.padding = 0;
  }

  onClickElement = () => {
    this.setState({
      selected: true,
    })

    this.props.setSelectedElement(this.props.rubric.id);
  }

  render () {
    let classNames = `${styles.outlineElementContainer}`;
    if (this.props.selectedElement != null) {
      classNames += " " + (this.props.rubric.id === this.props.selectedElement &&
        `${styles.outlineElementContainerSelected}`);
    }

    return (
      <div className={styles.outlineContainer}>
        <div className={styles.outlineHeader} onClick={this.onClickElement}>
          <div className={classNames}>
            <h4>Rubric</h4>
            <div>{this.props.isEditing && (<span>Editing</span>)}</div>
          </div>
        </div>

        {this.props.rubric != null ?
          <div>
            <RubricOutlineGroup padding={this.padding} data={this.props.rubric.children}/>
          </div>
          :
          <div>
            Empty
          </div>
        }
      </div>
    )
  }
}

// class RubricOutlineHeader extends Component {
//   constructor (props) {
//     super(props);
//
//     // this.state = {
//     //   showMenu: true,
//     // }
//   }
//
//   // addElementMenuSetState = (showMenu) => {
//   //   this.setState({
//   //     showMenu: showMenu
//   //   })
//   // }
//
//   onClickElement = () => {
//     console.log(this.props.data);
//
//     this.setState({
//       selected: true,
//     })
//
//     this.props.setSelectedElement(this.props.data.id);
//   }
//
//   render () {
//     return(
//       <div className={styles.outlineHeader} onClick={this.onClickElement}>
//         <div className={styles.outlineElementContainer}>
//           <h4>Outline</h4>
//         </div>
//       </div>
//     )
//   }
// }

const mapStateToProps = state => {
  return {
    rubric: state.rubricNew.rubric,
    selectedElement: state.rubricNew.selectedElement,
    isEditing: state.rubricNew.isEditing
  };
};

const actionCreators = {
  setSelectedElement
}

export default connect(mapStateToProps, actionCreators)(RubricOutline)