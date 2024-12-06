import { Configuration, PatientOverviewApi } from "@one-view-med/client-api";
import { useQuery } from "@tanstack/react-query";
import { useCallback, useMemo } from "react";
import useConfiguration from "./configuration";

const usePatientOverviewApi = () => {
  const { backendUrl } = useConfiguration();

  const apiInstance = useMemo(() => {
    const config = new Configuration({ basePath: backendUrl });
    return new PatientOverviewApi(config);
  }, [backendUrl]);

  const getPatientOverview = useCallback(
    (patientId: string) =>
      apiInstance.getPatientOverview({ xPatientId: patientId }),
    [apiInstance],
  );

  return { getPatientOverview };
};

export const PATIENT_OVERVIEW_QUERY_KEY = "patient-overview";

export const usePatientOverview = (patientId: string) => {
  const { isLoaded } = useConfiguration();
  const { getPatientOverview } = usePatientOverviewApi();

  const queryFn = useCallback(() => {
    return getPatientOverview(patientId);
  }, [getPatientOverview, patientId]);

  return useQuery({
    queryKey: [PATIENT_OVERVIEW_QUERY_KEY, patientId],
    queryFn,
    enabled: isLoaded,
  });
};
