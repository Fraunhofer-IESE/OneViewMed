package de.fhg.iese.oneviewmed.backend.data.entity.layout;

import de.fhg.iese.oneviewmed.backend.data.entity.dataset.Dataset;
import de.fhg.iese.oneviewmed.backend.data.entity.layout.visualization.Visualization;

public interface Content {

  Dataset getDataset();

  Visualization getVisualization();

}
