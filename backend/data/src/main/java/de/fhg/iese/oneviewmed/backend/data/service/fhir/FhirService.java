package de.fhg.iese.oneviewmed.backend.data.service.fhir;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Element;

public interface FhirService {

  URI getRepositoryUrl();

  IGenericClient createClient();

  <T extends IBaseResource> List<T> toListOfResourcesOfType(IBaseBundle theBundle,
                                                            Class<T> theTypeToInclude);

  <T extends IBaseResource> Optional<T> findResourceById(Class<? extends T> clazz, String id);

  <T extends IBaseResource> T getResourceById(Class<? extends T> clazz, String id);

  Optional<Boolean> getBoolExtensionValue(DomainResource domainResource, String url);

  Optional<Boolean> getBoolExtensionValue(Element element, String url);

  Optional<String> getStringExtensionValue(DomainResource domainResource, String url);

  Optional<String> getStringExtensionValue(Element element, String url);
}
