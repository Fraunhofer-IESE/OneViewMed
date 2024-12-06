import { CSSObject } from "@emotion/react";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import {
  Box,
  Drawer,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  styled,
  useTheme,
} from "@mui/material";
import { ElementType, FC, useState } from "react";
import { Outlet, useLocation, useNavigate } from "react-router-dom";

type DrawerLayoutProps = {
  drawerItems: Array<DrawerItem>;
};

export type DrawerItem = {
  title: string;
  path: string;
  Icon: ElementType;
};

const DrawerLayout: FC<DrawerLayoutProps> = ({ drawerItems }) => {
  const theme = useTheme();
  const [open, setOpen] = useState<boolean>(false);
  const navigate = useNavigate();
  const location = useLocation();

  const handleDrawerClick = () => setOpen((prevValue) => !prevValue);

  const drawerWidth = 240;

  const commonStyle: CSSObject = {
    boxShadow: `-3px 0px 7px ${theme.palette.primary.main}`,
    "& .Mui-selected": {
      backgroundColor: theme.palette.secondary.light,
      borderRadius: "0% 45% 45% 0%",
    },
    overflowX: "hidden",
  };

  const DrawerHeader = styled("div")(({ theme }) => ({
    display: "flex",
    alignItems: "center",
    padding: theme.spacing(0, 1),
    marginTop: theme.spacing(2),
  }));

  const openedMixin = (): CSSObject => ({
    width: drawerWidth,
    transition: "all 0.5s ease-in",
    ...commonStyle,
  });

  const closedMixin = (): CSSObject => ({
    transition: "all 0.5s ease-out",
    width: `calc(${theme.spacing(9)} + 1px)`,
    [theme.breakpoints.up("sm")]: {
      width: `calc(${theme.spacing(9)} + 1px)`,
    },
    ...commonStyle,
  });

  const MiniDrawer = styled(Drawer, {
    shouldForwardProp: (prop) => prop !== "open",
  })(() => ({
    width: drawerWidth,
    flexShrink: 0,
    whiteSpace: "nowrap",
    boxSizing: "border-box",
    variants: [
      {
        props: ({ open }) => open,
        style: {
          ...openedMixin(),
          "& .MuiDrawer-paper": openedMixin(),
        },
      },
      {
        props: ({ open }) => !open,
        style: {
          ...closedMixin(),
          "& .MuiDrawer-paper": closedMixin(),
        },
      },
    ],
  }));

  return (
    <Box display="flex" height="100%">
      <MiniDrawer variant="permanent" anchor="left" open={open}>
        <DrawerHeader>
          <IconButton
            onClick={handleDrawerClick}
            color="secondary"
            sx={{
              border: `1px solid ${theme.palette.divider}`,
              marginTop: theme.spacing(2),
            }}
          >
            {open ? <ChevronLeftIcon /> : <ChevronRightIcon />}
          </IconButton>
        </DrawerHeader>
        <List>
          {drawerItems.map(({ title, path, Icon }) => (
            <ListItem
              key={title}
              disablePadding
              sx={{
                display: "block",
              }}
            >
              <ListItemButton
                key={title}
                selected={location.pathname === path}
                onClick={() => navigate(path)}
                sx={{
                  width: "48px",
                  height: "48px",
                }}
              >
                <ListItemIcon
                  sx={[
                    {
                      minWidth: 0,
                      justifyContent: "center",
                    },
                    open ? { mr: theme.spacing(2) } : { mr: "0" },
                  ]}
                >
                  <Icon
                    fill={theme.palette.secondary.main}
                    width={24}
                    height={24}
                  />
                </ListItemIcon>
                <ListItemText
                  primary={title}
                  color={theme.palette.secondary.main}
                  sx={{
                    display: open ? "" : "none",
                    color: theme.palette.secondary.main,
                  }}
                />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
      </MiniDrawer>
      <Outlet />
    </Box>
  );
};

export default DrawerLayout;
