package de.fhg.iese.oneviewmed.backend.data.entity;

import java.util.List;
import org.springframework.lang.Nullable;

public interface PatientOverview {

  @Nullable
  String getDiagnosis();

  @Nullable
  String getSurgery();

  List<String> getImportantEvents();

  @Nullable
  String getInfection();

  boolean isComplication();

  boolean isAllergies();

  @Nullable
  Long getPostSurgeryDays();

  List<StationStay> getStationStays();

}
