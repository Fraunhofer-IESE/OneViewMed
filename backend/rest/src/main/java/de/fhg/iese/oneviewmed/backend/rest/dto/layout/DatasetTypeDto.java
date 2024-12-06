package de.fhg.iese.oneviewmed.backend.rest.dto.layout;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "DatasetType",
    description = """
        Type of a data source

        - `VALUE`: Data as a single value
        - `TABLE`: Data as a table
        - `TIME_SERIES`: Data as a series of dates with values
        """,
    enumAsRef = true
)
public enum DatasetTypeDto {
  VALUE,
  TABLE,
  TIME_SERIES,
}
