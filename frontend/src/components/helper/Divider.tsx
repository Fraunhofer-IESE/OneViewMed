import { Box } from "@mui/material";
import { FC } from "react";

type DividerProps = {
  color?: string;
};

const Divider: FC<DividerProps> = ({ color }) => {
  return (
    <Box
      component="hr"
      sx={{
        border: (theme) =>
          `1px solid ${theme.palette.mode === "dark" ? "#fff" : color ? color : theme.palette.divider}`,
      }}
    />
  );
};

export default Divider;
