package de.fhg.iese.oneviewmed.backend.data.service.dataset;

import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetType;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueDataset;
import de.fhg.iese.oneviewmed.backend.data.service.common.NotFoundException;
import java.util.Map;
import java.util.Set;

public interface DatasetsService {

  Map<String, Set<DatasetType>> getDatasets();

  ValueDataset getValueDataset(String name, String patientId,
                               DatasetConfiguration configuration)
      throws NotFoundException;

  TableDataset getTableDataset(String name, String patientId,
                               DatasetConfiguration configuration)
      throws NotFoundException;

  Map<String, Number> getTimeSeriesDataset(String name, String patientId,
                                           DatasetConfiguration configuration)
      throws NotFoundException;

}
