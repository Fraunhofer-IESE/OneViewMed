package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api;

import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetType;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;

public interface TableDatasetResolver
    extends DatasetResolver {

  @Override
  default boolean supports(final DatasetType type) {
    return type == DatasetType.TABLE;
  }

  TableDataset resolveTableDataset(String patientId, DatasetConfiguration configuration);

}
