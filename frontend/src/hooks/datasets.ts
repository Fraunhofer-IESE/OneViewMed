import {
  Configuration,
  DatasetsApi,
  HTTPQuery,
  querystring,
} from "@one-view-med/client-api";
import { useQuery } from "@tanstack/react-query";
import { useCallback, useMemo } from "react";
import useConfiguration from "./configuration";

const CONFIGURATION_PARAM_NAME = "configuration";

const useDatasetsApi = () => {
  const { backendUrl } = useConfiguration();

  const apiInstance = useMemo(() => {
    const queryParamsStringify = (params: HTTPQuery) => {
      const configuration = params[CONFIGURATION_PARAM_NAME] as Record<
        string,
        string
      >;
      if (configuration) {
        const explodedParams = {
          ...params,
          ...configuration,
        };
        delete explodedParams[CONFIGURATION_PARAM_NAME];
        return querystring(explodedParams);
      }
      return querystring(params);
    };
    const configuration = new Configuration({
      basePath: backendUrl,
      queryParamsStringify,
    });
    return new DatasetsApi(configuration);
  }, [backendUrl]);

  const getTable = useCallback(
    (patientId: string, name: string, _configuration: Record<string, string>) =>
      apiInstance.getTable({ name, _configuration, xPatientId: patientId }),
    [apiInstance],
  );

  const getTimeSeries = useCallback(
    (patientId: string, name: string, _configuration: Record<string, string>) =>
      apiInstance.getTimeSeries({
        name,
        _configuration,
        xPatientId: patientId,
      }),
    [apiInstance],
  );

  const getValue = useCallback(
    (patientId: string, name: string, _configuration: Record<string, string>) =>
      apiInstance.getValue({ name, _configuration, xPatientId: patientId }),
    [apiInstance],
  );

  return { getTable, getTimeSeries, getValue };
};

const GET_TABLE = "dataset-get-table";

export const useGetTable = (
  patientId: string,
  name: string,
  _configuration: Record<string, string> = {},
) => {
  const { isLoaded } = useConfiguration();
  const { getTable } = useDatasetsApi();

  const queryFn = useCallback(
    () => getTable(patientId, name, _configuration),
    [getTable, patientId, name, _configuration],
  );

  return useQuery({
    queryKey: [GET_TABLE, patientId, name, _configuration],
    queryFn,
    enabled: isLoaded,
  });
};

const GET_TIME_SERIES = "dataset-get-time-series";

export const useGetTimeSeries = (
  patientId: string,
  name: string,
  _configuration: Record<string, string> = {},
) => {
  const { isLoaded } = useConfiguration();
  const { getTimeSeries } = useDatasetsApi();

  const queryFn = useCallback(
    () => getTimeSeries(patientId, name, _configuration),
    [getTimeSeries, patientId, name, _configuration],
  );

  return useQuery({
    queryKey: [GET_TIME_SERIES, patientId, name],
    queryFn,
    enabled: isLoaded,
  });
};

const GET_VALUE = "dataset-get-value";

export const useGetValue = (
  patientId: string,
  name: string,
  _configuration: Record<string, string> = {},
) => {
  const { isLoaded } = useConfiguration();
  const { getValue } = useDatasetsApi();

  const queryFn = useCallback(
    () => getValue(patientId, name, _configuration),
    [getValue, patientId, name, _configuration],
  );

  return useQuery({
    queryKey: [GET_VALUE, patientId, name, _configuration],
    queryFn,
    enabled: isLoaded,
  });
};

export const DataSetCacheKeys = {
  GET_TABLE,
  GET_TIME_SERIES,
  GET_VALUE,
};
