import { Typography } from "@mui/material";
import { ValueDataset, ValueType } from "@one-view-med/client-api";
import { formatDuration } from "date-fns";
import { de } from "date-fns/locale";
import { parse } from "iso8601-duration";
import { FC, Fragment, useCallback } from "react";
import { FormattedDate, FormattedMessage, FormattedTime } from "react-intl";
import { useGetValue } from "../../hooks/datasets";
import { QueryResult } from "../helper";
import NoDataMessage from "../helper/NoDataMessage";

type PatientDataValueProps = {
  patientId: string;
  dataset: string;
  configuration?: Record<string, string>;
};

const PatientDataValue: FC<PatientDataValueProps> = (props) => {
  const { patientId, dataset, configuration } = props;
  const queryResult = useGetValue(patientId, dataset, configuration);
  const { data } = queryResult;

  const isValidDataSet = useCallback((data: ValueDataset) => {
    if (!data.text && !data.number) {
      return false;
    }
    if (data.text) {
      return data.text.length > 0;
    }
    return true;
  }, []);

  const formatValue = useCallback(
    (value: string | number, type?: ValueType) => {
      if (type === ValueType.Boolean) {
        return value === "true" ? (
          <FormattedMessage id="value.true" />
        ) : (
          <FormattedMessage id="value.false" />
        );
      }
      if (type === ValueType.Instant) {
        const stringValue = value.toString();
        return (
          <Fragment>
            <FormattedDate dateStyle="medium" value={stringValue} />{" "}
            <FormattedTime value={stringValue} />
          </Fragment>
        );
      }
      if (type === ValueType.Duration) {
        const stringValue = value.toString();
        return formatDuration(parse(stringValue), { locale: de });
      }
      return value;
    },
    [],
  );

  return (
    <QueryResult result={queryResult}>
      {data && isValidDataSet(data) ? (
        <Typography variant="body1">
          {data.number
            ? formatValue(data.number, data.type)
            : formatValue(data.text, data.type)}
        </Typography>
      ) : (
        <NoDataMessage />
      )}
    </QueryResult>
  );
};

export default PatientDataValue;
