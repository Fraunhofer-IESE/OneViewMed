package de.fhg.iese.oneviewmed.backend.rest.dto.layout;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;
import lombok.Data;
import org.springframework.lang.Nullable;

@Schema(
    name = "Dataset",
    description = """
        A dataset describes a set of data values that can be visualized.

        Datasets with the same name can exist in different types.
        For example, the dataset with the name "blood-pressure" can exist as a time series, table and (last) value.
        The diagnosis, however, can only exist as a single (text) value.
        """
)
@Data
public class DatasetDto {

  @Schema(
      description = """
          Type of the dataset.

          The type describes the structure of the data in the data set.

          Depending on the type, different endpoints must be used to retrieve the data.
          See `/patients/{patientId}/datasets/{type}/{name}`.

          Note: Not every dataset type is suitable for every visualization type.
          """,
      example = "TIME_SERIES"
  )
  @NotEmpty
  private final DatasetTypeDto type;

  @Schema(
      description = "Name of the dataset",
      example = "blood-pressure"
  )
  @NotEmpty
  private final String name;

  /*TODO:
      Alternativ, konkrete Instanzen des Datasets für jedes Format definieren anstatt eines generischen Dataset
      (ValueDataset | TableDataset | TimeSeriesDataset | ...)
   */
  @Schema(
      description = """
          Properties for configuring the dataset.

          The properties are used to customize and filter the named dataset.
          These are specified by the backend and must be passed as query parameters by the frontend
          when retrieving the dataset via the endpoint.

          The specific properties that are available depend on the type of the dataset.
          """
  )
  @Nullable
  private final Map<String, Object> configuration;

}
