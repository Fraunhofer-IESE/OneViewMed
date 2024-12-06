package de.fhg.iese.oneviewmed.backend.rest.dto.layout;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lombok.Data;

@Schema(
    name = "Group",
    description = """
        A group describes an area that contains tiles within the dashboard.

        How groups are displayed is up to the front end.
        They can be displayed as different sections but also as tabs.
        """
)
@Data
public class GroupDto {

  @Schema(
      description = "Title of the group",
      example = "Übersicht"
  )
  @NotNull
  private String title;

  @Schema(
      description = """
          Tiles within the group.

          (a.k.a. Panels, Widgets, Components, Objects, ...)
          """
  )
  @NotNull
  private Collection<TileDto> tiles;

}
