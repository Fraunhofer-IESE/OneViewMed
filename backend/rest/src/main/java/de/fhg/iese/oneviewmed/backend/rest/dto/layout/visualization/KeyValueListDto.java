package de.fhg.iese.oneviewmed.backend.rest.dto.layout.visualization;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import org.springframework.lang.NonNull;

@Schema(
    name = "KeyValueList"
)
@Data
public class KeyValueListDto {

  @Schema(
      description = "List of entries in the key value list"
  )
  @NonNull
  private final List<KeyValueEntryDto> entries;

}
