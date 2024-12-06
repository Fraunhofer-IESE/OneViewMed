package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;

import static java.util.function.Predicate.not;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticTableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueType;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.TableDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Component;

@Component
public class FhirPractitionersResolver
    extends AbstractDatasetResolver
    implements TableDatasetResolver {

  private final FhirService fhirService;

  public FhirPractitionersResolver(final FhirService fhirService) {
    super(DatasetNames.PRACTITIONERS);
    this.fhirService = Objects.requireNonNull(fhirService);
  }

  private Patient resolvePatientIncludingPractitioners(final String patientId) {
    final IGenericClient client = fhirService.createClient();
    final IBaseBundle bundle = client.search()
        .forResource(Patient.class)
        .where(Patient.RES_ID.exactly().code(patientId))
        .include(Patient.INCLUDE_GENERAL_PRACTITIONER)
        .execute();
    final List<Patient> result = fhirService.toListOfResourcesOfType(bundle, Patient.class);
    if (result.size() != 1) {
      throw new IllegalStateException();
    }
    return result.getFirst();
  }

  private Map<String, Object> rowFromPractitioner(final Practitioner practitioner) {
    final Map<String, Object> row = new HashMap<>();
    Optional.ofNullable(practitioner.getName())
        .filter(not(List::isEmpty))
        .ifPresent(name -> row.put("name", name.getFirst().getNameAsSingleString()));
    Optional.ofNullable(practitioner.getGender())
        .map(gender -> switch (gender) {
              case MALE -> "Hausarzt";
              case FEMALE -> "Hausärztin";
              default -> null;
            }
        )
        .ifPresent(role -> row.put("role", role));
    Optional.ofNullable(practitioner.getAddress())
        .filter(not(List::isEmpty))
        .map(List::getFirst)
        .map(HumanReadableHelper::toString)
        .ifPresent(address -> row.put("address", address));
    Optional.ofNullable(practitioner.getTelecom())
        .map(HumanReadableHelper::toContactPointsString)
        .ifPresent(telecom -> row.put("telecom", telecom));
    return row;
  }

  @Override
  public TableDataset resolveTableDataset(final String patientId,
                                          final DatasetConfiguration configuration) {
    final StaticTableDataset table = new StaticTableDataset(Map.of(
        "name", ValueType.STRING,
        "role", ValueType.STRING,
        "address", ValueType.STRING,
        "telecom", ValueType.STRING
    ));

    final Patient patient = resolvePatientIncludingPractitioners(patientId);
    final List<Reference> generalPractitioners = patient.getGeneralPractitioner();
    if (generalPractitioners != null) {
      generalPractitioners.stream()
          .map(Reference::getResource)
          .map(resource -> {
            if (resource instanceof final Practitioner practitioner) {
              if (!practitioner.getActive()) {
                return null;
              }
              return rowFromPractitioner(practitioner);
            } else {
              final String className =  resource.getClass().getSimpleName();
              throw new IllegalStateException("Unhandled type: " + className);
            }
          })
          .filter(Objects::nonNull)
          .forEach(table::addRow);
    }
    return table;
  }

}
