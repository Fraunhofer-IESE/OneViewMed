package de.fhg.iese.oneviewmed.backend.data.entity.layout.visualization;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

@Builder
@Data
public class StaticTableColumn
    implements TableColumn {

  private final String name;

  @Nullable
  private final String linkName;

  @Nullable
  private final String title;

}
