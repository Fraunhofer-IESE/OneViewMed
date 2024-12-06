import { Box, Button, Stack, Typography, useTheme } from "@mui/material";
import { FC, useCallback } from "react";
import { useNavigate, useRouteError } from "react-router-dom";

type Error = {
  statusText?: string;
  message?: string;
};

const ErrorPage: FC = () => {
  const error = useRouteError() as Error;
  const theme = useTheme();
  const navigate = useNavigate();

  const goBack = useCallback(() => {
    navigate("/");
  }, [navigate]);

  return (
    <Box id="error-page" alignContent="center" height="100%" width="100%">
      <Stack spacing={theme.spacing(2)} alignItems="center">
        <Typography variant="h2" fontWeight="bold" textAlign="center">
          Oops!
        </Typography>
        <Typography variant="body1" textAlign="center">
          Sorry, etwas ist schief gelaufen.
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
        <Typography variant="body1" textAlign="center">
          <i>Error: {error.statusText || error.message}</i>
        </Typography>
      </Stack>
    </Box>
  );
};

export default ErrorPage;
