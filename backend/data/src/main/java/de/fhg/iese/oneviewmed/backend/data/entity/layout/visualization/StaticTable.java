package de.fhg.iese.oneviewmed.backend.data.entity.layout.visualization;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StaticTable
    implements Table {

  private final List<TableColumn> columns;

  private final boolean headerVisible;

}
