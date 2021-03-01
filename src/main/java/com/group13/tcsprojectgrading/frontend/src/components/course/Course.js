// import React, { Component } from 'react'
// import CourseCard from './CourseCard'
//
// import styles from './home.module.css'
// import {request} from "../../services/request";
// import {BASE, USER_COURSES, USER_INFO} from "../../services/endpoints";
// import {Link} from "react-router-dom";
// import Button from "react-bootstrap/Button";
// import {URL_PREFIX} from "../../services/config";
// import {v4 as uuidv4} from "uuid";
// // import ProjectCard from '../projects/ProjectCard'
//
// class Course extends Component {
//   constructor (props) {
//     super(props)
//     this.state = {
//       courses: [],
//       user: {},
//       loaded: false,
//       rubric: null,
//     }
//   }
//
//   componentDidMount () {
//     request(BASE + USER_COURSES)
//       .then(response => {
//         return response.json();
//       })
//       .then(data => {
//         console.log(data);
//         this.setState({
//           courses: data
//         })
//       })
//       .catch(error => {
//         console.error(error.message);
//       });
//
//     request(BASE + USER_INFO)
//       .then(response => {
//         return response.json();
//       })
//       .then(data => {
//         console.log(data);
//         this.setState({
//           user: data
//         })
//       })
//       .catch(error => {
//         console.error(error.message)
//       });
//   }
//
//   render () {
//     return (
//       <div className={styles.container}>
//         <h1>Welcome, {this.state.user.name}!</h1>
//         <div className={styles.coursesContainer}>
//           <h2>Your courses</h2>
//           <ul className={styles.ul}>
//             {this.state.courses.map(course => {
//               return (
//                 <li className={styles.li} key={course.uuid}>
//                   <CourseCard data={course}/>
//                 </li>
//               )
//             })}
//           </ul>
//           {this.state.rubric != null ?
//             <div>
//               <h2>Rubric</h2>
//               <Button variant="primary"><Link className={styles.plainLink} to={URL_PREFIX + '/rubric/'}>Open
//                 rubric</Link></Button>
//             </div>
//             :
//             <div>
//               <h2>Rubric</h2>
//               <div>No rubric</div>
//               <Button variant="primary">Create rubric</Button>
//             </div>
//           }
//         </div>
//       </div>
//     )
//   }
// }
//
// export default Course
