import { Gender } from "@one-view-med/client-api";
export const trimString = (
  str: string,
  length: number = 30,
  ending: string = "...",
) => {
  if (str.length > length) {
    return str.substring(0, length - ending.length) + ending;
  }
  return str;
};

export const getGender = (str: string) => {
  switch (str) {
    case Gender.Female:
      return "Weiblich";
    case Gender.Male:
      return "Männlich";
    case Gender.Other:
      return "Diverse";
    case Gender.Unknown:
      return "Unbekannt";
  }
};
