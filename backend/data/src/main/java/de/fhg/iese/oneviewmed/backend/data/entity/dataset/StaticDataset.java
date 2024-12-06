package de.fhg.iese.oneviewmed.backend.data.entity.dataset;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StaticDataset
    implements Dataset {

  private final DatasetType type;

  private final String name;

  private final Map<String, Object> configuration;

}
