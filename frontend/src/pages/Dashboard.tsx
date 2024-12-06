import {
  Box,
  Container,
  Grid,
  Typography,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import { Patient } from "@one-view-med/client-api";
import { FC } from "react";
import { PatientTile, QueryResult } from "../components";
import { usePatients } from "../hooks/patients";

const Dashboard: FC = () => {
  const queryResult = usePatients();
  const { data: patients = [] } = queryResult;

  const theme = useTheme();
  const isLandscape = useMediaQuery(theme.breakpoints.up("lg"));

  return (
    <Box minHeight="100vh" minWidth="75%">
      <Container maxWidth={false}>
        <QueryResult result={queryResult}>
          <Box my={2}>
            <Typography fontSize="1.2rem" marginY={2}>
              Patienten auf der Station
            </Typography>
            <Grid container spacing={2}>
              {patients.map((patient: Patient) => {
                return (
                  <Grid item key={patient.id} xs={isLandscape ? 3 : 4}>
                    <PatientTile
                      patient={patient}
                      withLastEvent
                      withStationStays
                    />
                  </Grid>
                );
              })}
            </Grid>
          </Box>
        </QueryResult>
      </Container>
    </Box>
  );
};

export default Dashboard;
