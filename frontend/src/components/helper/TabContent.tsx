import { Box } from "@mui/material";
import { FC } from "react";

type TabContentProps = {
  children?: React.ReactNode;
  prefix: string;
  index: number;
  value: number;
};

const TabContent: FC<TabContentProps> = (props) => {
  const { children, prefix, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`${prefix}-tabpanel-${index}`}
      aria-labelledby={`${prefix}-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ paddingTop: 2 }}>{children}</Box>}
    </div>
  );
};

export default TabContent;
