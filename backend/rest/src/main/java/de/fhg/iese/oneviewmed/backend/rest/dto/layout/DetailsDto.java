package de.fhg.iese.oneviewmed.backend.rest.dto.layout;

import de.fhg.iese.oneviewmed.backend.rest.dto.layout.visualization.VisualizationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(
    name = "Details",
    description = "A full screen view that shows detail data"
)
@Data
public class DetailsDto {

  @Schema(
      description = "Information on the source of the data"
  )
  @NotNull
  private DatasetDto dataset;

  @Schema(
      description = "Information on the visualization of the data"
  )
  @NotNull
  private VisualizationDto visualization;

}
