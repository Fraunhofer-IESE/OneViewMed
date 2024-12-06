package de.fhg.iese.oneviewmed.backend.data.entity;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

@Builder
@Data
public class Patient {

  private String id;

  @Nullable
  private String caseNumber;

  private String givenName;

  private String familyName;

  @Nullable
  private Instant birthDate;

  private Gender gender;

  public String getFullName() {
    return getFamilyName() + ", " + getGivenName();
  }

}
