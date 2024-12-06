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
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Meta;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class FhirMedicationStatementDatasetResolver extends AbstractMedicationResolver implements TableDatasetResolver {
  private final FhirService fhirService;
  private final FhirEncounterService fhirEncounterService;

  public FhirMedicationStatementDatasetResolver(final FhirService fhirService,
      final FhirEncounterService fhirEncounterService,
      final FhirMedicationService fhirMedicationService) {
    super(DatasetNames.MEDICATION_STATEMENT, fhirMedicationService);
    this.fhirService = fhirService;
    this.fhirEncounterService = fhirEncounterService;
  }

  @Nullable
  private Encounter getCurrentEncounterForPatient(final String patientId) {
    final IGenericClient fhirServiceClient = fhirService.createClient();
    return fhirEncounterService.getCurrentEncounterForPatient(fhirServiceClient, patientId);
  }

  private List<MedicationStatement> resolveMedicationStatements(final String patientId) {
    final IGenericClient client = fhirService.createClient();
    final Encounter encounter = getCurrentEncounterForPatient(patientId);
    final Date start = getEncounterStart(encounter);
    final Bundle bundle = client.search().forResource(MedicationStatement.class)
        .where(MedicationStatement.PATIENT.hasId(patientId))
        .and(MedicationStatement.EFFECTIVE.afterOrEquals().day(start))
        .sort()
        .descending(MedicationStatement.EFFECTIVE)
        .returnBundle(Bundle.class)
        .execute();
    return fhirService.toListOfResourcesOfType(bundle, MedicationStatement.class);
  }

  @Override
  public TableDataset resolveTableDataset(final String patientId,
      final DatasetConfiguration configuration) {
    final StaticTableDataset table = new StaticTableDataset(Map.of(
        "medicationName", ValueType.STRING,
        "medicationBrandName", ValueType.STRING,
        "medicationForm", ValueType.STRING,
        "effectiveDate", ValueType.INSTANT,
        "dosageInstructionText", ValueType.STRING,
        "dosageNote", ValueType.STRING,
        "source", ValueType.STRING));
    final Collection<MedicationStatement> medicationStatements = resolveMedicationStatements(patientId);
    medicationStatements.stream().map(medicationStatement -> {
      final Map<String, Object> row = new HashMap<>();

      final Reference medicationReference = medicationStatement.getMedicationReference();
      final Optional<String> optionalMedicationId = Optional.ofNullable(medicationReference)
          .map(Reference::getReference);
      if (optionalMedicationId.isPresent()) {
        final String medicationId = optionalMedicationId.get();
        final Map<String, Object> medicationProperties = getMedicationProperties(medicationId);
        row.putAll(medicationProperties);
      } else {
        row.put("medicationName", StandardValues.UNKNOWN);
      }
      Optional.ofNullable(medicationStatement.getMeta())
          .map(Meta::getSource)
          .map(this::trimSource)
          .ifPresent(source -> row.put("source", source));
      Optional.ofNullable(medicationStatement.getEffectiveDateTimeType().getValue())
          .map(Date::toInstant)
          .ifPresent(effectiveDate -> row.put("effectiveDate", effectiveDate));
      final String dosageInstructionText = medicationStatement.getDosage().stream()
          .map(Dosage::getText)
          .collect(Collectors.joining());
      final String dosageNote = medicationStatement.getNote().stream()
          .map(Annotation::getText)
          .collect(Collectors.joining());
      row.put("dosageInstructionText", dosageInstructionText);
      row.put("dosageNote", dosageNote);
      return row;
    }).forEach(table::addRow);

    return table;
  }
}
