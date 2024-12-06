package de.fhg.iese.oneviewmed.backend.rest.dto.layout;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "Orientation",
    description = "Orientation of a tile",
    enumAsRef = true
)
public enum OrientationDto {
  HORIZONTAL,
  VERTICAL,
  SQUARE
}
