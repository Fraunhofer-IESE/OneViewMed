package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticTableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueType;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.TableDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirEncounterService;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirMedicationService;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirService;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Meta;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class FhirActiveMedicationDatasetResolver extends AbstractMedicationResolver implements TableDatasetResolver {
  private static final String MEDICATION_ACTIVE_STATUS = "active";

  private final FhirService fhirService;
  private final FhirEncounterService fhirEncounterService;

  public FhirActiveMedicationDatasetResolver(final FhirService fhirService,
      final FhirEncounterService fhirEncounterService,
      final FhirMedicationService fhirMedicationService) {
    super(DatasetNames.ACTIVE_MEDICATION, fhirMedicationService);
    this.fhirService = fhirService;
    this.fhirEncounterService = fhirEncounterService;
  }

  @Nullable
  private Encounter getCurrentEncounterForPatient(final String patientId) {
    final IGenericClient fhirServiceClient = fhirService.createClient();
    return fhirEncounterService.getCurrentEncounterForPatient(fhirServiceClient, patientId);
  }

  private List<MedicationRequest> resolveMedicationRequests(final String patientId) {
    final IGenericClient client = fhirService.createClient();
    final Encounter encounter = getCurrentEncounterForPatient(patientId);
    final Date start = getEncounterStart(encounter);
    final Bundle bundle = client.search().forResource(MedicationRequest.class)
        .where(MedicationRequest.PATIENT.hasId(patientId))
        .and(MedicationRequest.STATUS.exactly().identifier(MEDICATION_ACTIVE_STATUS))
        .and(MedicationRequest.AUTHOREDON.afterOrEquals().day(start))
        .sort()
        .descending(MedicationRequest.AUTHOREDON)
        .returnBundle(Bundle.class)
        .execute();
    return fhirService.toListOfResourcesOfType(bundle, MedicationRequest.class);
  }

  @Override
  public TableDataset resolveTableDataset(final String patientId,
      final DatasetConfiguration configuration) {
    final StaticTableDataset table = new StaticTableDataset(Map.of(
        "medicationName", ValueType.STRING,
        "medicationBrandName", ValueType.STRING,
        "medicationForm", ValueType.STRING,
        "authoredOn", ValueType.INSTANT,
        "dispenseStart", ValueType.INSTANT,
        "dispenseEnd", ValueType.INSTANT,
        "dosageInstructionText", ValueType.STRING,
        "dosageNote", ValueType.STRING,
        "source", ValueType.STRING));
    final Collection<MedicationRequest> medicationRequests = resolveMedicationRequests(patientId);
    medicationRequests.stream().map(medicationRequest -> {
      final Map<String, Object> row = HashMap.newHashMap(9);

      final Reference medicationReference = medicationRequest.getMedicationReference();
      final Optional<String> optionalMedicationId = Optional.ofNullable(medicationReference)
          .map(Reference::getReference);
      if (optionalMedicationId.isPresent()) {
        final String medicationId = optionalMedicationId.get();
        final Map<String, Object> medicationProperties = getMedicationProperties(medicationId);
        row.putAll(medicationProperties);
      } else {
        row.put("medicationName", StandardValues.UNKNOWN);
      }
      Optional.ofNullable(medicationRequest.getMeta())
          .map(Meta::getSource)
          .map(this::trimSource)
          .ifPresent(source -> row.put("source", source));

      Optional.ofNullable(medicationRequest.getAuthoredOn())
          .map(Date::toInstant)
          .ifPresent(authoredOn -> row.put("authoredOn", authoredOn));

      Optional.ofNullable(medicationRequest.getDispenseRequest())
          .map(MedicationRequest.MedicationRequestDispenseRequestComponent::getValidityPeriod)
          .map(Period::getStart)
          .map(Date::toInstant)
          .ifPresent(dispenseStart -> row.put("dispenseStart", dispenseStart));

      Optional.ofNullable(medicationRequest.getDispenseRequest())
          .map(MedicationRequest.MedicationRequestDispenseRequestComponent::getValidityPeriod)
          .map(Period::getEnd)
          .map(Date::toInstant)
          .ifPresent(dispenseEnd -> row.put("dispenseEnd", dispenseEnd));

      final String dosageInstructionText = medicationRequest.getDosageInstruction().stream()
          .map(Dosage::getText)
          .collect(Collectors.joining());
      row.put("dosageInstructionText", dosageInstructionText);

      final String dosageNote = medicationRequest.getNote().stream()
          .map(Annotation::getText)
          .collect(Collectors.joining());
      row.put("dosageNote", dosageNote);

      return row;
    }).forEach(table::addRow);

    return table;
  }
}
