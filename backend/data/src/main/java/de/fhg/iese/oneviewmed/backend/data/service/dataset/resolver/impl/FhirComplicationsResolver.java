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
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirService;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Procedure;
import org.springframework.stereotype.Component;

@Component
public class FhirComplicationsResolver
    extends AbstractDatasetResolver
    implements ValueDatasetResolver, TableDatasetResolver {

  private final FhirService fhirService;

  public FhirComplicationsResolver(final FhirService fhirService) {
    super(DatasetNames.COMPLICATIONS);
    this.fhirService = fhirService;
  }

  @Override
  public boolean supports(final DatasetType type) {
    return (type == DatasetType.VALUE) || (type == DatasetType.TABLE);
  }

  private Collection<Procedure> getProceduresWithComplications(final String patientId) {
    final IGenericClient fhirServiceClient = fhirService.createClient();
    final Bundle bundle = fhirServiceClient.search().forResource(Procedure.class)
        .where(Procedure.SUBJECT.hasId(patientId))
        .sort()
        .descending(Procedure.DATE)
        .returnBundle(Bundle.class)
        .execute();
    return fhirService.toListOfResourcesOfType(bundle, Procedure.class)
        .stream()
        .filter(procedure -> procedure.hasComplication() || procedure.hasComplicationDetail())
        .toList();
  }

  private Optional<String> getComplicationDescription(final Procedure procedure) {
    return Optional.ofNullable(
            HumanReadableHelper.toReferencesString(procedure.getComplicationDetail()))
        .or(() -> Optional.ofNullable(
            HumanReadableHelper.toConceptsString(procedure.getComplication())));
  }

  @Override
  public TableDataset resolveTableDataset(final String patientId,
                                          final DatasetConfiguration configuration) {
    final StaticTableDataset table = new StaticTableDataset(Map.of(
        "date", ValueType.INSTANT,
        "description", ValueType.STRING
    ));

    getProceduresWithComplications(patientId)
        .stream()
        .map(procedure -> {
          final Map<String, Object> row = HashMap.newHashMap(2);

          Optional.ofNullable(procedure.getPerformed())
              .map(performed -> {
                if (performed instanceof final DateTimeType dateTime) {
                  return dateTime.getValue().toInstant();
                }
                return HumanReadableHelper.toTimeString(performed);
              })
              .ifPresent(date -> row.put("date", date));

          getComplicationDescription(procedure)
              .ifPresent(description -> row.put("description", description));

          return row;
        })
        .forEach(table::addRow);

    return table;
  }

  @Override
  public ValueDataset resolveValueDataset(final String patientId,
                                          final DatasetConfiguration configuration) {

    final String descriptions = getProceduresWithComplications(patientId)
        .stream()
        .map(this::getComplicationDescription)
        .flatMap(Optional::stream)
        .collect(Collectors.joining("\n"));

    return StaticValueDataset.builder()
        .text(descriptions)
        .type(ValueType.STRING)
        .build();
  }

}
