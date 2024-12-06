import { Configuration, PatientsApi } from "@one-view-med/client-api";
import { useQuery } from "@tanstack/react-query";
import { useCallback, useMemo } from "react";
import useConfiguration from "./configuration";

const usePatientsApi = () => {
  const { backendUrl } = useConfiguration();

  const apiInstance = useMemo(
    () => new PatientsApi(new Configuration({ basePath: backendUrl })),
    [backendUrl],
  );

  const listPatients = useCallback(
    () => apiInstance.listPatients(),
    [apiInstance],
  );

  const getPatient = useCallback(
    (patientId: string) => apiInstance.getPatient({ xPatientId: patientId }),
    [apiInstance],
  );

  return { listPatients, getPatient };
};

export const PATIENTS_QUERY_KEY = "patients";

export const usePatients = () => {
  const { isLoaded } = useConfiguration();
  const { listPatients } = usePatientsApi();

  return useQuery({
    queryKey: [PATIENTS_QUERY_KEY],
    queryFn: listPatients,
    enabled: isLoaded,
  });
};

export const PATIENT_QUERY_KEY = "patient";

export const useGetPatient = (patientId: string) => {
  const { isLoaded } = useConfiguration();
  const { getPatient } = usePatientsApi();

  return useQuery({
    queryKey: [PATIENT_QUERY_KEY, patientId],
    queryFn: () => getPatient(patientId),
    enabled: isLoaded,
  });
};
