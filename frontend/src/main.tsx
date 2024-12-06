import "@fontsource/fira-sans";
import { CssBaseline, ThemeProvider } from "@mui/material";
import { QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import LangWrapper from "lang/LangWrapper.tsx";
import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.tsx";
import queryClient from "./hooks/client.ts";
import theme from "./styles/theme.ts";
import AccountProvider from "./context/AccountContextProvider.tsx";
import ConfigurationProvider from "./context/ConfigurationContextProvider.tsx";

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <ConfigurationProvider>
        <QueryClientProvider client={queryClient}>
          <LangWrapper>
            <AccountProvider>
              <App />
            </AccountProvider>
          </LangWrapper>
          <ReactQueryDevtools />
        </QueryClientProvider>
      </ConfigurationProvider>
    </ThemeProvider>
  </React.StrictMode>,
);
