import { Box, Typography, useTheme } from "@mui/material";
import { FC } from "react";

export type ChronologyBoxProps = {
  title: string;
  type: string;
  color: string;
  url?: string;
};

const ChronologyBox: FC<ChronologyBoxProps> = (props) => {
  const { title, type, color, url } = props;
  const theme = useTheme();

  return (
    <a
      href={url}
      rel="noopener noreferrer"
      target="_blank"
      style={{ textDecoration: "unset" }}
    >
      <Box
        sx={{
          border: `1.5px solid ${color}`,
          borderRadius: 1,
          padding: theme.spacing(1),
          marginY: theme.spacing(1),
          marginX: theme.spacing(4),
        }}
      >
        <Typography
          lineHeight={1}
          color="text.secondary"
          fontSize="0.8rem"
          textAlign="center"
        >
          {type}
        </Typography>
        <Typography fontWeight="bold" textAlign="center" fontSize="0.8rem">
          {title}
        </Typography>
      </Box>
    </a>
  );
};

export default ChronologyBox;
