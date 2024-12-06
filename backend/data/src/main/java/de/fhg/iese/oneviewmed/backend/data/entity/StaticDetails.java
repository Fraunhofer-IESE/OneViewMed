package de.fhg.iese.oneviewmed.backend.data.entity;

import de.fhg.iese.oneviewmed.backend.data.entity.dataset.Dataset;
import de.fhg.iese.oneviewmed.backend.data.entity.layout.Content;
import de.fhg.iese.oneviewmed.backend.data.entity.layout.visualization.Visualization;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StaticDetails
    implements Content {

  private final Dataset dataset;

  private final Visualization visualization;

}
