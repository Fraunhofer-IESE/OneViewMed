import { Skeleton, Stack, Typography, useTheme } from "@mui/material";
import { FC, ReactNode } from "react";

type DataTileProps = {
  loading?: boolean;
  title: string;
  data: string | ReactNode;
  center?: boolean;
  boldText?: boolean;
};

const DataTile: FC<DataTileProps> = ({
  loading = false,
  title,
  data,
  center = false,
  boldText = false,
}) => {
  const theme = useTheme();

  return (
    <Stack>
      <Typography
        variant="overline"
        textTransform="capitalize"
        lineHeight={1}
        fontSize="0.8rem"
        whiteSpace="nowrap"
        color={theme.palette.text.secondary}
      >
        {title}
      </Typography>
      {loading ? (
        <Skeleton />
      ) : typeof data == "string" ? (
        <Typography
          sx={{
            overflowWrap: "anywhere",
            marginInline: center ? "auto" : "unset",
            fontWeight: boldText ? "bold" : "normal",
          }}
        >
          {data && data !== "Unbekannt" ? data : "-"}
        </Typography>
      ) : (
        data
      )}
    </Stack>
  );
};

export default DataTile;
