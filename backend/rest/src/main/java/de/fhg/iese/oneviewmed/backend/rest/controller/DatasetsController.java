package de.fhg.iese.oneviewmed.backend.rest.controller;

import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticDatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.service.common.NotFoundException;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.DatasetsService;
import de.fhg.iese.oneviewmed.backend.rest.dto.dataset.TableDatasetDto;
import de.fhg.iese.oneviewmed.backend.rest.dto.dataset.ValueDatasetDto;
import de.fhg.iese.oneviewmed.backend.rest.dto.layout.DatasetTypeDto;
import de.fhg.iese.oneviewmed.backend.rest.mapper.DatasetMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "datasets",
    description = "Endpoints to retrieve the data of a dataset"
)
@RequestMapping("datasets")
@RestController
@RequiredArgsConstructor
public class DatasetsController {

  private final DatasetsService datasetsService;

  private final DatasetMapper datasetMapper;

  @Operation(
      summary = "Get datasets",
      description = "Returns all available dataset names with types"
  )
  @GetMapping
  public Map<String, Set<DatasetTypeDto>> getDatasets() {
    return datasetMapper.toDto(datasetsService.getDatasets());
  }

  @Operation(
      summary = "Get value",
      description = "Returns data as a single value for the patient"
  )
  @GetMapping("value/{name}")
  public ValueDatasetDto getValue(
      @Parameter @PathVariable("name") final String name,
      @Parameter @RequestParam @Nullable final Map<String, String> configuration,
      @Parameter @RequestHeader(HeaderNames.PATIENT_ID) final String patientId)
      throws NotFoundException {
    return datasetMapper.toDto(datasetsService.getValueDataset(name, patientId,
        new StaticDatasetConfiguration(configuration)));
  }

  @Operation(
      summary = "Get table",
      description = "Returns data as a table for the patient"
  )
  @GetMapping("table/{name}")
  public TableDatasetDto getTable(
      @Parameter @PathVariable("name") final String name,
      @Parameter @RequestParam @Nullable final Map<String, String> configuration,
      @Parameter @RequestHeader(HeaderNames.PATIENT_ID) final String patientId) throws NotFoundException {
    return datasetMapper.toDto(datasetsService.getTableDataset(name, patientId,
        new StaticDatasetConfiguration(configuration)));
  }

  @Operation(
      summary = "Get times series",
      description = "Returns data as a time series for the patient"
  )
  @GetMapping("time-series/{name}")
  public Map<String, Number> getTimeSeries(
      @Parameter @PathVariable("name") final String name,
      @Parameter @RequestParam @Nullable final Map<String, String> configuration,
      @Parameter @RequestHeader(HeaderNames.PATIENT_ID) final String patientId)
      throws NotFoundException {
    return datasetsService.getTimeSeriesDataset(name, patientId,
        new StaticDatasetConfiguration(configuration));
  }

}
