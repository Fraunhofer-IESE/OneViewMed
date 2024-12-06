package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticTableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueType;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.TableDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirEncounterService;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirService;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Type;
import org.springframework.stereotype.Component;

@Component
public class FhirVitalSignDatasetResolver
    extends AbstractDatasetResolver
    implements TableDatasetResolver {

  private final FhirService fhirService;
  private final FhirEncounterService fhirEncounterService;

  public FhirVitalSignDatasetResolver(final FhirService fhirService,
                                      final FhirEncounterService fhirEncounterService) {
    super(DatasetNames.VITAL_SIGNS);
    this.fhirService = fhirService;
    this.fhirEncounterService = fhirEncounterService;
  }

  private Collection<Observation> getVitalSignsForPatient(final String patientId) {
    final IGenericClient client = fhirService.createClient();
    final Encounter currentEncounter =
        fhirEncounterService.getCurrentEncounterForPatient(client, patientId);
    if (currentEncounter == null) {
      return Collections.emptyList();
    }
    final Bundle bundle = client.search().forResource(Observation.class)
        .where(Observation.PATIENT.hasId(patientId))
        .and(Observation.CATEGORY.exactly().identifier("vital-signs"))
        .and(Observation.ENCOUNTER.hasId(currentEncounter.getIdPart()))
        .sort()
        .descending(Observation.DATE)
        .returnBundle(Bundle.class)
        .execute();
    return fhirService.toListOfResourcesOfType(bundle, Observation.class);
  }

  @Override
  public TableDataset resolveTableDataset(final String patientId,
                                          final DatasetConfiguration configuration) {
    final StaticTableDataset table = new StaticTableDataset(Map.of(
        "date", ValueType.INSTANT,
        "value", ValueType.STRING,
        "notes", ValueType.COLLECTION
    ));
    final Collection<Observation> observations = getVitalSignsForPatient(patientId);
    observations.stream().map(observation -> {
      final Map<String, Object> row = HashMap.newHashMap(3);

      Optional.ofNullable(observation.getIssued())
          .map(Date::toInstant)
          .ifPresent(date -> row.put("date", date));

      Optional.ofNullable(observation.getValue())
          .map(Type::toString)
          .ifPresent(value -> row.put("value", value));

      final List<String> notes = observation.getNote()
          .stream()
          .map(Annotation::getText)
          .toList();
      row.put("notes", notes);

      return row;
    }).forEach(table::addRow);
    return table;
  }

}
