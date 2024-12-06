package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticTableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueType;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.TableDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirEncounterService;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirMedicalInformationService;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirService;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.codesystems.ObservationCategory;
import org.springframework.stereotype.Component;

@Component
public class FhirLaboratoryResultsDatasetResolver extends AbstractDatasetResolver implements TableDatasetResolver {
  private final FhirService fhirService;
  private final FhirEncounterService fhirEncounterService;
  private final FhirMedicalInformationService fhirMedicalInformationService;

  protected FhirLaboratoryResultsDatasetResolver(final FhirService fhirService,
                                                 final FhirEncounterService fhirEncounterService,
                                                 final FhirMedicalInformationService fhirMedicalInformationService) {
    super(DatasetNames.LABORATORY_RESULTS);
    this.fhirService = fhirService;
    this.fhirEncounterService = fhirEncounterService;
    this.fhirMedicalInformationService = fhirMedicalInformationService;
  }

  private List<Observation> resolveObservations(final String patientId) {
    final IGenericClient client = fhirService.createClient();
    final Encounter currentEncounter = fhirEncounterService.getCurrentEncounterForPatient(client, patientId);
    if (currentEncounter == null) {
      return List.of();
    }
    final Bundle bundle = client.search().forResource(Observation.class)
        .where(Observation.PATIENT.hasId(patientId))
        .and(Observation.ENCOUNTER.hasId(currentEncounter.getIdPart()))
//        .and(Observation.STATUS.exactly()
//            .codes( ObservationStatus.FINAL.toCode(),
//                ObservationStatus.AMENDED.toCode(),
//                ObservationStatus.CORRECTED.toCode()))
        .and(Observation.CATEGORY.exactly()
            .systemAndCode(CodeSystems.OBSERVATION_CATEGORY,
                ObservationCategory.LABORATORY.toCode()))
        .sort()
        .descending(Observation.DATE)
        .returnBundle(Bundle.class)
        .execute();
    return fhirService.toListOfResourcesOfType(bundle, Observation.class);
  }

  @Override
  public TableDataset resolveTableDataset(final String patientId,
                                          final DatasetConfiguration configuration) {
    final boolean onlyInfectious = configuration.getBool("onlyInfectious")
        .orElse(Boolean.FALSE);

    final Predicate<Observation> filter;
    if (onlyInfectious) {
      filter = (observation) -> {
        if (observation.hasCode()) {
          final CodeableConcept code = observation.getCode();
          if (code.hasCoding()) {
            return code.getCoding()
                .stream()
                .anyMatch(fhirMedicalInformationService::isInfectious);
          }
        }
        return false;
      };
    } else {
      filter = (observation) -> true;
    }

    final StaticTableDataset table = new StaticTableDataset(Map.of(
        "issued", ValueType.INSTANT,
        "status", ValueType.STRING,
        "code", ValueType.STRING,
        "name", ValueType.STRING,
        "value", ValueType.STRING
    ));
    resolveObservations(patientId)
        .stream()
        .filter(filter)
        .map(observation -> {
          final Map<String, Object> row = HashMap.newHashMap(5);

          Optional.ofNullable(observation.getIssued())
              .map(Date::toInstant)
              .ifPresent(code -> row.put("issued", code));

          Optional.ofNullable(observation.getStatus())
              .map(ObservationStatus::getDisplay)
              .ifPresent(status -> row.put("status", status));

          Optional.ofNullable(observation.getCode().getCodingFirstRep())
              .map(Coding::getCode)
              .ifPresent(code -> row.put("code", code));

          Optional.ofNullable(observation.getCode())
              .map(HumanReadableHelper::toString)
              .ifPresent(name -> row.put("name", name));

          if (observation.hasValueQuantity()) {
            final Quantity quantity = observation.getValueQuantity();
            final String value = String.valueOf(quantity.getValue());
            final String unit = String.valueOf(quantity.getUnit());
            row.put("value", value + " " + unit);
          }

          return row;
        })
        .forEach(table::addRow);
    return table;
  }

}
