package de.fhg.iese.oneviewmed.backend.rest.dto.layout.visualization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.lang.Nullable;

@Schema(
    name = "TableColumn"
)
@Data
public class TableColumnDto {

  @Schema(
      description = "Name of the column in the dataset",
      example = "code"
  )
  @NotNull
  private final String name;

  @Schema(
      description = "Name of the link column in the dataset",
      example = "url"
  )
  @Nullable
  private final String linkName;

  @Schema(
      description = "Title of the column",
      example = "Code"
  )
  @Nullable
  private final String title;

}
