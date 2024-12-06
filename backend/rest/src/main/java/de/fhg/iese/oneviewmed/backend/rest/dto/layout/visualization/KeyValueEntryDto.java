package de.fhg.iese.oneviewmed.backend.rest.dto.layout.visualization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(
    name = "KeyValueEntry"
)
@Data
public class KeyValueEntryDto {

  @Schema(
      description = "The key of for the entry"
  )
  @NotNull
  private final String key;

  @Schema(
      description = "The human readable name of key of for the entry"
  )
  @NotNull
  private final String keyName;

}
