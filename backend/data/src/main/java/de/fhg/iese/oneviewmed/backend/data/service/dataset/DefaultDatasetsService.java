package de.fhg.iese.oneviewmed.backend.data.service.dataset;

import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetType;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueDataset;
import de.fhg.iese.oneviewmed.backend.data.service.common.NotFoundException;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.DatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.TableDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.TimeSeriesDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.ValueDatasetResolver;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultDatasetsService
    implements DatasetsService {

  private final Collection<DatasetResolver> datasetResolvers;

  private final Collection<ValueDatasetResolver> valueDatasetResolvers;

  private final Collection<TableDatasetResolver> tableDatasetResolvers;

  private final Collection<TimeSeriesDatasetResolver> timeSeriesDatasetResolvers;

  @Override
  public Map<String, Set<DatasetType>> getDatasets() {
    return datasetResolvers.stream()
        .map(DatasetResolver::getName)
        .distinct()
        .collect(Collectors.toMap(Function.identity(), this::getTypesForName));
  }

  private Set<DatasetType> getTypesForName(final String name) {
    return Arrays.stream(DatasetType.values())
        .filter(type -> datasetResolvers.stream()
            .anyMatch(resolver -> resolver.supports(type) && resolver.getName().equals(name)))
        .collect(Collectors.toCollection(() -> EnumSet.noneOf(DatasetType.class)));
  }

  private <T extends DatasetResolver> T getResolver(final Collection<T> resolvers,
                                                    final String name, final DatasetType type)
      throws NotFoundException {
    final List<T> resolversWithName = resolvers.stream()
        .filter(resolver -> resolver.getName().equals(name))
        .filter(resolver -> resolver.supports(type))
        .toList();
    if (resolversWithName.isEmpty()) {
      throw new NotFoundException("Dataset resolver " + name + '/' + type + " not found");
    } else {
      if (resolversWithName.size() == 1) {
        return resolversWithName.getFirst();
      } else {
        throw new IllegalStateException(
            "Multiple dataset resolvers found for " + name + '/' + type);
      }
    }
  }

  @Override
  public ValueDataset getValueDataset(final String name, final String patientId,
                                      final DatasetConfiguration configuration)
      throws NotFoundException {
    final ValueDatasetResolver resolver =
        getResolver(valueDatasetResolvers, name, DatasetType.VALUE);
    return resolver.resolveValueDataset(patientId, configuration);
  }

  @Override
  public TableDataset getTableDataset(final String name, final String patientId,
                                      final DatasetConfiguration configuration)
      throws NotFoundException {
    final TableDatasetResolver resolver =
        getResolver(tableDatasetResolvers, name, DatasetType.TABLE);
    return resolver.resolveTableDataset(patientId, configuration);
  }

  @Override
  public Map<String, Number> getTimeSeriesDataset(final String name, final String patientId,
                                                  final DatasetConfiguration configuration)
      throws NotFoundException {
    final TimeSeriesDatasetResolver resolver =
        getResolver(timeSeriesDatasetResolvers, name, DatasetType.TIME_SERIES);
    return resolver.resolveTimeSeriesDataset(patientId, configuration);
  }

}
