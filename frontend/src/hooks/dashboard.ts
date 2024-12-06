import { Configuration, DashboardApi } from "@one-view-med/client-api";
import { useQuery } from "@tanstack/react-query";
import { useCallback, useMemo } from "react";
import useConfiguration from "./configuration";

export const DASHBOARD_QUERY_KEY = "dashboard";

export const useDashboard = (patientId: string) => {
  const { isLoaded, backendUrl } = useConfiguration();

  const dashboardApi = useMemo(() => {
    return new DashboardApi(new Configuration({ basePath: backendUrl }));
  }, [backendUrl]);

  const queryFn = useCallback(async () => {
    return dashboardApi.getDashboard({ xPatientId: patientId });
  }, [dashboardApi, patientId]);

  return useQuery({
    queryKey: [DASHBOARD_QUERY_KEY, patientId],
    queryFn,
    enabled: isLoaded,
  });
};
