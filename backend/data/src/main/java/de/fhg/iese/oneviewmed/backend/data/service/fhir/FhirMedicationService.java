package de.fhg.iese.oneviewmed.backend.data.service.fhir;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Medication;
import org.springframework.stereotype.Service;

@Service
public class FhirMedicationService {
  private final FhirService fhirService;

  public FhirMedicationService(final FhirService fhirService) {
    this.fhirService = fhirService;
  }

  public Medication getMedicationPerId(final String medicationId) {
    if (medicationId.isEmpty()) {
      return new Medication();
    }
    final IGenericClient client = fhirService.createClient();
    return client.read().resource(Medication.class).withId(medicationId).execute();
  }
}
