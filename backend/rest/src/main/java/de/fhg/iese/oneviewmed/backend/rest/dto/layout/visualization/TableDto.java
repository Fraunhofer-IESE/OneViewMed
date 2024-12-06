package de.fhg.iese.oneviewmed.backend.rest.dto.layout.visualization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Schema(
    name = "Table"
)
@Data
public class TableDto {

  @Schema(
      description = "List of columns in the table"
  )
  @NotNull
  private final List<TableColumnDto> columns;

  @Schema(
      description = "Whether the header of the columns should be displayed"
  )
  @NotNull
  private final boolean headerVisible;

}
