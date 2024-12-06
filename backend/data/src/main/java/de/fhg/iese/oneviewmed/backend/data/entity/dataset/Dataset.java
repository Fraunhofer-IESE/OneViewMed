package de.fhg.iese.oneviewmed.backend.data.entity.dataset;

import java.util.Map;
import org.springframework.lang.Nullable;

public interface Dataset {

  DatasetType getType();

  String getName();

  @Nullable
  Map<String, Object> getConfiguration();

}
