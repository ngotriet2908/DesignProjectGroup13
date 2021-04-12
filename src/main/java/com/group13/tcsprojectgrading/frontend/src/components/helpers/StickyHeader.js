import React, {Component} from "react";
import globalStyles from "./global.module.css";
import classnames from 'classnames';

class StickyHeader extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isSticky: false
    }
  }

  handleScroll = () => {
    this.setState({
      isSticky: window.pageYOffset > 64
    })
  }

  componentDidMount() {
    window.addEventListener("scroll", this.handleScroll);
  }

  componentWillUnmount() {
    window.removeEventListener('scroll', this.handleScroll);
  }

  render() {
    return(
      <>
        <div className={classnames(globalStyles.stickyHeaderContainer, this.state.isSticky && globalStyles.stickyHeaderContainerFixed)}>
          <h1>{this.props.title}</h1>

          {this.props.buttons}
        </div>
      </>
    )
  }
}

export default StickyHeader;