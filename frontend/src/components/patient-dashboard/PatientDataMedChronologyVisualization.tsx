import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";
import KeyboardArrowUpIcon from "@mui/icons-material/KeyboardArrowUp";
import {
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  useTheme,
} from "@mui/material";
import { TableDataset } from "@one-view-med/client-api";
import { compareAsc, eachDayOfInterval, isAfter } from "date-fns";
import React, { FC, Fragment, useCallback, useMemo, useState } from "react";
import { FormattedDate } from "react-intl";
import theme from "styles/theme";
import { formatDayForBackend } from "utils";

type PatientDataChronicalViewProps = {
  dataset: string;
  data: TableDataset;
};

type SubstanceGroupRowProps = {
  group: object | null;
  sortedValues: Array<{ [key: string]: object }>;
  firstDate: Date;
  lastDate: Date;
};

const stickyCSS = {
  position: "sticky",
  left: 0,
  background: theme.palette.background.paper,
};

const SubstanceGroupRow: FC<SubstanceGroupRowProps> = ({
  group,
  sortedValues,
  firstDate,
  lastDate,
}) => {
  const theme = useTheme();
  const [open, setOpen] = useState<boolean>(true);

  const handleArrowClick = useCallback(
    (event: React.MouseEvent<HTMLElement>) => {
      event.preventDefault();
      event.stopPropagation();
      setOpen((prevValue) => !prevValue);
    },
    [],
  );

  const substanceGroupValues = useMemo(() => {
    return sortedValues.filter(
      ({ substanceGroup }) => substanceGroup === group,
    );
  }, [group, sortedValues]);

  return (
    <Fragment key={"" + group}>
      <TableRow
        key={"" + group}
        sx={{
          borderTop: `1px solid ${theme.palette.divider} !important`,
          boxShadow: `inset 20px 1px 0px 0px ${theme.palette.divider}`,
        }}
      >
        <TableCell
          key="medication"
          sx={{
            ...stickyCSS,
            zIndex: 1,
            borderBottom: "unset",
            borderRight: `1px solid ${theme.palette.divider}`,
            borderTop: `1px solid ${theme.palette.divider}`,
            display: "flex",
          }}
        >
          <Typography fontWeight="bold" marginRight="auto">
            {group ? "" + group : "Unbekannt"}
          </Typography>
          <IconButton
            aria-label="expand row"
            sx={{ padding: "unset" }}
            size="small"
            color="secondary"
            onClick={handleArrowClick}
          >
            {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
          </IconButton>
        </TableCell>
      </TableRow>
      {substanceGroupValues.map((value, index) => {
        const {
          medicationName,
          medicationBrandName,
          dispenseStart,
          dispenseEnd,
          substanceGroupColor,
        } = value;
        const start = new Date(dispenseStart.toString());
        const end = new Date(dispenseEnd.toString());
        const emptyStartInterval = firstDate
          ? eachDayOfInterval({
              start: firstDate,
              end: start,
            })
          : [];
        const emptyEndInterval = lastDate
          ? eachDayOfInterval({
              start: end,
              end: lastDate,
            })
          : [];
        // remove the last date, because it is the start date
        emptyStartInterval.pop();
        emptyEndInterval.pop();
        return (
          <TableRow
            key={index}
            style={{
              display: open ? "" : "none",
            }}
          >
            <TableCell
              key="medication"
              sx={{
                ...stickyCSS,
                zIndex: 1,
                marginRight: theme.spacing(1),
                borderBottom: "unset",
                borderRight: `1px solid ${theme.palette.divider}`,
              }}
            >
              <Typography
                color="textPrimary"
                sx={{
                  lineHeight: 1,
                  paddingRight: theme.spacing(1),
                }}
              >
                {medicationName
                  ? "" + medicationName
                  : medicationBrandName
                    ? "" + medicationBrandName
                    : "Unbekannt"}
              </Typography>
            </TableCell>
            {emptyStartInterval.map((day) => (
              <TableCell key={day.getTime()}></TableCell>
            ))}
            {eachDayOfInterval({ start, end }).map((day) => (
              <TableCell
                key={day.getTime()}
                className="day-cell"
                sx={{
                  paddingInline: "unset",
                  paddingRight: "1px",
                  borderBottom: "unset",
                  ":nth-child(1 of td.day-cell)": {
                    paddingLeft: theme.spacing(1),
                  },
                  ":nth-child(1 of td.day-cell) > *": {
                    borderRadius: "40px 0 0 40px",
                  },
                  ":nth-last-child(1 of td.day-cell) > *": {
                    borderRadius: "0 40px 40px 0",
                  },
                }}
              >
                <Typography
                  sx={{
                    backgroundColor:
                      substanceGroupColor || theme.palette.grey["400"],
                    display: "table",
                    width: "100%",
                    color: substanceGroupColor || theme.palette.grey["400"],
                    lineHeight: 2,
                  }}
                >
                  X
                </Typography>
              </TableCell>
            ))}
            {emptyEndInterval.map((day) => (
              <TableCell key={day.getTime()}></TableCell>
            ))}
          </TableRow>
        );
      })}
    </Fragment>
  );
};

const PatientDataChronicalView: FC<PatientDataChronicalViewProps> = (props) => {
  const { dataset, data } = props;

  const medications = useMemo(() => {
    const { values } = data;
    if (values.length === 0) {
      return [];
    }
    return values[0].medications as Array<Record<string, object>>;
  }, [data]);

  const stations = useMemo(() => {
    const { values } = data;
    if (values.length === 0) {
      return [];
    }
    return values[0].stations as Array<Record<string, string>>;
  }, [data]);

  const groupSet = useMemo(() => {
    return new Set(
      medications.map(({ substanceGroup }) => substanceGroup).sort(),
    );
  }, [medications]);

  const sortedValues = useMemo(() => {
    return [...medications]
      .filter((value) => {
        const { dispenseStart, dispenseEnd } = value;
        return dispenseStart && dispenseEnd;
      })
      .sort((a, b) => {
        const aDispenseStart = new Date(a.dispenseStart.toString());
        const bDispenseStart = new Date(b.dispenseStart.toString());
        return compareAsc(aDispenseStart, bDispenseStart);
      });
  }, [medications]);

  const firstDate = useMemo(() => {
    const firstValue = sortedValues[0];
    if (!firstValue) {
      return null;
    }
    return new Date(firstValue.dispenseStart.toString());
  }, [sortedValues]);

  const lastDate = useMemo(() => {
    let result: string | undefined;
    sortedValues.forEach((value) => {
      const dispenseEnd = value.dispenseEnd.toString();
      if (!result || isAfter(dispenseEnd, result)) {
        result = dispenseEnd;
      }
    });
    if (!result) {
      return null;
    }
    return new Date(result);
  }, [sortedValues]);

  const allDays = useMemo(() => {
    if (!firstDate || !lastDate) {
      return [];
    }
    return eachDayOfInterval({ start: firstDate, end: lastDate });
  }, [firstDate, lastDate]);

  return (
    <TableContainer sx={{ width: "85vw" }}>
      <Table
        aria-label={`${dataset}-data-table`}
        size="small"
        sx={{ borderCollapse: "separate" }}
      >
        <TableHead>
          <TableRow
            sx={{
              ...stickyCSS,
              left: "unset",
              top: 0,
              zIndex: 1,
            }}
          >
            <TableCell
              key="medication-header"
              sx={{
                ...stickyCSS,
                zIndex: 2,
                borderBottom: "unset",
                borderRight: `1px solid ${theme.palette.divider}`,
              }}
            >
              <Typography color={theme.palette.background.paper}>O</Typography>
            </TableCell>
            {allDays.map((day) => (
              <TableCell key={day.getTime()}>
                <Typography
                  align="center"
                  color="textSecondary"
                  variant="overline"
                  textTransform="capitalize"
                  sx={{
                    lineHeight: 1,
                    width: "100%",
                    display: "inline-block",
                  }}
                >
                  {stations[0][formatDayForBackend(day)]}
                </Typography>
              </TableCell>
            ))}
          </TableRow>
          <TableRow
            sx={{
              ...stickyCSS,
              left: "unset",
              top: 0,
              zIndex: 1,
              borderBottom: `1px solid ${theme.palette.divider}`,
            }}
          >
            <TableCell
              key="medication-header"
              sx={{
                ...stickyCSS,
                zIndex: 2,
                borderRight: `1px solid ${theme.palette.divider}`,
              }}
            >
              <Typography color={theme.palette.background.paper}>O</Typography>
            </TableCell>
            {allDays.map((day) => (
              <TableCell key={day.getTime()}>
                <Typography
                  align="center"
                  color="textSecondary"
                  variant="overline"
                  textTransform="uppercase"
                  sx={{
                    lineHeight: 1,
                    width: "100%",
                    display: "inline-block",
                  }}
                >
                  <FormattedDate value={day} />
                </Typography>
              </TableCell>
            ))}
          </TableRow>
        </TableHead>
        <TableBody>
          {firstDate &&
            lastDate &&
            [...groupSet].map((group) => (
              <SubstanceGroupRow
                sortedValues={sortedValues}
                firstDate={firstDate}
                lastDate={lastDate}
                group={group}
              />
            ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default PatientDataChronicalView;
