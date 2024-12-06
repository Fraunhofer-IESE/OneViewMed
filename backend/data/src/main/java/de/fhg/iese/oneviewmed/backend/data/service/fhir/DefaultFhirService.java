package de.fhg.iese.oneviewmed.backend.data.service.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IRestfulClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.util.BundleUtil;
import de.fhg.iese.oneviewmed.backend.data.properties.FhirProperties;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Element;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.springframework.stereotype.Service;

@Service
public class DefaultFhirService implements FhirService {
  private static final FhirContext CONTEXT = FhirContext.forR4();

  private final URI fhirRepositoryUrl;
  private final IGenericClient client;

  public DefaultFhirService(final FhirProperties fhirProperties) {
    this.fhirRepositoryUrl = fhirProperties.getRepositoryUrl();
    client = CONTEXT.newRestfulGenericClient(fhirProperties.getRepositoryUrl().toString());
    addLogger(client);
  }

  @Override
  public URI getRepositoryUrl() {
    return this.fhirRepositoryUrl;
  }

  private static void addLogger(final IRestfulClient client) {
    final LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
    loggingInterceptor.setLogRequestSummary(true);
    loggingInterceptor.setLogRequestBody(true);
    loggingInterceptor.setLogResponseBody(true);
    client.registerInterceptor(loggingInterceptor);
  }

  @Override
  public IGenericClient createClient() {
    return client;
  }

  @Override
  public <T extends IBaseResource> List<T> toListOfResourcesOfType(final IBaseBundle theBundle,
                                                                   final Class<T> theTypeToInclude) {
    return BundleUtil.toListOfResourcesOfType(CONTEXT, theBundle, theTypeToInclude);
  }

  @Override
  public <T extends IBaseResource> Optional<T> findResourceById(final Class<? extends T> clazz,
                                                                final String id) {
    try {
      return Optional.of(getResourceById(clazz, id));
    } catch (final ResourceNotFoundException e) {
      return Optional.empty();
    }
  }

  @Override
  public <T extends IBaseResource> T getResourceById(final Class<? extends T> clazz,
                                                     final String id) {
    final IGenericClient client = createClient();
    return client.read()
        .resource(clazz)
        .withId(id)
        .execute();
  }

  private static Optional<Boolean> getBoolValue(final Extension extension) {
    final Type value = extension.getValue();
    if (value.isBooleanPrimitive()) {
      final BooleanType booleanType = value.castToBoolean(value);
      return Optional.of(booleanType.booleanValue());
    }
    return Optional.empty();
  }

  @Override
  public Optional<Boolean> getBoolExtensionValue(final DomainResource domainResource,
                                                 final String url) {
    final Extension extension = domainResource.getExtensionByUrl(url);
    if (extension != null) {
      return getBoolValue(extension);
    }
    return Optional.empty();
  }

  @Override
  public Optional<Boolean> getBoolExtensionValue(final Element element, final String url) {
    final Extension extension = element.getExtensionByUrl(url);
    if (extension != null) {
      return getBoolValue(extension);
    }
    return Optional.empty();
  }

  private static Optional<String> getStringValue(final Extension extension) {
    final Type value = extension.getValue();
    if (value.isPrimitive()) {
      final StringType stringType = value.castToString(value);
      return Optional.of(stringType.getValue());
    }
    return Optional.empty();
  }

  @Override
  public Optional<String> getStringExtensionValue(final DomainResource domainResource, final String url) {
    final Extension extension = domainResource.getExtensionByUrl(url);
    if (extension != null) {
      return getStringValue(extension);
    }
    return Optional.empty();
  }

  @Override
  public Optional<String> getStringExtensionValue(final Element element, final String url) {
    final Extension extension = element.getExtensionByUrl(url);
    if (extension != null) {
      return getStringValue(extension);
    }
    return Optional.empty();
  }


}
