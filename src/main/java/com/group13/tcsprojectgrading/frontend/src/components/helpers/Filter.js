import React, {Component} from "react";
import MenuItem from "@material-ui/core/MenuItem";
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import Select from "@material-ui/core/Select";

class Filter extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return(
      <FormControl
        variant="outlined"
        className={this.props.className}
      >
        <InputLabel>{this.props.label}</InputLabel>
        <Select
          value={this.props.selected}
          onChange={(event) => {
            this.props.setSelected(event.target.value)
          }}
          label={this.props.label}
        >
          {this.props.options.map((option, index) => (
            <MenuItem
              key={option}
              selected={option === this.props.selected}
              value={option}
            >
              {option}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    )
  }
}

export default Filter;