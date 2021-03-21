import React, { Component } from 'react'

import styles from '../grading.module.css'
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
              path={this.props.path + "/" + index}
              onClickElement={this.props.onClickElement}
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
      // collapsed: !this.props.data.children,
      collapsed: false,
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
      <>
        <RubricOutlineElement
          onClickBlockCollapse={this.onClickBlockCollapse}
          collapsed={this.state.collapsed}
          data={this.props.data}
          padding={this.props.padding}
          onClickElement={this.props.onClickElement}
          path={this.props.path}
        />

        <div className={className}>
          {this.props.data.children && <RubricOutlineGroup path={this.props.path + "/children"} onClickElement={this.props.onClickElement} padding={this.props.padding} data={this.props.data.children}/>}
        </div>
      </>
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


















