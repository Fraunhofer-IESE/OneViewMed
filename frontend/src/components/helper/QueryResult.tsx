import { Alert, AlertTitle, Container, Skeleton } from "@mui/material";
import { UseQueryResult } from "@tanstack/react-query";
import { FunctionComponent, PropsWithChildren } from "react";

interface Props<T = unknown> {
  result: UseQueryResult<T, Error>;
}

const QueryResult: FunctionComponent<PropsWithChildren<Props>> = ({
  children,
  result,
}) => {
  const { error, isError, isLoading } = result;

  if (isLoading) {
    return (
      <Container>
        <Skeleton />
      </Container>
    );
  }
  if (isError) {
    return (
      <Alert severity="error">
        <AlertTitle>Fehler beim Abrufen der Informationen</AlertTitle>
        {error.name}: {error.message}
      </Alert>
    );
  }

  return children;
};

export default QueryResult;
