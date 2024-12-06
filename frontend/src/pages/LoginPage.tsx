import {
  Alert,
  Box,
  Button,
  Container,
  Stack,
  TextField,
  Typography,
  useTheme,
} from "@mui/material";
import useAccount from "hooks/account";
import { FC, useCallback, useState } from "react";

const USERNAME = "test";
const PASSWORD = "test";

const LoginPage: FC = () => {
  const [username, setUsername] = useState(USERNAME);
  const [password, setPassword] = useState(PASSWORD);
  const [error, setError] = useState(false);
  const { onLogIn } = useAccount();

  const theme = useTheme();

  const handleLogin = useCallback(() => {
    if (username === USERNAME && password === PASSWORD) {
      onLogIn(username);
    } else {
      setError(true);
    }
  }, [onLogIn, password, username]);

  return (
    <Box height="100%" alignContent="center" justifyContent="center">
      <Typography variant="h1" textAlign="center">
        OneViewMed
      </Typography>
      <Container maxWidth="sm">
        <Stack spacing={theme.spacing(2)}>
          <Box bgcolor="common.white">
            <TextField
              label="Benutzername"
              fullWidth
              error={error}
              value={username}
              onChange={(event) => setUsername(event.target.value)}
            />
          </Box>
          <Box bgcolor="common.white">
            <TextField
              label="Passwort"
              type="password"
              fullWidth
              error={error}
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />
          </Box>
          {error && (
            <Box>
              <Alert severity="error">
                Ungültiger Benutzername oder Passwort
              </Alert>
            </Box>
          )}
          <Box>
            <Button
              variant="contained"
              size="large"
              fullWidth
              disabled={username === "" && password === ""}
              onClick={handleLogin}
              color="primary"
            >
              Anmelden
            </Button>
          </Box>
        </Stack>
      </Container>
    </Box>
  );
};

export default LoginPage;
