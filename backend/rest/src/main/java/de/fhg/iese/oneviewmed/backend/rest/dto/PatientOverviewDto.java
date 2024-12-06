package de.fhg.iese.oneviewmed.backend.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

@Schema(
    name = "PatientOverview",
    description = "Overview of a patient's most important medical information"
)
@Builder
@Data
public class PatientOverviewDto {

  @Schema(
      description = "The main diagnosis",
      example = "Thoraxtrauma"
  )
  @Nullable
  private final String diagnosis;

  @Schema(
      description = "The main surgery",
      example = "Thorakoskpie"
  )
  @Nullable
  private final String surgery;

  @Schema(
      description = "The last most important events",
      example = "[\"Thorakoskopie\", \"Extubation\"]"
  )
  private final List<String> importantEvents;

  @Schema(
      description = "The current infection",
      example = "SARS-CoV"
  )
  @Nullable
  private final String infection;

  @Schema(
      description = "Whether there have been any complications recently",
      example = "true"
  )
  private final boolean complication;

  @Schema(
      description = "Whether the patient has known allergies",
      example = "true"
  )
  @Nullable
  private final Boolean allergies;

  @Schema(
      description = "Days in hospital after the operation",
      example = "true"
  )
  @Nullable
  private final Long postSurgeryDays;

  @Schema(
      description = "The patient's previous stays on the stations during his hospitalization"
  )
  private final List<StationStayDto> stationStays;

}
