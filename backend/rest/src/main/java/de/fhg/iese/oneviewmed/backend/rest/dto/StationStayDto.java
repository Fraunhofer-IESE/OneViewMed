package de.fhg.iese.oneviewmed.backend.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

@Schema(
    name = "StationStay",
    description = "Information about the stay on a station"
)
@Builder
@Data
public class StationStayDto {

  @Schema(
      description = "Name of the station",
      example = "ZNA"
  )
  private String name;

  @Schema(
      description = "Duration of stay in days",
      example = "3"
  )
  @Nullable
  private Integer days;

}
