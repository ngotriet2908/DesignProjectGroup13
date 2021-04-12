import React, {Component, createRef} from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";

import {alterCriterionText} from "../../redux/rubric/actions";
import {findById} from "../../redux/rubric/functions";
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import TextField from "@material-ui/core/TextField";


// import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";

// const DraftField = React.forwardRef(function DraftField(props, ref) {
//   const { component: Component, editorRef, handleOnChange, ...rest } = props;
//
//   // console.log(stateToHTML(props.editorState.getCurrentContent()));
//   // console.log(editorRef?.current);
//
//   console.log(ref);
//   console.log(editorRef);
//
//   React.useImperativeHandle(ref, () => ({
//     focus: () => {
//       console.log("Focys");
//       editorRef?.current?.focus();
//     },
//     // value: String(stateToHTML(props.editorState.getCurrentContent()))
//   }));
//
//   return(
//     // <div>
//     <Component {...rest} ref={editorRef} onChange={handleOnChange} />
//     // {/*<toolbarPlugin.Toolbar/>*/}
//     // {/*</div>*/}
//   );
// });

// function DraftField(props) {
//   const { Component, handleOnChange, editorRef, ...other } = props;
//
//   console.log(props.editorRef);
//
//   React.useImperativeHandle(editorRef, () => ({
//     focus: () => {
//       console.log("here");
//       editorRef?.current?.focus();
//     },
//   }));
//
//   // `Component` will be your `SomeThirdPartyComponent` from below
//   return <Component {...other} ref={editorRef} onChange={handleOnChange}/>;
// }

// class RubricEditorRichText extends Component {
//   constructor (props) {
//     super(props);
//
//     this.state = {
//       editorState: EditorState.createWithContent(stateFromHTML(this.props.element.content.text)),
//       // hasFocus: false,
//     }
//
//     // this.editor = null;
//   }
//
//   onTextEditorChange = (newEditorState) => {
//     // const currentContentState = this.state.editorState.getCurrentContent()
//     // const newContentState = newEditorState.getCurrentContent()
//
//     // if (currentContentState !== newContentState) {
//     // change in content
//     this.setState({
//       editorState: newEditorState
//     })
//
//     // this.props.alterCriterionText(this.props.element.content.id, stateToHTML(newEditorState.getCurrentContent()), this.props.currentPath + "/content/text");
//     // } else {
//     //  change in focus or selection
//     // }
//   }
//
//   setEditor = (editor) => {
//     console.log("set")
//     this.editor = editor;
//   };
//
//   focusEditor = () => {
//     if (this.editor) {
//       this.editor.focus();
//     }
//   };
//
//   render () {
//     // const { Toolbar } = this.pluginComponents;
//
//     return (
//       <div
//         // className={
//         // this.state.hasFocus ? `${styles.richTextEditor} ${styles.editorFocused}`
//         //   : `${styles.richTextEditor}`}
//         onClick={this.focusEditor}
//       >
//
//         {/*<TextField*/}
//         {/*  id="outlined-basic"*/}
//         {/*  label="Description"*/}
//         {/*  variant="outlined"*/}
//         {/*  // value={""}*/}
//         {/*  multiline={true}*/}
//         {/*  rows={4}*/}
//         {/*  fullWidth={true}*/}
//         {/*  // className={styles.viewerSectionContainerField}*/}
//         {/*  InputProps={{*/}
//         {/*    inputProps: {*/}
//         {/*      Component: Editor,*/}
//         {/*      // editorRef: this.editor,*/}
//         {/*      editorRef: this.editorRef,*/}
//         {/*      editorState: this.state.editorState,*/}
//         {/*      handleOnChange: this.onTextEditorChange,*/}
//         {/*      // plugins: this.plugins,*/}
//         {/*      // onFocus: () => this.setState({ hasFocus: true }),*/}
//         {/*      // onBlur: () => this.setState({ hasFocus: false }),*/}
//         {/*      // editorRef: this.setEditor*/}
//         {/*    },*/}
//         {/*    inputComponent: DraftField,*/}
//         {/*  }}*/}
//         {/*/>*/}
//
//
//         {/*<Editor*/}
//         {/*  ref={this.setEditor}*/}
//         {/*  editorState={this.state.editorState}*/}
//         {/*  onChange={this.onTextEditorChange}*/}
//         {/*  plugins={this.plugins}*/}
//         {/*  onFocus={() => this.setState({ hasFocus: true })}*/}
//         {/*  onBlur={() => this.setState({ hasFocus: false })}*/}
//         {/*/>*/}
//         {/*<Toolbar/>*/}
//
//         <Editor
//           ref={this.editor}
//           editorState={this.state.editorState}
//           onEditorStateChange={this.onTextEditorChange}
//           // onFocus={() => this.setState({ hasFocus: true })}
//           // onBlur={() => this.setState({ hasFocus: false })}
//         />
//       </div>
//     )
//   }
// }















//
// class RubricEditorRichText extends Component {
//   constructor (props) {
//     super(props);
//
//     this.state = {
//       value: this.props.element.content.text
//     }
//   }
//
//   setText = (text) => {
//     this.setState({
//       value: text
//     })
//
//     this.props.alterCriterionText(this.props.element.content.id, text, this.props.currentPath + "/content/text");
//   }
//
//   render() {
//     return (
//       <>
//         <ReactQuill
//           theme="snow"
//           value={this.state.value}
//           onChange={this.setText}
//         />
//         {/*<div dangerouslySetInnerHTML={{__html: this.state.value}}/>*/}
//       </>
//     )
//   }
// }
//
// const mapStateToProps = state => {
//   let element = findById(state.rubric.rubric, state.rubric.selectedElement);
//
//   return {
//     element: element,
//     currentPath: state.rubric.currentPath
//   };
// };
//
// const actionCreators = {
//   alterCriterionText,
// }
//
// export default connect(mapStateToProps, actionCreators)(RubricEditorRichText)















function RichField(props) {
  const { Component, handleOnChange, editorRef, value, ...other } = props;

  React.useImperativeHandle(editorRef, () => ({
    focus: () => {
      editorRef?.current?.focus();
    },
  }));

  return <Component {...other} value={value} ref={editorRef} onChange={handleOnChange}/>;
}


class RubricEditorRichText extends Component {
  constructor (props) {
    super(props);

    this.state = {
      value: this.props.element.content.text
    }

    this.editorRef = createRef();
  }

  setText = (text) => {
    this.setState({
      value: text
    })

    this.props.alterCriterionText(this.props.element.content.id, text, this.props.currentPath + "/content/text");
  }

  render() {
    return (
      <TextField
        id="outlined-basic"
        // label="Description"
        variant="outlined"
        multiline={true}
        rows={4}
        value={""}
        fullWidth={true}
        InputProps={{
          inputProps: {
            Component: ReactQuill,
            editorRef: this.editorRef,
            value: this.state.value,
            handleOnChange: this.setText,
            theme: "snow"
          },
          inputComponent: RichField,
        }}
      />

    // <>
    //   <ReactQuill
    //     theme="snow"
    //     value={this.state.value}
    //     onChange={this.setText}
    //   />
    //   {/*<div dangerouslySetInnerHTML={{__html: this.state.value}}/>*/}
    // </>
    )
  }
}

const mapStateToProps = state => {
  let element = findById(state.rubric.rubric, state.rubric.selectedElement);

  return {
    element: element,
    currentPath: state.rubric.currentPath
  };
};

const actionCreators = {
  alterCriterionText,
}

export default connect(mapStateToProps, actionCreators)(RubricEditorRichText)


















