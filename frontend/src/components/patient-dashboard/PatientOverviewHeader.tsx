import { Box, Divider, Grid, Typography, useTheme } from "@mui/material";
import { VirusIcon, WarningIcon } from "assets";
import { NoDataMessage, QueryResult } from "components/helper";
import { DataTile } from "components/patient-tile";
import { FC, useMemo } from "react";
import { IconOptions } from "styles";
import { formatDate, getAge, getGender, trimString } from "utils";
import { usePatientOverview } from "../../hooks/patient-overview";
import { useGetPatient } from "../../hooks/patients";

type PatientOverviewHeaderProps = {
  patientId: string;
};

const PatientOverviewHeader: FC<PatientOverviewHeaderProps> = ({
  patientId,
}) => {
  const theme = useTheme();
  const queryResult = useGetPatient(patientId);
  const overviewResult = usePatientOverview(patientId);
  const { data: patient, isLoading: isPatientLoading } = queryResult;
  const { data: patientOverview, isLoading: isOverviewLoading } =
    overviewResult;
  const birthDate = patient?.birthDate ? patient.birthDate : "";

  const stationStayData = useMemo(() => {
    if (!patientOverview) {
      return "Keine Daten";
    }
    const { stationStays } = patientOverview;
    if (stationStays && stationStays.length > 0) {
      const currentStationStay = stationStays[stationStays.length - 1];
      const { days, name } = currentStationStay;
      return `${days} Tage (${name})`;
    }
    return "Keine Daten";
  }, [patientOverview]);

  return (
    <QueryResult result={queryResult}>
      <Grid
        container
        id="topbar"
        spacing={theme.spacing(2)}
        marginBottom={theme.spacing(3)}
        sx={{
          ".MuiGrid-root > .MuiGrid-item": {
            paddingTop: "unset",
          },
        }}
      >
        {patient ? (
          <>
            <Grid item display="flex">
              <Typography fontSize="2rem" fontWeight="600">
                {patient.familyName}, &thinsp;
              </Typography>
              <Typography fontSize="2rem" fontWeight="400">
                {patient.givenName}
              </Typography>
              <Typography fontSize="2rem" fontWeight="300">
                &thinsp;{"| Fall-Nr."} {patient.caseNumber}
              </Typography>
            </Grid>
            <Grid
              container
              margin="unset"
              width="100%"
              spacing={theme.spacing(2)}
              marginTop={theme.spacing(2)}
            >
              <Grid item>
                <DataTile
                  title="Geburstdatum"
                  data={`${formatDate(birthDate)} (${getAge(birthDate)} Jahre)`}
                  loading={isPatientLoading}
                />
              </Grid>
              <Divider
                orientation="vertical"
                sx={{
                  borderColor: theme.palette.divider,
                  marginLeft: theme.spacing(2),
                }}
                flexItem
              />
              <Grid item>
                <DataTile
                  title="Geschlecht"
                  data={getGender(patient.gender)}
                  loading={isPatientLoading}
                />
              </Grid>
              <Divider
                orientation="vertical"
                sx={{
                  borderColor: theme.palette.divider,
                  marginLeft: theme.spacing(2),
                }}
                flexItem
              />
              <Grid item>
                <DataTile
                  title="Infektiös"
                  data={
                    patientOverview?.infection == null ? (
                      "nein"
                    ) : (
                      <Box
                        display="inline-flex"
                        justifyContent="space-between"
                        alignItems="center"
                      >
                        <VirusIcon
                          {...IconOptions}
                          fill={theme.palette.error.main}
                          style={{ marginRight: theme.spacing(1) }}
                        />
                        <Typography>
                          {trimString(patientOverview.infection, 20)}
                        </Typography>
                      </Box>
                    )
                  }
                  loading={isOverviewLoading}
                />
              </Grid>
              <Divider
                orientation="vertical"
                sx={{
                  borderColor: theme.palette.divider,
                  marginLeft: theme.spacing(2),
                }}
                flexItem
              />
              <Grid item>
                <DataTile
                  title="Komplikationen"
                  data={
                    patientOverview?.complication ? (
                      <Box
                        display="inline-flex"
                        justifyContent="space-around"
                        alignItems="center"
                      >
                        <WarningIcon
                          {...IconOptions}
                          fill={theme.palette.error.main}
                        />
                        <Typography>ja</Typography>
                      </Box>
                    ) : (
                      <Typography>nein</Typography>
                    )
                  }
                  loading={isOverviewLoading}
                />
              </Grid>
              <Divider
                orientation="vertical"
                sx={{
                  borderColor: theme.palette.divider,
                  marginLeft: theme.spacing(2),
                }}
                flexItem
              />
              <Grid item>
                <DataTile
                  title="Allergien"
                  data={
                    patientOverview?.allergies == null ? (
                      "k.A."
                    ) : patientOverview?.allergies ? (
                      <Box
                        display="inline-flex"
                        justifyContent="space-around"
                        alignItems="center"
                      >
                        <WarningIcon
                          {...IconOptions}
                          fill={theme.palette.error.main}
                        />
                        <Typography>ja</Typography>
                      </Box>
                    ) : (
                      "nein"
                    )
                  }
                  loading={isOverviewLoading}
                />
              </Grid>
              <Divider
                orientation="vertical"
                sx={{
                  borderColor: theme.palette.divider,
                  marginLeft: theme.spacing(2),
                }}
                flexItem
              />
              <Grid item>
                <DataTile
                  title="Aufnahme"
                  data={stationStayData}
                  loading={isOverviewLoading}
                />
              </Grid>
            </Grid>
          </>
        ) : (
          <NoDataMessage />
        )}
      </Grid>
    </QueryResult>
  );
};

export default PatientOverviewHeader;
