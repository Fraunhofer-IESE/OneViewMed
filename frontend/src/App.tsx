import { Box, CssBaseline } from "@mui/material";

import {
  InformationIcon,
  LogoutIcon,
  RecentlyViewedIcon,
  SettingsIcon,
  WorkspaceIcon,
} from "assets";
import RootPage from "pages/RootPage";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import "./App.css";
import {
  Dashboard,
  ErrorPage,
  LogoutPage,
  NotImplementedPage,
  PatientDetails,
} from "./pages";

const APP_BASE_PATH = import.meta.env.BASE_URL;

const router = createBrowserRouter(
  [
    {
      element: (
        <RootPage
          drawerItems={[
            { title: "Dashboard", path: "/", Icon: WorkspaceIcon },
            {
              title: "Zuletzt Angesehen",
              path: "/entwicklung1",
              Icon: RecentlyViewedIcon,
            },
            { title: "Infos", path: "/entwicklung2", Icon: InformationIcon },
            {
              title: "Einstellungen",
              path: "/entwicklung3",
              Icon: SettingsIcon,
            },
            { title: "Ausloggen", path: "/logout", Icon: LogoutIcon },
          ]}
        />
      ),
      errorElement: <ErrorPage />,
      children: [
        {
          path: "/",
          element: <Dashboard />,
          errorElement: <ErrorPage />,
        },
        {
          path: "patienten/:patientId",
          element: <PatientDetails />,
          errorElement: <ErrorPage />,
        },
        {
          path: "/logout",
          element: <LogoutPage />,
          errorElement: <ErrorPage />,
        },
        {
          path: "/entwicklung1",
          element: <NotImplementedPage />,
          errorElement: <ErrorPage />,
        },
        {
          path: "/entwicklung2",
          element: <NotImplementedPage />,
          errorElement: <ErrorPage />,
        },
        {
          path: "/entwicklung3",
          element: <NotImplementedPage />,
          errorElement: <ErrorPage />,
        },
      ],
    },
  ],
  {
    basename: APP_BASE_PATH,
  },
);

const App = () => {
  return (
    <Box height="100%">
      <CssBaseline />
      <RouterProvider router={router} />
    </Box>
  );
};

export default App;
