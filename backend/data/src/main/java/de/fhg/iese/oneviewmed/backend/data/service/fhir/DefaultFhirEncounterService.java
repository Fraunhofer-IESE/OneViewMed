package de.fhg.iese.oneviewmed.backend.data.service.fhir;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl.CodeSystems;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultFhirEncounterService implements FhirEncounterService {
  private static final String EXTENSION_URL_ENCOUNTER_TITLE =
      "https://oneviewmed.iese.fhg.de/fhir#encounter-title";

  private final FhirService fhirService;

  private List<Encounter> getEncountersForPatient(final IGenericClient fhirServiceClient,
                                                  final String patientId) {
    final Bundle bundle = fhirServiceClient.search().forResource(Encounter.class)
        .where(Encounter.PATIENT.hasId(patientId))
        .and(Encounter.CLASS.exactly().identifier(CodeSystems.ENCOUNTER_CLASS_INPATIENT_ENCOUNTER))
        .and(Encounter.TYPE.exactly().systemAndCode(CodeSystems.ENCOUNTER_TYPE_CONTACT_LEVEL,
            CodeSystems.ENCOUNTER_TYPE_LEVEL_HOSPITAL))
        .and(Encounter.STATUS.exactly().identifier(Encounter.EncounterStatus.INPROGRESS.toCode()))
        .sort()
        .descending(Encounter.DATE)
        .returnBundle(Bundle.class)
        .count(1)
        .execute();
    return fhirService.toListOfResourcesOfType(bundle, Encounter.class);
  }

  @Override
  @Nullable
  public Encounter getCurrentEncounterForPatient(final String patientId) {
    final IGenericClient fhirServiceClient = fhirService.createClient();
    return getCurrentEncounterForPatient(fhirServiceClient, patientId);
  }

  @Override
  @Nullable
  public Encounter getCurrentEncounterForPatient(final IGenericClient fhirServiceClient,
                                                 final String patientId) {
    final List<Encounter> encounters = this.getEncountersForPatient(fhirServiceClient, patientId);
    if (encounters.isEmpty()) {
      return null;
    }
    return encounters.getFirst();
  }

  @Override
  public List<Encounter> getStationStays(final String patientId) {
    final IGenericClient fhirServiceClient = fhirService.createClient();
    final Bundle bundle = fhirServiceClient.search()
        .forResource(Encounter.class)
        .where(Encounter.PATIENT.hasId(patientId))
        .and(Encounter.CLASS.exactly().identifier(CodeSystems.ENCOUNTER_CLASS_INPATIENT_ENCOUNTER))
        .and(Encounter.TYPE.exactly().systemAndCode(CodeSystems.ENCOUNTER_TYPE_CONTACT_LEVEL,
            CodeSystems.ENCOUNTER_TYPE_LEVEL_STATION))
        .and(Encounter.STATUS.exactly().codes(List.of(
            Encounter.EncounterStatus.ARRIVED.toCode(),
            Encounter.EncounterStatus.INPROGRESS.toCode(),
            Encounter.EncounterStatus.ONLEAVE.toCode(),
            Encounter.EncounterStatus.FINISHED.toCode()
        )))
        .sort()
        .descending(Encounter.DATE)
        .returnBundle(Bundle.class)
        .execute();
    return fhirService.toListOfResourcesOfType(bundle, Encounter.class);
  }

  @Override
  public List<Encounter> getVisits(final String patientId) {
    final IGenericClient fhirServiceClient = fhirService.createClient();
    final Bundle bundle = fhirServiceClient.search()
        .forResource(Encounter.class)
        .where(Encounter.PATIENT.hasId(patientId))
        .and(Encounter.CLASS.exactly().identifier(CodeSystems.ENCOUNTER_CLASS_INPATIENT_NON_ACUTE))
        .and(Encounter.STATUS.exactly().codes(List.of(
            Encounter.EncounterStatus.ARRIVED.toCode(),
            Encounter.EncounterStatus.INPROGRESS.toCode(),
            Encounter.EncounterStatus.ONLEAVE.toCode(),
            Encounter.EncounterStatus.FINISHED.toCode()
        )))
        .sort()
        .descending(Encounter.DATE)
        .returnBundle(Bundle.class)
        .execute();
    return fhirService.toListOfResourcesOfType(bundle, Encounter.class);
  }

  @Override
  public Optional<String> getTitleForEncounter(final Encounter encounter) {
    return fhirService.getStringExtensionValue(encounter, EXTENSION_URL_ENCOUNTER_TITLE);
  }

}
