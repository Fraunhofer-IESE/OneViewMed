import { Stack, useTheme } from "@mui/material";
import { FC } from "react";
import { trimString } from "utils";
import DataTile from "./DataTile";

type DiagnoseTileProps = {
  loading?: boolean;
  diagnose: string;
  lastEvent?: string | null;
  surgery: string | undefined;
};

const DiagnoseTile: FC<DiagnoseTileProps> = ({
  loading = false,
  diagnose,
  lastEvent,
  surgery,
}) => {
  const theme = useTheme();

  return (
    <Stack spacing={theme.spacing(1)}>
      <Stack spacing={theme.spacing(1)}>
        <DataTile
          loading={loading}
          title="diagnose"
          data={trimString(diagnose)}
          boldText
        />
        <DataTile loading={loading} title={"Haupt-Operation"} data={surgery} />
        <DataTile
          loading={loading}
          title={"Wichtige Ereignisse"}
          data={lastEvent}
        />
      </Stack>
    </Stack>
  );
};

export default DiagnoseTile;
