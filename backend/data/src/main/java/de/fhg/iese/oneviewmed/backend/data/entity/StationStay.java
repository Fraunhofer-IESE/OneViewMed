package de.fhg.iese.oneviewmed.backend.data.entity;

import org.springframework.lang.Nullable;

public interface StationStay {

  String getName();

  @Nullable
  Long getDays();

}
