import React, { Component } from 'react'

import styles from './rubric.module.css'
import Card from "react-bootstrap/Card";
import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";

import 'draft-js/dist/Draft.css';
import {EditorState, convertToRaw } from 'draft-js';

import { stateFromHTML } from 'draft-js-import-html'
import {stateToHTML} from 'draft-js-export-html';

import Editor from '@draft-js-plugins/editor';
import '@draft-js-plugins/static-toolbar/lib/plugin.css';
import '@draft-js-plugins/hashtag/lib/plugin.css';

import createToolbarPlugin from '@draft-js-plugins/static-toolbar';

import {
  ItalicButton,
  BoldButton,
  UnderlineButton,
  CodeButton,
  HeadlineOneButton,
  HeadlineTwoButton,
  HeadlineThreeButton,
  UnorderedListButton,
  OrderedListButton,
  BlockquoteButton,
  CodeBlockButton,
} from '@draft-js-plugins/buttons';

// import createHashtagPlugin from '@draft-js-plugins/hashtag';
// const hashtagPlugin = createHashtagPlugin();
//
// const staticToolbarPlugin = createToolbarPlugin();
// const { Toolbar } = staticToolbarPlugin;
// const plugins = [staticToolbarPlugin, hashtagPlugin];

import { Draggable } from "react-beautiful-dnd";
import {removeCriterion, alterCriterion } from "../../redux/rubric/actions";
import {connect} from "react-redux";

import {FaArrowsAlt, FaPencilAlt, FaTimes, FaCheck, FaTrashAlt } from "react-icons/fa";

const getItemStyle = (isDragging, draggableStyle) => ({
  userSelect: "none",
  // boxShadow: isDragging ? "1px 3px 1px #E6EBEB": "none",
  // borderRadius: "5px",
  ...draggableStyle
});


class RubricCriterion extends Component {
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
      editorState: EditorState.createWithContent(stateFromHTML(props.data.text)),
      hasFocus: false,
    }
  }

  onTextEditorChange = (editorState) => {
    let alteredCriterion = {
      title: this.props.data.title,
      text: stateToHTML(editorState.getCurrentContent()),
    }

    this.props.alterCriterion(alteredCriterion, this.props.blockId, this.props.data.id);

    this.setState({
      editorState: editorState,
    });
  }

  setEditor = (editor) => {
    this.editor = editor;
  };

  focusEditor = () => {
    if (this.editor) {
      this.editor.focus();
    }
  };

  onChangeTitle = (event) => {
    let alteredCriterion = {
      title: event.target.value,
      text: stateToHTML(this.state.editorState.getCurrentContent()),
    }

    this.props.alterCriterion(alteredCriterion, this.props.blockId, this.props.data.id);
  }

  onClickRemoveCriterion = () => {
    this.props.removeCriterion(this.props.blockId, this.props.data.id);
  }

  // onTextEditorFocused = () => {
  //   this.setState({
  //     hasFocus: true,
  //   })
  // }

  render () {
    const { Toolbar } = this.pluginComponents;

    if (!this.props.isEditing) {
      // standard mode
      return (
        <Card className={styles.criterionCard}>
          <Card.Body>
            <Card.Title>
              <div className={styles.criterionCardTitle}>
                {this.props.data.title}
              </div>
            </Card.Title>
            <div>
              <div dangerouslySetInnerHTML={{__html: stateToHTML(this.state.editorState.getCurrentContent())}}/>
            </div>
          </Card.Body>
        </Card>
      )
    } else {
      // edit mode
      return(
        <Draggable
          key={this.props.data.id}
          draggableId={this.props.data.id}
          index={this.props.index}
        >
          {(provided, snapshot) => (
            <Card
              ref={provided.innerRef}
              {...provided.draggableProps}
              {...provided.dragHandleProps}
              // style={getItemStyle(
              //   snapshot.isDragging,
              //   provided.draggableProps.style
              // )}
              className={styles.criterionCard}
            >
              <Card.Body>
                <Card.Title>
                  <div className={styles.criterionCardTitle}>
                    <input type="text" name="title" value={this.props.data.title} onChange={this.onChangeTitle}/>
                    <div>
                      <Button variant="danger" onClick={this.onClickRemoveCriterion}><FaTrashAlt/></Button>
                    </div>
                  </div>
                </Card.Title>
                <div className={this.state.hasFocus ? `${styles.editor} ${styles.editorFocused}` : `${styles.editor}`} onClick={this.focusEditor}>
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
              </Card.Body>
            </Card>
          )}
        </Draggable>
      )
    }
  }
}

const mapStateToProps = state => {
  return {
    isEditing: state.rubric.isEditing,
  };
};

const actionCreators = {
  removeCriterion,
  alterCriterion
}

export default connect(mapStateToProps, actionCreators)(RubricCriterion)
