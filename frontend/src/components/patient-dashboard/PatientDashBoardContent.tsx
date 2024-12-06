import { Box, Grid, Tab, Tabs, useTheme } from "@mui/material";
import { Dashboard } from "@one-view-med/client-api";
import { QueryResult } from "components/helper";
import TabContent from "components/helper/TabContent";
import { useDashboard } from "hooks/dashboard";
import React, { FC, useCallback, useState } from "react";
import PatientDashBoardTile from "./PatientDashBoardTile";

const emptyDashBoardConfig: Dashboard = {
  title: "",
  groups: [],
};

function a11yProps(index: number) {
  return {
    id: `patient-tab-${index}`,
    "aria-controls": `patient-tabpanel-${index}`,
  };
}

type PatientDashboardContentProps = {
  patientId: string;
};

const PatientDashboardContent: FC<PatientDashboardContentProps> = (props) => {
  const { patientId } = props;
  const configQueryResult = useDashboard(patientId);
  const { data: dashBoardConfig = { ...emptyDashBoardConfig } } =
    configQueryResult;
  const { groups } = dashBoardConfig;
  const [currentTab, setCurrentTab] = useState<number>(0);
  const theme = useTheme();

  const handleTabChanged = useCallback(
    (event: React.SyntheticEvent, value: number) => {
      event.preventDefault();
      event.stopPropagation();
      setCurrentTab(value);
    },
    [],
  );

  return (
    <QueryResult result={configQueryResult}>
      <Tabs
        onChange={handleTabChanged}
        value={currentTab}
        sx={{
          borderBottom: `1px solid ${theme.palette.divider}`,
          minHeight: "unset",
          fontSize: "0.9rem",
          "& .MuiTabs-indicator": {
            display: "flex",
            justifyContent: "center",
            backgroundColor: theme.palette.secondary.main,
            maxWidth: "80%",
            width: "80%",
          },
        }}
      >
        {groups.map((group, index) => {
          const { title } = group;
          return <Tab key={index} label={title} {...a11yProps(index)} />;
        })}
      </Tabs>
      {groups.map((group, index) => {
        const { tiles } = group;
        const twoInOneTile = tiles.filter(
          ({ title }) => title === "Notfallkontakt" || title === "Behandelnde",
        );

        return (
          <TabContent
            key={index}
            prefix="patient"
            index={index}
            value={currentTab}
          >
            <Box paddingBottom={theme.spacing(2)}>
              <Grid container spacing={3} sx={{ alignItems: "stretch" }}>
                {twoInOneTile.length > 0 ? (
                  <Grid item xs={twoInOneTile[0].minWidth} display="grid">
                    <PatientDashBoardTile
                      patientId={patientId}
                      tile={twoInOneTile[0]}
                    />
                    <PatientDashBoardTile
                      patientId={patientId}
                      tile={twoInOneTile[1]}
                    />
                  </Grid>
                ) : null}
                {tiles
                  .filter(
                    ({ title }) =>
                      title !== "Notfallkontakt" && title !== "Behandelnde",
                  )
                  .map((tile) => {
                    const { id, minWidth = 1 } = tile;
                    return (
                      <Grid key={id} item xs={minWidth} display="grid">
                        <PatientDashBoardTile
                          patientId={patientId}
                          tile={tile}
                        />
                      </Grid>
                    );
                  })}
              </Grid>
            </Box>
          </TabContent>
        );
      })}
    </QueryResult>
  );
};

export default PatientDashboardContent;
