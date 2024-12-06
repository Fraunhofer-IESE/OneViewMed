package de.fhg.iese.oneviewmed.backend.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "Gender",
    description = "Gender of a person",
    enumAsRef = true
)
public enum GenderDto {
  MALE,
  FEMALE,
  OTHER,
  UNKNOWN
}
