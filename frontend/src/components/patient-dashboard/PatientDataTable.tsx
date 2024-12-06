import { Visualization, VisualizationType } from "@one-view-med/client-api";
import { NoDataMessage, QueryResult } from "components/helper";
import { FC, Fragment, useMemo } from "react";
import { useGetTable } from "../../hooks/datasets";
import MedicationDataTableVisualization, {
  MedicationDataTableVisualizationProps,
} from "./medication-details/MedicationTable";
import PatientDataChronologyView, {
  PatientDataChronologyViewProps,
} from "./patient-data-chronology/PatientDataChronologyVisualization";
import PatientDataMedChronologyView from "./PatientDataMedChronologyVisualization";
import PatientDataTableVisualization from "./PatientDataTableVisualization";

type PatientDataTableProps = {
  patientId: string;
  dataset: string;
  configuration?: Record<string, string>;
  visualization: Visualization;
};

const PatientDataTable: FC<PatientDataTableProps> = (props) => {
  const { patientId, dataset, configuration, visualization } = props;
  const { type } = visualization;
  const queryResult = useGetTable(patientId, dataset, configuration);
  const { data } = queryResult;

  const isValidData = useMemo(() => {
    return data && data.values && data.values.length > 0;
  }, [data]);

  const isMedChronologyView = useMemo(() => {
    return type === VisualizationType.MedicationChronology;
  }, [type]);

  const isChronologyView = useMemo(() => {
    return type === VisualizationType.Chronology;
  }, [type]);

  const isMedData = useMemo(() => {
    return (
      dataset === "medication-active" || dataset === "medication-statement"
    );
  }, [dataset]);

  return (
    <QueryResult result={queryResult}>
      {data && isValidData ? (
        <Fragment>
          {isMedChronologyView ? (
            <PatientDataMedChronologyView dataset={dataset} data={data} />
          ) : isChronologyView ? (
            <PatientDataChronologyView
              dataset={dataset}
              data={data as unknown as PatientDataChronologyViewProps["data"]}
            />
          ) : isMedData ? (
            <MedicationDataTableVisualization
              dataset={dataset}
              data={
                data as unknown as MedicationDataTableVisualizationProps["data"]
              }
              visualization={visualization}
            />
          ) : (
            <PatientDataTableVisualization
              dataset={dataset}
              data={data}
              visualization={visualization}
            />
          )}
        </Fragment>
      ) : (
        <NoDataMessage />
      )}
    </QueryResult>
  );
};

export default PatientDataTable;
