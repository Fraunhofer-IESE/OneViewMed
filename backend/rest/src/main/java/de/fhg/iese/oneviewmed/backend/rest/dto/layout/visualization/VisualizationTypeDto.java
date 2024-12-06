package de.fhg.iese.oneviewmed.backend.rest.dto.layout.visualization;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "VisualizationType", description = "Type of a visualization", enumAsRef = true)
public enum VisualizationTypeDto {
  VALUE,
  CHANGE,
  KEY_VALUE_LIST,
  MEDICATION_CHRONOLOGY,
  CHRONOLOGY,
  TABLE,
  BAR_CHART,
  LINE_CHART,
}
