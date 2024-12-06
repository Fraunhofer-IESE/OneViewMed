package de.fhg.iese.oneviewmed.backend.rest.dto.dataset;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "ValueType",
    description = "Information about the type of a value",
    enumAsRef = true
)
public enum ValueTypeDto {
  STRING,
  INTEGER,
  DOUBLE,
  BOOLEAN,
  INSTANT,
  DURATION,
  COLLECTION,
}
