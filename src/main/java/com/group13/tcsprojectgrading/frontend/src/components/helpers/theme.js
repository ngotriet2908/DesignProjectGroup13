import { unstable_createMuiStrictModeTheme } from '@material-ui/core/styles';
import colors from "./colors.css";
import blue from '@material-ui/core/colors/blue';
import pink from '@material-ui/core/colors/pink';

export const theme = unstable_createMuiStrictModeTheme({
  palette: {
    // primary: {
    //   main: "#002C5F",
    // },
    // secondary: {
    //   main: "#822433",
    // },
    // primary: {
    //   main: "#2196f3",
    // },
    // secondary: {
    //   main: "#f50057",
    // },
    primary: {
      main: "#2f5d8a",
    },
    secondary: {
      main: "#ffc13b",
    },
    // error : {
    //
    // },
    warning: {
      main: "#d43808",
    },
    // info: {
    //
    // },
    success: {
      main: "#3b9b26",
    },
    ternary: {
      light: "#ffa88d",
      main: "#ff6e40",
      dark: "#e9430f",
    },
    quinary: {
      main: "#f5f0e1",
    },
    tonalOffset: 0.5,
    labels: {
      green: "#3b9b26",
      yellow: "#ffc13b",
      red: "#d43808",
      blue: "#2f5d8a",
    },
    additionalColors: {
      lightBlue: "rgba(36,155,241,0.18)",
      yellow: "#ffc13b",
    }
  },
});