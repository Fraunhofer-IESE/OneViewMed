package de.fhg.iese.oneviewmed.backend.data.entity.layout;

import de.fhg.iese.oneviewmed.backend.data.entity.dataset.Dataset;
import de.fhg.iese.oneviewmed.backend.data.entity.layout.visualization.Visualization;
import java.time.Duration;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

@Builder
@Data
public class StaticTile
    implements Tile {

  private final String id;

  private final Set<Orientation> orientations;

  @Nullable
  private final Integer minWidth;

  @Nullable
  private final Integer minHeight;

  private final int significance;

  @Nullable
  private final String title;

  private final Dataset dataset;

  private final Visualization visualization;

  @Nullable
  private final Duration refreshInterval;

  private final Content details;

}
