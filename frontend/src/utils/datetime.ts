import { differenceInYears, format } from "date-fns";

const GERMAN_DAY_PATTERN = "dd.MM.yyyy";

export const formatDate = (date: Date | string) => {
  return format(date, GERMAN_DAY_PATTERN);
};

const BACKEND_DAY_PATTERN = "yyyy-MM-dd";

export const formatDayForBackend = (date: Date) => {
  return format(date, BACKEND_DAY_PATTERN);
};

export const getAge = (birthday: Date | string) => {
  return differenceInYears(new Date(), new Date(birthday));
};
