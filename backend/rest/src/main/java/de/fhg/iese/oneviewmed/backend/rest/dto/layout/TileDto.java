package de.fhg.iese.oneviewmed.backend.rest.dto.layout;

import de.fhg.iese.oneviewmed.backend.rest.dto.layout.visualization.VisualizationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Set;
import lombok.Data;

@Schema(
    name = "Tile",
    description = """
        A tile describes an area on the dashboard.
        The content of the tile is defined by the `visualization` and `dataset` properties.
        """
)
@Data
public class TileDto {

  @Schema(
      description = "Identifier of the tile",
      example = "20e4363f-be70-4626-817e-c90eda3e33c4"
  )
  @NotNull
  private String id;

  @Schema(
      description = "Allowed orientations for the tile",
      example = "[\"HORIZONTAL\", \"SQUARE\"]"
  )
  @Min(1)
  @NotNull
  private Set<OrientationDto> orientations;

  @Schema(
      description = """
          Minimum number of columns that the tile must at least have in width.

          The size is based on a grid with 12 columns.
          """,
      example = "4"
  )
  @Min(0)
  @Max(12)
  private Integer minWidth;

  @Schema(
      description = """
          Minimum number of rows that the tile must at least have in height

          The size is based on a grid with 12 rows.
          """,
      example = "2"
  )
  @Min(0)
  @Max(12)
  private Integer minHeight;

  @Schema(
      description = """
          How significant the information in this tile is for the user.

          Significant information should always be more visible to the user.

          The higher the value, the more significant the information is.
          The lower the value, the less significant the information is.
          """,
      example = "1",
      defaultValue = "0"
  )
  @NotNull
  private int significance;

  @Schema(
      description = "A title that describes the information displayed in the tile",
      example = "Blood Pressure"
  )
  private String title;

/*
Combinations for dataset type and visualization type:

dataset     | visualization
------------+---------------
VALUE       | VALUE
            | CHANGE
TABLE       | TABLE
TABLE       | BAR_CHART
TIME_SERIES | TABLE
TIME_SERIES | BAR_CHART
TIME_SERIES | LINE_CHART
*/

  @Schema(
      description = "Information on the source of the data"
  )
  @NotNull
  private DatasetDto dataset;

  @Schema(
      description = "Information on the visualization of the data"
  )
  @NotNull
  private VisualizationDto visualization;

  @Schema(
      description = """
          Time interval in ISO 8601 format at which the tile data should be updated.

          If no value is specified, the data is only loaded once initially and never updated.
          """
  )
  private Duration refreshInterval;

  @Schema(
      description = """
          Information on more detailed data.

          If this property is missing, there is no more detailed data and either no details or only
          the normal data should be displayed.
          """
  )
  private DetailsDto details;

}
