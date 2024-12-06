package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticTableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueType;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.TableDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirService;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Period;
import org.springframework.stereotype.Component;

@Component
public class FhirPreviousHospitalStaysDatasetResolver extends AbstractDatasetResolver
    implements TableDatasetResolver {

  private final FhirService fhirService;

  public FhirPreviousHospitalStaysDatasetResolver(final FhirService fhirService) {
    super(DatasetNames.PREVIOUS_HOSPITAL_STAYS);
    this.fhirService = Objects.requireNonNull(fhirService);
  }

  private List<Encounter> getEncountersForPatient(final String patientId) {
    final IGenericClient fhirServiceClient = fhirService.createClient();
    final Bundle bundle = fhirServiceClient.search().forResource(Encounter.class)
        .where(Encounter.PATIENT.hasId(patientId))
        .and(Encounter.CLASS.exactly().identifier(CodeSystems.ENCOUNTER_CLASS_INPATIENT_ENCOUNTER))
        .and(Encounter.STATUS.exactly().identifier("finished"))
        .sort()
        .descending(Encounter.DATE)
        .returnBundle(Bundle.class)
        .execute();
    return fhirService.toListOfResourcesOfType(bundle, Encounter.class);
  }

  @Override
  public TableDataset resolveTableDataset(final String patientId,
                                          final DatasetConfiguration configuration) {
    final StaticTableDataset table = new StaticTableDataset(Map.of(
        "startDate", ValueType.INSTANT,
        "endDate", ValueType.INSTANT,
        "department", ValueType.STRING
    ));
    final Collection<Encounter> encounters = getEncountersForPatient(patientId);
    encounters.stream()
        .map(encounter -> {
          final Period period = encounter.getPeriod();
          final Instant startDate = period.getStart().toInstant();
          final Instant endDate = period.getEnd().toInstant();
          final String department =
              Optional.ofNullable(encounter.getServiceType())
                  .map(HumanReadableHelper::toString)
                  .orElse(StandardValues.UNKNOWN);
          return Map.<String, Object>of(
              "startDate", startDate,
              "endDate", endDate,
              "department", department);
        })
        .forEach(table::addRow);
    return table;
  }

}
