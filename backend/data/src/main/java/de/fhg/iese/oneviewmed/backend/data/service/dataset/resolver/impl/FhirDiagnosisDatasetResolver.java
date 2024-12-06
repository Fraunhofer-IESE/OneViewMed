package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetType;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticTableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticValueDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueType;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.TableDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.ValueDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirEncounterService;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirService;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Narrative;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.stereotype.Component;

@Component
public class FhirDiagnosisDatasetResolver
    extends AbstractDatasetResolver
    implements ValueDatasetResolver, TableDatasetResolver {

  private final FhirService fhirService;
  private final FhirEncounterService fhirEncounterService;

  public FhirDiagnosisDatasetResolver(final FhirService fhirService,
                                      final FhirEncounterService fhirEncounterService) {
    super(DatasetNames.DIAGNOSIS);
    this.fhirService = fhirService;
    this.fhirEncounterService = fhirEncounterService;
  }

  @Override
  public boolean supports(final DatasetType type) {
    return (type == DatasetType.VALUE) || (type == DatasetType.TABLE);
  }

  private List<Condition> resolveConditions(final String patientId,
                                            final DatasetConfiguration configuration) {
    final boolean onlyConfirmed = configuration.getBool("onlyConfirmed")
        .orElse(Boolean.FALSE);

    final IGenericClient client = fhirService.createClient();
    final Encounter currentEncounter =
        fhirEncounterService.getCurrentEncounterForPatient(client, patientId);
    if (currentEncounter == null) {
      return List.of();
    }
    final Bundle bundle = client.search().forResource(Condition.class)
        .where(Condition.PATIENT.hasId(patientId))
        .and(Condition.ENCOUNTER.hasId(currentEncounter.getIdPart()))
        .sort().descending(Condition.ONSET_DATE)
        .sort().ascending(Condition.CODE)
        .returnBundle(Bundle.class)
        .execute();

    return fhirService.toListOfResourcesOfType(bundle, Condition.class);
  }

  private static boolean hasPrincipalDiagnosisRole(final Encounter.DiagnosisComponent diagnosisComponent) {
    if (!diagnosisComponent.hasUse()) {
      return false;
    }
    final CodeableConcept use = diagnosisComponent.getUse();
    return use.hasCoding(CodeSystems.DIAGNOSIS_ROLE, CodeSystems.PRINCIPAL_DIAGNOSIS);
  }

  private Optional<Condition> resolveMainDiagnosis(final String patientId) {
    final IGenericClient client = fhirService.createClient();
    final Encounter currentEncounter = fhirEncounterService.getCurrentEncounterForPatient(client, patientId);
    if (currentEncounter == null) {
      return Optional.empty();
    }
    return currentEncounter.getDiagnosis()
        .stream()
        .filter(FhirDiagnosisDatasetResolver::hasPrincipalDiagnosisRole)
        .map(diagnosisComponent -> diagnosisComponent.getCondition().getReference())
        .map(reference -> fhirService.getResourceById(Condition.class, reference))
        .findFirst();
  }

  @Override
  public ValueDataset resolveValueDataset(final String patientId,
                                          final DatasetConfiguration configuration) {
    final String value = resolveMainDiagnosis(patientId)
        .map(condition -> {
          final Narrative text = condition.getText();
          if (text.hasDiv()) {
            return text.getDiv().allText();
          }
          return HumanReadableHelper.toString(condition.getCode());
        }).orElse(StandardValues.UNKNOWN);

    return StaticValueDataset.builder()
        .text(value)
        .type(ValueType.STRING)
        .build();
  }

  @Override
  public TableDataset resolveTableDataset(final String patientId,
                                          final DatasetConfiguration configuration) {
    final StaticTableDataset table = new StaticTableDataset(Map.of(
        "category", ValueType.STRING,
        "code", ValueType.STRING,
        "recorded", ValueType.INSTANT,
        "onset", ValueType.STRING,
        "bodySite", ValueType.STRING,
        "isMain", ValueType.BOOLEAN
    ));
    final Optional<String> mainDiagnosisId = resolveMainDiagnosis(patientId).map(Resource::getId);
    resolveConditions(patientId, configuration)
        .stream()
        .map(condition -> {
          final Map<String, Object> row = HashMap.newHashMap(5);

          Optional.ofNullable(condition.getCategoryFirstRep())
              .map(HumanReadableHelper::toString)
              .ifPresent(category -> row.put("category", category));

          Optional.ofNullable(condition.getCode())
              .map(HumanReadableHelper::toString)
              .ifPresent(code -> row.put("code", code));

          Optional.ofNullable(condition.getRecordedDate())
              .map(Date::toInstant)
              .ifPresent(recorded -> row.put("recorded", recorded));

          Optional.ofNullable(condition.getOnset())
              .map(HumanReadableHelper::toTimeString)
              .ifPresent(onset -> row.put("onset", onset));

          Optional.ofNullable(HumanReadableHelper.toConceptsString(condition.getBodySite()))
              .ifPresent(bodySite -> row.put("bodySite", bodySite));

          final boolean isMainDiagnosis = mainDiagnosisId
              .map(mainConditionId -> Objects.equals(mainConditionId, condition.getId()))
              .orElse(Boolean.FALSE);
          row.put("isMain", isMainDiagnosis);

          return row;
        })
        .forEach(table::addRow);
    return table;
  }

}
