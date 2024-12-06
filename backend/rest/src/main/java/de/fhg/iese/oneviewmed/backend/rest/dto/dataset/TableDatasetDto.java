package de.fhg.iese.oneviewmed.backend.rest.dto.dataset;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Schema(
    name = "TableDataset",
    description = "A data table"
)
@Data
public class TableDatasetDto {

  @Schema(
      description = "Information about the columns existing in the table dataset"
  )
  @NotNull
  private Map<String, TableDatasetColumnDto> columns;

  @Schema(
      description = "A list of rows with the values of the columns"
  )
  @NotNull
  private List<Map<String, Object>> values;

}
