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
        <div className={[styles.sectionTitle, styles.sectionTitleWithButton].join(" ")}>
          <h3 className={styles.sectionTitleH}>{this.props.title}</h3>

          {this.props.icon &&
            <div className={styles.sectionTitleButton}>
              {this.props.icon}
            </div>
          }
        </div>

        {/*<div className={styles.sectionFlexContainer}>*/}
        {this.props.data.length > 0 ?
          (this.props.data.map((item, index) => {
            return (
            // <div key={index}>
              <this.props.Component key={index} data={item}/>
            // </div>
            )
          }))
          :
        // (<div>
        //   <p>{this.props.emptyText}</p>
          <EmptyCard data={this.props.emptyText} icon={this.props.EmptyIcon}/>
          // </div>)
        }
        {/*</div>*/}
      </div>
    )
  }
}

export default SectionContainer;