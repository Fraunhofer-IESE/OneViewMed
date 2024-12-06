package de.fhg.iese.oneviewmed.backend.data.entity.layout.visualization;

import org.springframework.lang.Nullable;

public interface Visualization {

  VisualizationType getType();

  @Nullable
  Table getTable();

  @Nullable
  KeyValueList getKeyValueList();
}
