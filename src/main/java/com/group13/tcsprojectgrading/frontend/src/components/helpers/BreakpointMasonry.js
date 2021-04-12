import React from "react";
import { makeStyles, useTheme } from "@material-ui/core/styles";
import Masonry from "react-masonry-css";

// from https://github.com/mui-org/material-ui/issues/17000 by chrishoermann

const useStyles = makeStyles((theme) => ({
  root: {
    margin: theme.spacing(3, 0, 2, 0),
  },
  paper: {
    marginBottom: theme.spacing(4),
  },
  masonryGrid: {
    display: "flex",
    marginLeft: theme.spacing(-4),
    width: "inherit",
  },
  masonryColumn: {
    paddingLeft: theme.spacing(4),
    backgroundClip: "padding-box",
  },
}));

const BreakpointMasonry = ({ children }) => {
  const classes = useStyles();
  const theme = useTheme();

  const breakpointCols = {
    default: 2,
    [theme.breakpoints.values.xl]: 2,
    [theme.breakpoints.values.lg]: 2,
    [theme.breakpoints.values.md]: 2,
    [theme.breakpoints.values.sm]: 2,
    [theme.breakpoints.values.xs]: 1,
  };

  return (
    <Masonry
      breakpointCols={breakpointCols}
      className={classes.masonryGrid}
      columnClassName={classes.masonryColumn}
    >
      {children}
    </Masonry>
  );
};

export default BreakpointMasonry;