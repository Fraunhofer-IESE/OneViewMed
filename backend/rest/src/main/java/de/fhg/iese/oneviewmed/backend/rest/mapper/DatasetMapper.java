package de.fhg.iese.oneviewmed.backend.rest.mapper;

import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetType;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueType;
import de.fhg.iese.oneviewmed.backend.rest.dto.dataset.TableDatasetColumnDto;
import de.fhg.iese.oneviewmed.backend.rest.dto.dataset.TableDatasetDto;
import de.fhg.iese.oneviewmed.backend.rest.dto.dataset.ValueDatasetDto;
import de.fhg.iese.oneviewmed.backend.rest.dto.dataset.ValueTypeDto;
import de.fhg.iese.oneviewmed.backend.rest.dto.layout.DatasetTypeDto;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface DatasetMapper {

  @Mapping(target = "number", source = "valueAsNumber")
  @Mapping(target = "text", source = "valueAsText")
  @Mapping(target = "type", source = "valueType")
  ValueDatasetDto toDto(ValueDataset dataset);

  DatasetTypeDto toDto(DatasetType type);

  Set<DatasetTypeDto> toDto(Set<DatasetType> types);

  Map<String, Set<DatasetTypeDto>> toDto(Map<String, Set<DatasetType>> datasets);

  @Mapping(target = "columns", source = ".")
  @BeanMapping(ignoreUnmappedSourceProperties = "columnNames")
  TableDatasetDto toDto(TableDataset dataset);

  ValueTypeDto toDto(ValueType type);

  default Map<String, TableDatasetColumnDto> toColumnDto(final TableDataset tableDataset) {
    return tableDataset.getColumnNames()
        .stream()
        .collect(Collectors.toMap(Function.identity(), name -> {
          final ValueType type = tableDataset.getColumnType(name);
          return new TableDatasetColumnDto(toDto(type));
        }));
  }

}
