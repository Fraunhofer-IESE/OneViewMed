import {
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableRow,
  Typography,
  useTheme,
} from "@mui/material";
import { TableDatasetColumn, Visualization } from "@one-view-med/client-api";
import { InformationIcon } from "assets";
import { BaseModal } from "components/helper";
import { FC, useCallback, useMemo, useState } from "react";
import MedicationModalContent from "./MedicationModalContent";

export type MedicationValue = {
  authoredOn?: Date;
  effectiveDate?: Date;
  dosageInstructionText: string;
  dosageNote: string;
  dispenseStart?: string;
  dispenseEnd?: string;
  medicationBrandName?: string;
  medicationForm?: string;
  medicationName: string;
  source: string;
};

export type MedicationDataTableVisualizationProps = {
  dataset: string;
  data: {
    columns: Record<string, TableDatasetColumn>;
    values: Array<MedicationValue>;
  };
  visualization: Visualization;
};

const MedicationDataTableVisualization: FC<
  MedicationDataTableVisualizationProps
> = (props) => {
  const { dataset, data, visualization } = props;
  const { table } = visualization;
  const theme = useTheme();
  const [modalContent, setModalContent] = useState<MedicationValue | null>(
    null,
  );

  const handleModalClose = useCallback(() => setModalContent(null), []);

  const sortedDataValues = useMemo(() => {
    const { values } = data;
    return values.sort((v1, v2) =>
      v1.medicationName.localeCompare(v2.medicationName),
    );
  }, [data]);

  const borderBottomStyle = {
    ":nth-last-of-type(n+2)": {
      borderBottom: `1px solid ${theme.palette.divider}`,
    },
  };

  return (
    <>
      <Table aria-label={`${dataset}-data-table`} size="small">
        <TableBody>
          {sortedDataValues.map((row: MedicationValue, index) => {
            return (
              <TableRow key={index} sx={{ ...borderBottomStyle }}>
                {table?.columns?.map(({ name }) => {
                  return (
                    name && (
                      <TableCell
                        sx={{
                          padding: theme.spacing(1),
                          borderBottom: "unset",
                          width: "70%",
                        }}
                        key={`${index}-${row[name as keyof MedicationValue]}`}
                      >
                        <Typography>
                          {row[name as keyof MedicationValue] as string}
                        </Typography>
                      </TableCell>
                    )
                  );
                })}
                <TableCell>
                  <IconButton
                    aria-label="expand medication details"
                    sx={{ padding: "unset" }}
                    type="button"
                    onClick={() => setModalContent(row)}
                  >
                    <InformationIcon
                      fill={theme.palette.secondary.main}
                      width={20}
                      height={20}
                    />
                  </IconButton>
                </TableCell>
              </TableRow>
            );
          })}
        </TableBody>
      </Table>
      <BaseModal
        onClose={handleModalClose}
        open={modalContent !== null}
        title={
          dataset === "medication-active"
            ? "Aktuelle Medikation"
            : "Aufnahmemedikation"
        }
      >
        {modalContent && <MedicationModalContent {...modalContent} />}
      </BaseModal>
    </>
  );
};

export default MedicationDataTableVisualization;
