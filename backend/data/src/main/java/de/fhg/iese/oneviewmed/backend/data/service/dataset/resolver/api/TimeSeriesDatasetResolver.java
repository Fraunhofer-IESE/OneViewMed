package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api;

import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetType;
import java.util.Map;

public interface TimeSeriesDatasetResolver
    extends DatasetResolver {

  @Override
  default boolean supports(final DatasetType type) {
    return type == DatasetType.TIME_SERIES;
  }

  Map<String, Number> resolveTimeSeriesDataset(String patientId,
                                               DatasetConfiguration configuration);

}
