package de.fhg.iese.oneviewmed.backend.application;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import java.time.Duration;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "REST API for backend service of the OneViewMed dashboard",
        description = """
            # General information

            - All date/time values use the ISO 8601 format (e.g. `2007-08-31T16:47+00:00` for times or `P1D` for durations)
            """,
        version = "0.0.1"
    )
)
@Configuration
public class OpenApiConfiguration {

  static {
    SpringDocUtils.getConfig().replaceWithSchema(Duration.class, new StringSchema()
        .format("duration")
        .description("Time span in ISO 8601 format")
        .example("PT1H"));
  }

}
