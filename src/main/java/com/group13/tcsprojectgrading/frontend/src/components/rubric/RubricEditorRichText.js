import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";

import Editor from "@draft-js-plugins/editor";
import {stateToHTML} from "draft-js-export-html";
import createToolbarPlugin from "@draft-js-plugins/static-toolbar";
import {
  BlockquoteButton,
  BoldButton, CodeBlockButton,
  ItalicButton,
  OrderedListButton,
  UnderlineButton,
  UnorderedListButton
} from "@draft-js-plugins/buttons";
import {EditorState} from "draft-js";
import {stateFromHTML} from "draft-js-import-html";
import {alterCriterionText} from "../../redux/rubric/actions";
import {findById} from "../../redux/rubric/functions";

class RubricEditorRichText extends Component {
  constructor (props) {
    super(props);

    const toolbarPlugin = createToolbarPlugin({
      structure: [
        BoldButton,
        ItalicButton,
        UnderlineButton,
        OrderedListButton,
        UnorderedListButton,
        BlockquoteButton,
        CodeBlockButton
      ],
      theme: {
        toolbarStyles: {
          toolbar: styles.toolbar,
        },
        buttonStyles: {
          button: styles.toolbarButton,
          buttonWrapper: styles.toolbarButtonWrapper,
          active: styles.toolbarButtonActive,
        },
      }
    });
    this.pluginComponents = {
      Toolbar: toolbarPlugin.Toolbar
    };
    this.plugins = [toolbarPlugin];

    this.state = {
      editorState: EditorState.createWithContent(stateFromHTML(this.props.element.content.text)),
      hasFocus: false,
    }
  }

  onTextEditorChange = (newEditorState) => {
    const currentContentState = this.state.editorState.getCurrentContent()
    const newContentState = newEditorState.getCurrentContent()

    if (currentContentState !== newContentState) {
      // change in content
      this.setState({
        editorState: newEditorState
      })

      this.props.alterCriterionText(this.props.element.content.id, stateToHTML(newEditorState.getCurrentContent()), this.props.currentPath + "/content/text");
    } else {
      //  change in focus or selection
    }
  }

  setEditor = (editor) => {
    this.editor = editor;
  };

  focusEditor = () => {
    if (this.editor) {
      this.editor.focus();
    }
  };

  render () {
    const { Toolbar } = this.pluginComponents;

    return (
      <div className={this.state.hasFocus ? `${styles.richTextEditor} ${styles.editorFocused}` : `${styles.richTextEditor}`} onClick={this.focusEditor}>
        <Editor
          ref={this.setEditor}
          editorState={this.state.editorState}
          onChange={this.onTextEditorChange}
          plugins={this.plugins}
          onFocus={() => this.setState({ hasFocus: true })}
          onBlur={() => this.setState({ hasFocus: false })}
        />
        <Toolbar/>
      </div>
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


















