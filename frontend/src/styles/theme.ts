import { createTheme, responsiveFontSizes } from "@mui/material/styles";
import type {} from "@mui/material/themeCssVarsAugmentation";

const theme = createTheme({
  breakpoints: {
    values: {
      xs: 0,
      sm: 700,
      md: 900,
      lg: 1300,
      xl: 1536,
    },
  },
  typography: {
    fontFamily: ["Fire Sans", "sans-serif"].join(","),
  },
  palette: {
    mode: "light",
    primary: { main: "#252F60", contrastText: "#ffffff", light: "#2745DF" },
    secondary: { main: "#5407CE", light: "#EAE8FF", contrastText: "#ffffff" },
    background: { default: "#F2F5FE", paper: "#ffffff" },
    success: { main: "#06B7A2" },
    error: { main: "#D1070B" },
    info: { main: "#2745DF" },
    text: {
      primary: "#040E44",
      secondary: "#646D98",
    },
    divider: "#AFB6F4",
    grey: {
      "50": "#DFE2FF",
      "200": "#f2f2f2",
      "300": "#ececec",
      "500": "#808080",
      "600": "#909090",
    },
    action: { active: "#B70682" },
  },
  components: {
    MuiSkeleton: {
      defaultProps: {
        animation: "wave",
      },
    },
    MuiTableCell: {
      defaultProps: {
        sx: {
          borderBottom: `unset`,
        },
      },
    },
    MuiTab: {
      defaultProps: {
        sx: {
          textTransform: "capitalize",
          letterSpacing: "0.02rem",
          fontSize: "0.95rem",
          padding: "2px 4px 2px 0px",
          minHeight: "30px",
          textAlign: "left",
        },
      },
    },
    MuiTypography: {
      defaultProps: {
        fontSize: "0.9rem",
      },
    },
  },
});

export default responsiveFontSizes(theme);
