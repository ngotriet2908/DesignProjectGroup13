import React, {Component} from "react";
import FilterListIcon from "@material-ui/icons/FilterList";
import IconButton from "@material-ui/core/IconButton";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";

class TableFilter extends Component {
  constructor(props) {
    super(props);

    this.state = {
      anchor: null,
    }
  }

  handleClickListItem = (event) => {
    this.setState({
      anchor: event.currentTarget
    })
  }

  handleMenuItemClick = (event, index) => {
    this.setState({
      anchor: null,
    })

    this.props.setSelected(index)
  };

  handleClose = () => {
    this.setState({
      anchor: null
    })
  };

  render() {
    return(
      <div style={{display: "inline-block"}}>
        <IconButton aria-label="filter" size={this.props.size} style={{marginRight: "0.5rem"}} onClick={this.handleClickListItem}>
          <FilterListIcon />
        </IconButton>

        <Menu
          id="lock-menu"
          anchorEl={this.state.anchor}
          keepMounted
          open={Boolean(this.state.anchor)}
          onClose={this.handleClose}
        >
          {this.props.options.map((option, index) => (
            <MenuItem
              key={option}
              selected={index === this.props.selected}
              onClick={(event) => this.handleMenuItemClick(event, index)}
            >
              {option}
            </MenuItem>
          ))}
        </Menu>
      </div>
    )
  }


}

export default TableFilter;