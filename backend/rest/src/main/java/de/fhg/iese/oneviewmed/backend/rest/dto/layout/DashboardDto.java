package de.fhg.iese.oneviewmed.backend.rest.dto.layout;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lombok.Data;

@Schema(
    name = "Dashboard",
    description = "A dashboard"
)
@Data
public class DashboardDto {

  @Schema(
      description = "Title of the dashboard",
      example = "Medical overview for John Doe"
  )
  @NotNull
  private String title;

  @Schema(
      description = "Areas within the dashboard"
  )
  @NotNull
  private Collection<GroupDto> groups;

}
