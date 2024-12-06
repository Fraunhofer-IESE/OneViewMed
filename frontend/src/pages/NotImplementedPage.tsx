import { Box, Button, Stack, Typography, useTheme } from "@mui/material";
import { FC, useCallback } from "react";
import { useNavigate } from "react-router-dom";

const NotImplementedPage: FC = () => {
  const theme = useTheme();
  const navigate = useNavigate();

  const goBack = useCallback(() => {
    navigate("/");
  }, [navigate]);

  return (
    <Box id="dev-page" alignContent="center" height="100%" width="100%">
      <Stack spacing={theme.spacing(2)} alignItems="center">
        <Typography variant="body1" textAlign="center">
          Diese Seite ist noch nicht verfügbar.
        </Typography>
        <Box>
          <Button
            variant="contained"
            size="small"
            sx={{ marginInline: "auto" }}
            onClick={goBack}
          >
            Zum Dashboard
          </Button>
        </Box>
      </Stack>
    </Box>
  );
};

export default NotImplementedPage;
