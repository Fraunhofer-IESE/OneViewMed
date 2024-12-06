import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableRow,
  Typography,
  useTheme,
} from "@mui/material";
import { TableDatasetColumn } from "@one-view-med/client-api";
import { compareAsc, max, parseISO } from "date-fns";
import { FC, useMemo } from "react";
import { FormattedDate } from "react-intl";
import ChronologyBox from "./ChronologyBox";

export type ChronologyVisit = {
  type: string;
  title: string;
};

export type ChronologyDocument = {
  type: string;
  title: string;
  url: string;
};

export type ChronologyEvent = {
  type: string;
  title: string;
};

export type ChronologyStation = {
  type: string;
  name: string;
};

export type ChronologyValue = {
  date: string;
  visits: Array<ChronologyVisit>;
  documents: Array<ChronologyDocument>;
  events: Array<ChronologyEvent>;
  station?: Array<ChronologyStation>;
};

export type PatientDataChronologyViewProps = {
  dataset: string;

  data: {
    columns: Record<string, TableDatasetColumn>;
    values: Array<ChronologyValue>;
  };
};

const PatientDataChronologyView: FC<PatientDataChronologyViewProps> = (
  props,
) => {
  const { dataset, data } = props;
  const theme = useTheme();

  const chronologyStyles = {
    tableCell: {
      padding: "unset",
      borderBottom: `1px solid #e1e3f1`,
      textAlign: "center",
    },
  };
  const sortedValues = useMemo(() => {
    const { values } = data;
    return [...values].sort((a, b) => {
      const aDate = new Date(a.date);
      const bDate = new Date(b.date);
      return compareAsc(bDate, aDate);
    });
  }, [data]);

  const stationDateMap = useMemo(() => {
    const result: Record<string, Record<string, number>> = {};

    const addToMap = (
      stationName: string | undefined,
      stationDates: Array<Date>,
    ) => {
      if (stationName && stationDates.length > 0) {
        const maxDate = max(stationDates);
        if (maxDate) {
          const maxDateString = maxDate.toISOString();
          const map = result[stationName] ?? {};
          map[maxDateString] = stationDates.length;
          result[stationName] = map;
        }
      }
    };

    let stationDates: Array<Date> = [];
    let currentStation;
    for (const value of sortedValues) {
      const { station, date } = value;
      if (station) {
        const { name } = station[0];
        const dateValue = parseISO(date);
        if (currentStation !== name) {
          addToMap(currentStation, stationDates);
          currentStation = name;
          stationDates = [dateValue];
        } else {
          stationDates.push(dateValue);
        }
      }
    }
    addToMap(currentStation, stationDates);
    return result;
  }, [sortedValues]);

  return (
    <TableContainer sx={{ overflowY: "auto", maxHeight: "70vh" }}>
      <Table
        aria-label={`${dataset}-data-table`}
        size="small"
        sx={{ tableLayout: "fixed" }}
      >
        <TableBody>
          {sortedValues.map((val) => {
            const { date, events, documents, station, visits } = val;
            const dateString = new Date(date).toISOString();
            const stationName = station && station[0].name;
            const stationType = station && station[0].type;
            const lengthMap = stationName
              ? stationDateMap[stationName]
              : undefined;
            const spanLength = lengthMap ? lengthMap[dateString] : undefined;

            return (
              <TableRow key={val.date.toString()}>
                <TableCell sx={{ ...chronologyStyles.tableCell }} width="80rem">
                  <Typography variant="overline">
                    <FormattedDate dateStyle="medium" value={date} />
                  </Typography>
                </TableCell>
                {station && station[0] ? (
                  // Creates merged cell with rowSpan of station stays if date is the first of the station stays
                  spanLength && (
                    <TableCell
                      sx={{
                        ...chronologyStyles.tableCell,
                        borderInline: `1px solid #e1e3f1`,
                      }}
                      width="90rem"
                      rowSpan={spanLength}
                    >
                      <Typography variant="overline" textTransform="capitalize">
                        {stationName !== "Unbekannt"
                          ? stationName
                          : stationType}
                      </Typography>
                    </TableCell>
                  )
                ) : (
                  // Creates empty cell if no station is given
                  <TableCell
                    sx={{
                      ...chronologyStyles.tableCell,
                      borderInline: `1px solid #e1e3f1`,
                    }}
                    width="90rem"
                  />
                )}
                <TableCell sx={{ ...chronologyStyles.tableCell }} size="medium">
                  {[...events][0] && (
                    <ChronologyBox
                      color={theme.palette.success.main}
                      title={events[0].title}
                      type={events[0].type}
                    />
                  )}
                </TableCell>
                <TableCell sx={{ ...chronologyStyles.tableCell }} size="medium">
                  {documents[0] && (
                    <ChronologyBox
                      color={theme.palette.action.active}
                      title={documents[0].title}
                      type={documents[0].type}
                      url={documents[0].url}
                    />
                  )}
                </TableCell>
                <TableCell sx={{ ...chronologyStyles.tableCell }} size="medium">
                  {visits[0] && (
                    <ChronologyBox
                      color={theme.palette.info.main}
                      title={visits[0].title}
                      type={visits[0].type}
                    />
                  )}
                </TableCell>
              </TableRow>
            );
          })}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default PatientDataChronologyView;
