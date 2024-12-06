package de.fhg.iese.oneviewmed.backend.data.entity.layout.visualization;

import org.springframework.lang.Nullable;

public interface TableColumn {

  String getName();

  @Nullable
  String getLinkName();

  @Nullable
  String getTitle();

}
