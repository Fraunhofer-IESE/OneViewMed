package de.fhg.iese.oneviewmed.backend.data.entity.layout.visualization;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StaticVisualization implements Visualization {

  private final VisualizationType type;

  private final Table table;

  private final KeyValueList keyValueList;
}
