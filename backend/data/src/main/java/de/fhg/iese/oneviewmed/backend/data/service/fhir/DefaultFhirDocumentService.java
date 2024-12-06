package de.fhg.iese.oneviewmed.backend.data.service.fhir;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DocumentReference;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultFhirDocumentService implements FhirDocumentService {

  private final FhirService fhirService;

  @Override
  public List<DocumentReference> getDocuments(final String patientId) {
    final IGenericClient client = fhirService.createClient();
    final Bundle bundle = client.search()
        .forResource(DocumentReference.class)
        .where(DocumentReference.PATIENT.hasId(patientId))
        .sort().descending(DocumentReference.DATE)
        .sort().ascending(DocumentReference.IDENTIFIER)
        .returnBundle(Bundle.class)
        .execute();
    return fhirService.toListOfResourcesOfType(bundle, DocumentReference.class);
  }

  @Override
  public DocumentReference getDocumentById(final String id) {
    return fhirService.getResourceById(DocumentReference.class, id);
  }

}
