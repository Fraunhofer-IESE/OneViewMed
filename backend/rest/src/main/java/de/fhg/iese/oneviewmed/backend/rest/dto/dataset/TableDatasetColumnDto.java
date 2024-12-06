package de.fhg.iese.oneviewmed.backend.rest.dto.dataset;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(
    name = "TableDatasetColumn",
    description = "A data table column"
)
@Data
public class TableDatasetColumnDto {

  @Schema(
      description = "Type of data values in this column"
  )
  @NotNull
  private final ValueTypeDto type;

}
