package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticTableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueType;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.TableDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirService;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.stereotype.Component;

@Component
public class FhirAllergyIntoleranceResolver
    extends AbstractDatasetResolver
    implements TableDatasetResolver {

  private final FhirService fhirService;

  public FhirAllergyIntoleranceResolver(final FhirService fhirService) {
    super(DatasetNames.ALLERGY_INTOLERANCE);
    this.fhirService = fhirService;
  }

  private List<AllergyIntolerance> resolveAllergyIntolerances(final String patientId) {
    final IGenericClient client = fhirService.createClient();
    final Bundle allergyIntoleranceBundle = client.search()
        .forResource(AllergyIntolerance.class)
        .where(AllergyIntolerance.PATIENT.hasId(patientId))
        .and(AllergyIntolerance.TYPE.isMissing(false))
        .sort()
        .descending(AllergyIntolerance.DATE)
        .returnBundle(Bundle.class)
        .execute();
    return fhirService.toListOfResourcesOfType(allergyIntoleranceBundle, AllergyIntolerance.class);
  }

  @Override
  public TableDataset resolveTableDataset(final String patientId,
                                          final DatasetConfiguration configuration) {

    final StaticTableDataset table = new StaticTableDataset(Map.of(
        "allergies", ValueType.COLLECTION,
        "intolerances", ValueType.COLLECTION
    ));
    final Collection<AllergyIntolerance> allergyIntolerances =
        resolveAllergyIntolerances(patientId);

    final List<String> allergies = allergyIntolerances.stream()
        .filter(allergyIntolerance -> allergyIntolerance.getType() ==
            AllergyIntolerance.AllergyIntoleranceType.ALLERGY)
        .map(AllergyIntolerance::getCode)
        .map(HumanReadableHelper::toString)
        .toList();

    final List<String> intolerances = allergyIntolerances.stream()
        .filter(allergyIntolerance -> allergyIntolerance.getType() ==
            AllergyIntolerance.AllergyIntoleranceType.INTOLERANCE)
        .map(AllergyIntolerance::getCode)
        .map(HumanReadableHelper::toString)
        .toList();

    table.addRow(Map.of("allergies", allergies, "intolerances", intolerances));

    return table;
  }
}
