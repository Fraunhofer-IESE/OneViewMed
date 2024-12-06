package de.fhg.iese.oneviewmed.backend.rest.dto.layout.visualization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.lang.Nullable;

@Schema(
    name = "Visualization",
    description = """
        A visualization describes the display of data from a dataset.
        """
)
@Data
public class VisualizationDto {

  @Deprecated(forRemoval = true)
  @Schema(
      description = "Type of the visualization with which the data is to be displayed",
      example = "LINE_CHART"
  )
  @NotEmpty
  private final VisualizationTypeDto type;

  @Schema(
      description = "Settings for configuring the display of the `TABLE` visualization"
  )
  @Nullable
  private final TableDto table;

  @Schema(
      description = "Settings for configuring the display of the `KEY_VALUE_LIST` visualization"
  )
  @Nullable
  private final KeyValueListDto keyValueList;

}
