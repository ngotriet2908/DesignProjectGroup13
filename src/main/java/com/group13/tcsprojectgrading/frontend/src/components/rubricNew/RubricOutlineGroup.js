import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import RubricOutlineElement from "./RubricOutlineElement";

class RubricOutlineGroup extends Component {
  constructor (props) {
    super(props);

    this.padding = this.props.padding + 1.4;
  }

  render () {
    return (
      <ul>
        {this.props.data.map((m, index) => {
          return (
            <RubricOutlineInnerGroup
              key={index}
              data={m}
              padding={this.padding}
            />
          );
        })}
      </ul>
    )
  }
}


class RubricOutlineInnerGroup extends Component {
  constructor (props) {
    super(props);

    this.state = {
      collapsed: !this.props.data.children,
    }
  }

  onClickBlockCollapse = (event) => {
    this.setState(prevState => ({
      collapsed: !prevState.collapsed
    }));

    event.stopPropagation();
  }

  render() {
    let className = `${styles.outlineGroup}`;
    if (this.state.collapsed) {
      className += " " + `${styles.outlineGroupCollapsed}`
    }

    return (
      <div>
        <RubricOutlineElement
          onClickBlockCollapse={this.onClickBlockCollapse}
          data={this.props.data.content}
          padding={this.props.padding}
          collapsed={this.state.collapsed}
        />

        <div className={className}>
          {this.props.data.children && <RubricOutlineGroup padding={this.props.padding} data={this.props.data.children}/>}
        </div>
      </div>
    );
  }
}

const mapStateToProps = state => {
  return {

  };
};

const actionCreators = {

}

export default connect(mapStateToProps, actionCreators)(RubricOutlineGroup)


















