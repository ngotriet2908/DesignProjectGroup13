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
        <div className={classnames(styles.sectionTitle, styles.sectionTitleWithButton, this.props.spreadButton && styles.sectionTitleWithButtonSpread)}>
          <h3 className={styles.sectionTitleH}>{this.props.title}</h3>

          {this.props.button &&
            <div className={classnames(styles.sectionTitleButton)}>
              {this.props.button}
            </div>
          }

          {this.props.icon &&
            <div onClick={this.props.onClickIcon} className={classnames(styles.iconButton, styles.sectionTitleButton)}>
              {this.props.icon}
            </div>
          }
        </div>

        {this.props.data.length > 0 ?
          (this.props.data.map((item, index) => {
            console.log(item);

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