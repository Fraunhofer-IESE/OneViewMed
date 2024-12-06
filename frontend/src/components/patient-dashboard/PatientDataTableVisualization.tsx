import {
  Link,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Typography,
  useTheme,
} from "@mui/material";
import {
  TableDataset,
  TableDatasetColumn,
  ValueType,
  Visualization,
  VisualizationType,
} from "@one-view-med/client-api";
import { formatDuration } from "date-fns";
import { de } from "date-fns/locale/de";
import { parse } from "iso8601-duration";
import { FC, Fragment, useCallback, useMemo } from "react";
import { FormattedDate, FormattedMessage, FormattedTime } from "react-intl";

type PatientDataTableVisualizationProps = {
  dataset: string;
  data: TableDataset;
  visualization: Visualization;
};

const PatientDataTableVisualization: FC<PatientDataTableVisualizationProps> = (
  props,
) => {
  const { dataset, data, visualization } = props;
  const { keyValueList, type, table } = visualization;
  const theme = useTheme();

  const hasBorderBottom = useMemo(() => {
    return (
      dataset === "documents" ||
      dataset === "medication-statement" ||
      dataset === "medication-active"
    );
  }, [dataset]);

  const isAllgemeinesOrAllergien = useMemo(() => {
    return (
      dataset === "patient-information" || dataset === "allergy-intolerance"
    );
  }, [dataset]);

  const borderBottomStyle = {
    ":nth-last-of-type(n+2)": {
      borderBottom: `1px solid ${theme.palette.divider}`,
    },
  };

  const keyNames = useMemo(() => {
    return keyValueList?.entries?.reduce<Record<string, string>>(
      (prevKeyNames, entry) => {
        const { key, keyName } = entry;
        if (!key) {
          return prevKeyNames;
        }
        const name = keyName ?? key;
        return {
          ...prevKeyNames,
          [key]: name,
        };
      },
      {},
    );
  }, [keyValueList?.entries]);

  const formatKey = useCallback(
    (key: string) => {
      if (!keyNames || Object.keys(keyNames).length === 0) {
        return key;
      }
      return keyNames[key];
    },
    [keyNames],
  );

  const formatValue = useCallback(
    (value: object, column?: TableDatasetColumn) => {
      if (value) {
        const stringValue = value?.toString().replace(/,/g, ", ");
        const type = column?.type;
        if (type === ValueType.Boolean) {
          const booleanValue = Boolean(value);
          if (booleanValue) {
            return <FormattedMessage id="values.true" />;
          }
          return <FormattedMessage id="values.false" />;
        }
        if (type === ValueType.Instant) {
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
        return stringValue;
      }
      return "-";
    },
    [],
  );

  const isKeyValueList = useMemo(() => {
    return type === VisualizationType.KeyValueList;
  }, [type]);

  const isHeaderVisible = useMemo(() => {
    return isKeyValueList ? false : table?.headerVisible;
  }, [isKeyValueList, table?.headerVisible]);

  return (
    <Table aria-label={`${dataset}-data-table`} size="small">
      {isHeaderVisible ? (
        <TableHead>
          <TableRow>
            {visualization.table?.columns?.map(({ title }) => (
              <TableCell
                key={title}
                sx={{
                  padding: theme.spacing(1),
                  maxWidth: "400px",
                }}
              >
                <Typography
                  variant="overline"
                  textTransform="capitalize"
                  lineHeight={1}
                  color="text.secondary"
                >
                  {title}
                </Typography>
              </TableCell>
            ))}
          </TableRow>
        </TableHead>
      ) : null}
      <TableBody>
        {isKeyValueList
          ? data.values.map((row) => {
              return Object.entries(row).map(([key, value]) => (
                <TableRow
                  key={key}
                  sx={hasBorderBottom ? { ...borderBottomStyle } : null}
                >
                  <TableCell
                    key={key}
                    sx={{
                      ":not(:last-of-type) > *": {
                        borderRight: hasBorderBottom
                          ? ""
                          : `1px solid ${theme.palette.divider}`,
                      },
                      borderBottom: "unset",
                    }}
                  >
                    <Typography
                      fontWeight={hasBorderBottom ? "bold" : "regular"}
                      paddingRight={theme.spacing(1)}
                      color={
                        isAllgemeinesOrAllergien
                          ? theme.palette.text.secondary
                          : theme.palette.text.primary
                      }
                      fontSize={isAllgemeinesOrAllergien ? "0.8rem" : "0.9rem"}
                    >
                      {formatKey(key)}
                    </Typography>
                  </TableCell>
                  <TableCell key={`${key}-value`}>
                    <Typography>
                      {formatValue(value, data.columns[key])}
                    </Typography>
                  </TableCell>
                </TableRow>
              ));
            })
          : data.values.map((row, index) => {
              return (
                <TableRow
                  key={index}
                  sx={hasBorderBottom ? { ...borderBottomStyle } : null}
                >
                  {table?.columns?.map(({ name, linkName }) => {
                    return (
                      name && (
                        <TableCell
                          sx={{
                            padding: theme.spacing(1),
                            width:
                              name === "code" || name === "name"
                                ? "650px"
                                : "150px",
                            borderBottom: "unset",
                            ":not(:last-of-type) > *": {
                              borderRight: hasBorderBottom
                                ? ""
                                : `1px solid ${theme.palette.divider}`,
                            },
                          }}
                          key={`${index}-${row[name]}`}
                        >
                          {linkName && row[linkName] ? (
                            <Link
                              component="a"
                              href={row[linkName].toString()}
                              target="_blank"
                              underline="hover"
                              variant="body1"
                              display="block"
                              width="100%"
                              fontWeight={
                                hasBorderBottom || row.isMain
                                  ? "bold"
                                  : "regular"
                              }
                              fontSize="0.9rem"
                            >
                              {formatValue(row[name], data.columns[name])}
                            </Link>
                          ) : (
                            <Typography
                              paddingRight={theme.spacing(1)}
                              fontWeight={row.isMain ? "bold" : "regular"}
                            >
                              {formatValue(row[name], data.columns[name])}
                            </Typography>
                          )}
                        </TableCell>
                      )
                    );
                  })}
                </TableRow>
              );
            })}
      </TableBody>
    </Table>
  );
};

export default PatientDataTableVisualization;
