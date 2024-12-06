import { Box, Paper, Typography } from "@mui/material";
import { Tile } from "@one-view-med/client-api";
import { FC, useMemo } from "react";
import theme from "styles/theme";
import PatientDataTable from "./PatientDataTable";
import PatientDataTimeSeries from "./PatientDataTimeSeries";
import PatientDataValue from "./PatientDataValue";

type PatientDashBoardTileProps = {
  patientId: string;
  tile: Tile;
};

const PatientDashBoardTile: FC<PatientDashBoardTileProps> = (props) => {
  const { patientId, tile } = props;
  const { title, dataset, visualization } = tile;
  const { name, type, _configuration } = dataset;

  const configuration: Record<string, string> | undefined = useMemo(() => {
    if (!_configuration) {
      return undefined;
    }
    return Object.fromEntries(
      Object.entries(_configuration).map(([key, value]) => [
        key,
        value.toString(),
      ]),
    );
  }, [_configuration]);

  return (
    <Box display="flex" flexDirection="column">
      <Typography fontSize="1.1rem" gutterBottom>
        {title}
      </Typography>
      <Paper
        variant="outlined"
        sx={{
          padding: theme.spacing(2),
          overflow: "auto",
          height: "100%",
        }}
      >
        {
          {
            VALUE: (
              <PatientDataValue
                patientId={patientId}
                dataset={name}
                configuration={configuration}
              />
            ),
            TABLE: (
              <PatientDataTable
                patientId={patientId}
                dataset={name}
                configuration={configuration}
                visualization={visualization}
              />
            ),
            TIME_SERIES: (
              <PatientDataTimeSeries patientId={patientId} dataset={name} />
            ),
          }[type]
        }
      </Paper>
    </Box>
  );
};

export default PatientDashBoardTile;
