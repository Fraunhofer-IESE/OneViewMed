import { Box, Typography, useTheme } from "@mui/material";
import { DataTile } from "components/patient-tile";
import { FC, useMemo } from "react";
import { formatDate } from "utils";
import { MedicationValue } from "./MedicationTable";

const MedicationModalContent: FC<MedicationValue> = (medication) => {
  const theme = useTheme();
  const {
    medicationName,
    medicationBrandName,
    medicationForm,
    dosageInstructionText,
    dosageNote,
    dispenseStart,
    dispenseEnd,
    effectiveDate,
    source,
    authoredOn,
  } = medication;

  const treatmentPeriod = useMemo(() => {
    if (dispenseStart && dispenseEnd) {
      return `${formatDate(dispenseStart)} bis ${formatDate(dispenseEnd)}`;
    }
    if (dispenseStart) {
      return `${formatDate(dispenseStart)} bis laufend`;
    }
    if (effectiveDate) {
      return `${formatDate(effectiveDate)} bis laufend`;
    }
    return "-";
  }, [dispenseEnd, dispenseStart, effectiveDate]);

  return (
    <Box height="100%">
      <Box marginBottom={theme.spacing(2)}>
        <Typography fontWeight="bold" fontSize="1rem" textAlign="center">
          {medicationName}
        </Typography>
      </Box>
      <Box display="flex" paddingBottom={theme.spacing(3)} height="100%">
        <Box
          borderRight={`1px solid ${theme.palette.divider}`}
          padding={theme.spacing(2)}
          display="flex"
          flexDirection="column"
          gap={theme.spacing(2)}
          width="100%"
        >
          <DataTile title="Hersteller-Bezeichnung" data={medicationBrandName} />
          <DataTile title="Darreichungsform" data={medicationForm} />
        </Box>
        <Box
          padding={theme.spacing(2)}
          justifyContent="space-between"
          display="flex"
          flexDirection="column"
          width="100%"
          height="100%"
        >
          <Box display="flex" flexDirection="column" gap={theme.spacing(2)}>
            <DataTile title="Dosierung" data={dosageInstructionText} />
            <DataTile title="Behandlungszeitraum" data={treatmentPeriod} />
            <DataTile title="Anmerkungen" data={dosageNote} />
          </Box>
          <Typography
            color={theme.palette.text.secondary}
            fontSize="0.8rem"
            textAlign="right"
          >
            Medikationseintrag aus {source}
            {authoredOn &&
              `, zuletzt geändert am
            ${formatDate(authoredOn)}`}
          </Typography>
        </Box>
      </Box>
    </Box>
  );
};

export default MedicationModalContent;
