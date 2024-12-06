import { Table, TableBody, TableCell, TableRow } from "@mui/material";
import { FC } from "react";
import { FormattedDate, FormattedTime } from "react-intl";
import { useGetTimeSeries } from "../../hooks/datasets";
import { QueryResult } from "../helper";
import NoDataMessage from "../helper/NoDataMessage";

type PatientDataTimeSeriesProps = {
  patientId: string;
  dataset: string;
  configuration?: Record<string, string>;
};

const PatientDataTimeSeries: FC<PatientDataTimeSeriesProps> = (props) => {
  const { patientId, dataset, configuration } = props;
  const queryResult = useGetTimeSeries(patientId, dataset, configuration);
  const { data } = queryResult;

  return (
    <QueryResult result={queryResult}>
      {data ? (
        <Table aria-label={`dataset data timeseries`}>
          <TableBody>
            {Object.entries(data).map(([time, value]) => (
              <TableRow key={time}>
                <TableCell key="time">
                  <FormattedDate dateStyle="medium" value={time} />{" "}
                  <FormattedTime value={time} />
                </TableCell>
                <TableCell key="value">{value}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      ) : (
        <NoDataMessage />
      )}
    </QueryResult>
  );
};

export default PatientDataTimeSeries;
