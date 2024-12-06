package de.fhg.iese.oneviewmed.backend.data.entity;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Builder
@Getter
public class StaticPatientOverview
    implements PatientOverview {

  @Nullable
  private final String diagnosis;

  @Nullable
  private final String surgery;

  private final List<String> importantEvents;

  private final String infection;

  private final boolean complication;

  private final boolean allergies;

  private final Long postSurgeryDays;

  private final List<StationStay> stationStays;

}
