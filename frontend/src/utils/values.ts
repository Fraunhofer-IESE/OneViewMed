export const isEmptyNullOrUndefined = (
  value: number | string | Date | undefined | null,
) => {
  if (value === 0) {
    return false;
  } else if (value) {
    return false;
  }
  return true;
};
