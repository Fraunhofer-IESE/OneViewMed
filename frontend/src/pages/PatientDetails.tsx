import { Box, Container } from "@mui/material";
import { PatientOverviewHeader } from "components/patient-dashboard";
import PatientDashboardContent from "components/patient-dashboard/PatientDashBoardContent";
import { FC } from "react";
import { useParams } from "react-router-dom";
import theme from "styles/theme";

const PatientDetails: FC = () => {
  const { patientId } = useParams();

  return (
    <Box minHeight="100vh" justifyContent="center" padding={theme.spacing(2)}>
      <Container sx={{ height: "100%" }}>
        {patientId && <PatientOverviewHeader patientId={patientId} />}
        {patientId && <PatientDashboardContent patientId={patientId} />}
      </Container>
    </Box>
  );
};

export default PatientDetails;
