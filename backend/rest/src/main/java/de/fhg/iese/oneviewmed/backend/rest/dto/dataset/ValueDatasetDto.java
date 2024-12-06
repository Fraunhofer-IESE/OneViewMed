package de.fhg.iese.oneviewmed.backend.rest.dto.dataset;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(
    name = "ValueDataset",
    description = "A data value"
)
@Data
public class ValueDatasetDto {

  @Schema(
      description = "The text value",
      example = "132 mmHg"
  )
  private @NotNull String text;

  @Schema(
      description = "The numerical value if available",
      example = "132.0"
  )
  private Number number;

  @Schema(
      description = "The type of the value if available",
      example = "STRING"
  )
  private ValueTypeDto type;

}
