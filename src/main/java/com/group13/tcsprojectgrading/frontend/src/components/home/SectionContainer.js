import React, {Component} from "react";
import styles from "../helpers/global.module.css";
import EmptyCard from "./EmptyCard";
import classnames from "classnames";

class SectionContainer extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return(
      <div className={classnames(styles.sectionContainer, this.props.className)}>
        <div className={classnames(styles.sectionTitle, styles.sectionTitleWithButton)}>
          <h3 className={styles.sectionTitleH}>{this.props.title}</h3>

          {this.props.icon &&
            <div className={classnames(styles.iconButton, styles.sectionTitleButton)}>
              {this.props.icon}
            </div>
          }
        </div>

        {this.props.data.length > 0 ?
          (this.props.data.map((item, index) => {
            return (
              <this.props.Component key={index} data={item}/>
            )
          }))
          :
          <EmptyCard data={this.props.emptyText} icon={this.props.EmptyIcon}/>
        }
      </div>
    )
  }
}

export default SectionContainer;