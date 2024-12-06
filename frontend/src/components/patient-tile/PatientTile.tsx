import {
  Box,
  Card,
  CardActionArea,
  CardContent,
  Grid,
  Typography,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import { Patient } from "@one-view-med/client-api";
import { DataTile, DiagnoseTile, Divider } from "components";
import { FC } from "react";
import { Link } from "react-router-dom";
import { VirusIcon, WarningIcon } from "../../assets";
import { usePatientOverview } from "../../hooks/patient-overview";
import { IconOptions } from "../../styles";
import { getAge, getGender, trimString } from "../../utils";
import StationStayTile from "./StationStayTile";

type PatientTileProps = {
  patient: Patient;
  withLastEvent?: boolean;
  withStationStays?: boolean;
};

const PatientTile: FC<PatientTileProps> = ({
  patient,
  withLastEvent = false,
  withStationStays = false,
}) => {
  const theme = useTheme();

  const isSmallWidth = useMediaQuery(theme.breakpoints.down("sm"));
  const isMediumWidth = useMediaQuery(theme.breakpoints.down("lg"));
  const {
    data: patientOverview,
    isLoading,
    isError,
  } = usePatientOverview(patient.id);
  const stringTrimNumber = isSmallWidth ? 8 : isMediumWidth ? 12 : 15;

  return (
    <Card
      sx={{
        height: "100%",
        display: "flex",
        alignItems: "center",
        border: "1px solid " + theme.palette.divider,
        borderRadius: 3,
        alignContent: "space-between",
      }}
    >
      <CardActionArea
        sx={{ height: "100%" }}
        component={Link}
        to={`/patienten/${patient.id}`}
      >
        <CardContent
          sx={{ height: "100%", display: "flex", flexDirection: "column" }}
        >
          <Box display="flex" justifyContent="space-between">
            <Typography
              variant="overline"
              textTransform="capitalize"
              color={theme.palette.text.secondary}
              lineHeight="unset"
              fontSize="0.7rem"
            >
              Fall-nr.
            </Typography>
            <Typography
              fontWeight="bold"
              fontSize="0.7rem"
              color={theme.palette.text.primary}
            >
              {patient.caseNumber}
            </Typography>
          </Box>
          <Box>
            <Divider color={theme.palette.divider} />
            <Grid
              container
              spacing={2}
              flexWrap={isSmallWidth ? "wrap" : "nowrap"}
              sx={{
                justifyContent: "space-between",
                alignItems: "stretch",
              }}
            >
              <Grid item>
                <DataTile
                  title="Name"
                  data={trimString(
                    `${patient.familyName}, ${patient.givenName}`,
                    stringTrimNumber,
                  )}
                />
              </Grid>
              <Grid item>
                <DataTile
                  title="Alter"
                  data={
                    patient.birthDate ? `${getAge(patient.birthDate)}` : "-"
                  }
                />
              </Grid>
              <Grid item>
                <DataTile title="Geschlecht" data={getGender(patient.gender)} />
              </Grid>
            </Grid>
          </Box>
          <Box>
            <Divider color={theme.palette.divider} />
            <DiagnoseTile
              loading={isLoading}
              diagnose={patientOverview?.diagnosis ?? "-"}
              surgery={patientOverview?.surgery ?? "-"}
              lastEvent={
                withLastEvent &&
                patientOverview?.importantEvents &&
                patientOverview?.importantEvents.length > 0
                  ? patientOverview?.importantEvents?.join(", ")
                  : "-"
              }
            />
          </Box>
          {withStationStays && (
            <Box flex="1 1 auto">
              <Divider color={theme.palette.divider} />
              <StationStayTile
                loading={isLoading}
                stationStays={patientOverview?.stationStays}
                postSurgeryDays={patientOverview?.postSurgeryDays}
              />
            </Box>
          )}
          <Box>
            <Divider color={theme.palette.divider} />
            <Grid
              container
              spacing={2}
              flexWrap={isSmallWidth ? "wrap" : "nowrap"}
              sx={{
                justifyContent: "space-between",
                alignItems: "stretch",
              }}
            >
              <Grid item>
                <DataTile
                  loading={isLoading}
                  title="infektiös"
                  data={
                    patientOverview?.infection == null ? (
                      "nein"
                    ) : (
                      <Box display="flex" alignItems="center">
                        <VirusIcon
                          {...IconOptions}
                          fill={theme.palette.error.main}
                          style={{ marginRight: theme.spacing(1) }}
                        />
                        <Typography>
                          {trimString(patientOverview?.infection, 10)}
                        </Typography>
                      </Box>
                    )
                  }
                />
              </Grid>
              <Grid item>
                <DataTile
                  loading={isLoading}
                  title="kompl."
                  data={
                    patientOverview?.complication ? (
                      <Box display="flex" alignItems="center">
                        <WarningIcon
                          {...IconOptions}
                          fill={theme.palette.error.main}
                          style={{ marginRight: theme.spacing(1) }}
                        />
                        <Typography>ja</Typography>
                      </Box>
                    ) : (
                      <Typography>nein</Typography>
                    )
                  }
                />
              </Grid>
              <Grid item>
                <DataTile
                  loading={isLoading}
                  title="allergien"
                  data={
                    patientOverview?.allergies == null ? (
                      "k.A."
                    ) : patientOverview?.allergies ? (
                      <Box display="flex" alignItems="center">
                        <WarningIcon
                          {...IconOptions}
                          fill={theme.palette.error.main}
                          style={{ marginRight: theme.spacing(1) }}
                        />
                        <Typography>ja</Typography>
                      </Box>
                    ) : (
                      "nein"
                    )
                  }
                />
              </Grid>
              {isError && (
                <Grid item>
                  <Typography color="error">
                    Patientendetails konnten nicht geladen werden
                  </Typography>
                </Grid>
              )}
            </Grid>
          </Box>
        </CardContent>
      </CardActionArea>
    </Card>
  );
};

export default PatientTile;
