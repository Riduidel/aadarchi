import { createTheme, StyledEngineProvider, ThemeProvider } from '@mui/material/styles';
import React from 'react';

const baseFonts = [
  '-apple-system',
  'BlinkMacSystemFont',
  '"Segoe UI"',
  'Roboto',
  '"Helvetica Neue"',
  'Arial',
  'sans-serif',
  '"Apple Color Emoji"',
  '"Segoe UI Emoji"',
  '"Segoe UI Symbol"',
];

const theme = createTheme({
  shape: {
    borderRadius: 8,
  },
  components: {
    MuiTableCell: {
      styleOverrides: {
        root: {
          fontSize: 16,
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 600,
          borderRadius: '6em',
          padding: '9px 24px',
          fontFamily: ['Nunito', ...baseFonts].join(','),
        },
        containedPrimary: {
          background: 'linear-gradient(135deg, #DB2244 0%, #C4285F 100%)',
          boxShadow: '0 3px 6px rgba(196, 40, 95, .3)',
          '&:hover': {
            boxShadow: '0 3px 6px rgba(196, 40, 95, .3)',
          },
          '&:disabled': {
            background: '#ddd',
          },
        },
        containedSizeSmall: {
          padding: '4px 16px',
        },
        text: {
          padding: '4px 16px',
        },
      },
    },
  },
  palette: {
    text: {
      primary: '#42454d',
    },
    primary: {
      main: '#B31835',
    },
    secondary: {
      main: '#B31835',
    },
  },
  status: {
    danger: 'orange',
  },
  typography: {
    fontFamily: ['Nunito', ...baseFonts].join(','),
    h1: {
      fontFamily: ['Nunito', ...baseFonts].join(','),
      fontWeight: 600,
    },
    h2: {
      fontFamily: ['Nunito', ...baseFonts].join(','),
      fontWeight: 600,
    },
    h3: {
      fontFamily: ['Nunito', ...baseFonts].join(','),
      fontWeight: 600,
    },
    h4: {
      fontFamily: ['Nunito', ...baseFonts].join(','),
      fontWeight: 600,
    },
    h5: {
      fontFamily: ['Nunito', ...baseFonts].join(','),
      fontWeight: 600,
    },
    h6: {
      fontFamily: ['Nunito', ...baseFonts].join(','),
      fontWeight: 600,
    },
  },
});

function QvgdcTheme(props) {
  return (
    <StyledEngineProvider injectFirst>
      <ThemeProvider theme={theme}>{props.children}</ThemeProvider>
    </StyledEngineProvider>
  );
}

export default QvgdcTheme;
