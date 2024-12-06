import {
  Box,
  Grid,
  Skeleton,
  Stack,
  Step,
  Stepper,
  Typography,
  useTheme,
} from "@mui/material";
import { PatientOverview, StationStay } from "@one-view-med/client-api";
import { FC } from "react";
import { isEmptyNullOrUndefined } from "utils";
import DataTile from "./DataTile";

type StationStayTileProps = {
  loading?: boolean;
  stationStays?: StationStay[];
  postSurgeryDays?: PatientOverview["postSurgeryDays"];
};

const StationStayTile: FC<StationStayTileProps> = ({
  loading = false,
  stationStays = [],
  postSurgeryDays = 0,
}) => {
  const totalDays = stationStays
    .map((stationStay) => stationStay.days ?? 0)
    .reduce((previous, current) => previous + current, 0);

  const theme = useTheme();
  return (
    <Grid container spacing={theme.spacing(2)}>
      <Grid
        item
        xs={12}
        display="flex"
        justifyContent="space-between"
        flexWrap="wrap"
      >
        <DataTile
          loading={loading}
          title="Liegezeit gesamt"
          data={`${totalDays} Tage`}
        />
        <DataTile
          loading={loading}
          title="Post-OP"
          data={postSurgeryDays.toString()}
        />
      </Grid>
      <Grid item xs={12}>
        {loading ? (
          <Skeleton />
        ) : (
          <Stepper activeStep={stationStays.length - 1}>
            {stationStays.map((stationStay, index) => (
              <Step key={index} sx={{ paddingInline: "unset" }}>
                <Box
                  sx={{
                    minWidth: "4em",
                    border: `1px solid ${index === stationStays.length - 1 ? theme.palette.info.light : theme.palette.divider}`,
                    borderRadius: 2,
                    padding: theme.spacing(1),
                  }}
                >
                  <Stack direction="column" alignItems="center">
                    <Typography
                      fontSize="0.75rem"
                      sx={{
                        whiteSpace: "nowrap",
                        color: theme.palette.text.secondary,
                      }}
                    >
                      {stationStay.name}
                    </Typography>
                    <Typography
                      sx={{ whiteSpace: "nowrap" }}
                      fontSize="0.75rem"
                    >
                      {isEmptyNullOrUndefined(stationStay.days)
                        ? "-"
                        : `${stationStay.days} Tage`}
                    </Typography>
                  </Stack>
                </Box>
              </Step>
            ))}
          </Stepper>
        )}
      </Grid>
    </Grid>
  );
};

export default StationStayTile;
