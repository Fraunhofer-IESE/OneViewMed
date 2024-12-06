package de.fhg.iese.oneviewmed.backend.data.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Builder
@Getter
public class StaticStationStay
    implements StationStay {

  private final String name;

  @Nullable
  private final Long days;

}
