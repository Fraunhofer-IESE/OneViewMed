package de.fhg.iese.oneviewmed.backend.data.entity.layout;

import java.time.Duration;
import java.util.Set;
import org.springframework.lang.Nullable;

public interface Tile
    extends Content {

  String getId();

  Set<Orientation> getOrientations();

  @Nullable
  Integer getMinWidth();

  @Nullable
  Integer getMinHeight();

  int getSignificance();

  String getTitle();

  @Nullable
  Duration getRefreshInterval();

  @Nullable
  Content getDetails();

}
