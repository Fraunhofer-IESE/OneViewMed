package de.fhg.iese.oneviewmed.backend.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Data;
import org.springframework.lang.Nullable;

@Schema(
    name = "Patient",
    description = "Patient information"
)
@Data
public class PatientDto {

  @Schema(
      description = "Identifier of the patient",
      example = "f6573f91-45d3-451e-9c9a-3045f0e6e7df"
  )
  @NotNull
  private String id;

  @Schema(
      description = "Case number of the patient",
      example = "F_0000002"
  )
  @Nullable
  private String caseNumber;

  @Schema(
      description = "Given name of the patient",
      example = "John"
  )
  @NotNull
  private String givenName;

  @Schema(
      description = "Family name of the patient",
      example = "Doe"
  )
  @NotNull
  private String familyName;

  @Schema(
      description = "Birth date of the patient",
      example = "1970-12-24T00:00:00Z"
  )
  @Nullable
  private Instant birthDate;

  @Schema(
      description = "Gender of the patient",
      example = "MALE"
  )
  @NotNull
  private GenderDto gender;

}
