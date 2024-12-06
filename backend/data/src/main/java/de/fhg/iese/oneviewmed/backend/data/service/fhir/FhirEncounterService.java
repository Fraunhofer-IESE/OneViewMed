package de.fhg.iese.oneviewmed.backend.data.service.fhir;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.Encounter;
import org.springframework.lang.Nullable;

public interface FhirEncounterService {

  @Nullable
  Encounter getCurrentEncounterForPatient(String patientId);

  @Nullable
  Encounter getCurrentEncounterForPatient(IGenericClient fhirServiceClient, String patientId);

  List<Encounter> getStationStays(String patientId);

  List<Encounter> getVisits(String patientId);

  Optional<String> getTitleForEncounter(Encounter encounter);
}
