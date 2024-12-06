package de.fhg.iese.oneviewmed.backend.data.properties;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "fhir")
@Component
@Data
public class FhirProperties {

  private @Valid URI repositoryUrl;

}
