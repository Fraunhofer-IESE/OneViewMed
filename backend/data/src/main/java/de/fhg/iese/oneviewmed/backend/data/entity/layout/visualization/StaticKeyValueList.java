package de.fhg.iese.oneviewmed.backend.data.entity.layout.visualization;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StaticKeyValueList implements KeyValueList {
  private final List<KeyValueEntry> entries;
}
