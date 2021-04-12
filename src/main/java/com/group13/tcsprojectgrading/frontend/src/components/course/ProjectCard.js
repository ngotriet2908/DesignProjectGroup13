import React, { Component } from 'react'

import styles from '../home/home.module.css'
import courseStyles from './course.module.css'
import store from "../../redux/store";
import {push} from "connected-react-router";
import globalStyles from '../helpers/global.module.css';
import classnames from 'classnames';
import CardContent from "@material-ui/core/CardContent";
import AccessTimeIcon from "@material-ui/icons/AccessTime";
import Card from "@material-ui/core/Card";
import Link from "@material-ui/core/Link";


class ProjectCard extends Component {
  constructor (props) {
    super(props)
  }

  onClickSeeMore = () => {
    store.dispatch(push("/app/courses/" + this.props.data.course.id + "/projects/" + this.props.data.id));
  }

  render () {
    return (
      <Card className={classnames(courseStyles.projectCard, globalStyles.cardShadow)}>
        <CardContent>
          <div className={styles.cardBodyTitle}>

            <Link href="#" color="primary" onClick={(event) => {
              event.preventDefault();
              this.onClickSeeMore();
            }}>
              <h4>{this.props.data.name}</h4>
            </Link>

          </div>
          <div className={styles.cardBodyContent}>
            <div className={classnames(globalStyles.flexRow, globalStyles.flexRowWithIcon)}>
              <AccessTimeIcon/> <span>Created on {(new Date(this.props.data.createdAt)).toDateString()}</span>
            </div>
          </div>
        </CardContent>
      </Card>
    )
  }
}

export default ProjectCard;

// class ProjectCard extends Component {
//   constructor (props) {
//     super(props)
//   }
//
//   onClickSeeMore = () => {
//     store.dispatch(push("/app/courses/" + this.props.data.course.id + "/projects/" + this.props.data.id));
//   }
//
//   render () {
//     return (
//       <Card className={classnames(styles.card)}>
//         <Card.Body className={styles.cardBodyContainer}>
//           <div className={styles.cardBodyTitle}>
//             <h5>{this.props.data.name}</h5>
//             <div className={classnames(globalStyles.iconButton, styles.cardButtonContainer)} onClick={this.onClickSeeMore}>
//               <IoArrowForward size={26}/>
//             </div>
//           </div>
//           <div className={styles.cardBodyContent}>
//             <div>Created on {(new Date(this.props.data.createdAt)).toDateString()}</div>
//             {/*<div className={styles.cardButtonContainer}>*/}
//             {/*<div onClick={this.onClickSeeMore}>*/}
//             {/*  <IoArrowForward size={26}/>*/}
//             {/*</div>*/}
//             {/*</div>*/}
//           </div>
//         </Card.Body>
//       </Card>
//     )
//   }
// }
//
// ProjectCard.propTypes = {
//   data: PropTypes.object
// }
//
// export default ProjectCard
