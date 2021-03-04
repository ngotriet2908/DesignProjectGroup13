// import {Button, Modal, Form, Card, ListGroup, ListGroupItem, Alert} from 'react-bootstrap'
// import React, {Component} from "react";
//
//
// class EditProjectsModal extends Component {
//   constructor(props) {
//     super(props)
//   }
//
//   render() {
//     return(
//       <Modal centered
//              backdrop="static"
//              size="lg"
//              show={this.props.show}
//              onHide={this.props.onClose}>
//         <Modal.Header closeButton>
//           <Modal.Title>Group Details</Modal.Title>
//         </Modal.Header>
//         <Modal.Body>
//           {
//             <div>
//               {(!this.props.showAlert)? null:
//                 <Alert variant="danger" onClose={this.props.closeAlertHandle} dismissible>
//                   <p>
//                     {this.props.alertBody}
//                   </p>
//                 </Alert>}
//               <h4>Active Projects</h4>
//               <Card className={styles.editProjectsModalCard}>
//                 <ListGroup className={styles.notAssignedGroupList}>
//                   {this.props.activeGraders
//                     // .filter((grader) => {
//                     //   return grader.name.toLowerCase().includes(this.state.groupsFilterString.toLowerCase())
//                     // })
//                     .map(grader => {
//                       return (
//                         <ListGroupItem
//                           key={grader.id}
//                           className={styles.listGroupItemCustom}
//                           action onClick={() => this.props.onClickDeactive(grader)} >
//                           name: {grader.name}
//                         </ListGroupItem>
//                       )
//                     })}
//                 </ListGroup>
//               </Card>
//
//               <h4>Available Projects</h4>
//               <Card className={styles.editGradersModalCard}>
//                 <ListGroup className={styles.notAssignedGroupList}>
//                   {this.props.availableGraders
//                     // .filter((grader) => {
//                     //   return grader.name.toLowerCase().includes(this.state.groupsFilterString.toLowerCase())
//                     // })
//                     .map(grader => {
//                       return (
//                         <ListGroupItem
//                           key={grader.id}
//                           className={styles.listGroupItemCustom}
//                           action onClick={() => this.props.onClickActive(grader)} >
//                           name = {grader.name}
//                         </ListGroupItem>
//                       )
//                     })}
//                 </ListGroup>
//               </Card>
//             </div>
//           }
//         </Modal.Body>
//         <Modal.Footer>
//           <Button variant="secondary"
//                   onClick={() => {
//                     this.props.onClose()
//                   }}>
//             Cancel
//           </Button>
//
//           <Button variant="primary"
//                   onClick={() =>
//                   {
//                     this.props.onAccept()
//                   }}>
//             Apply changes
//           </Button>
//         </Modal.Footer>
//       </Modal>
//     )
//   }
// }
//
// export default EditProjectsModal;