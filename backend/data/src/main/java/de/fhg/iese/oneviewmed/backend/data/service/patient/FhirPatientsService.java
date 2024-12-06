package de.fhg.iese.oneviewmed.backend.data.service.patient;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.fhg.iese.oneviewmed.backend.data.entity.Gender;
import de.fhg.iese.oneviewmed.backend.data.entity.Patient;
import de.fhg.iese.oneviewmed.backend.data.service.common.NotFoundException;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl.CodeSystems;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl.StandardValues;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirService;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FhirPatientsService
    implements PatientsService {

  private final FhirService fhirService;

  private Gender toGender(@Nullable final Enumerations.AdministrativeGender gender) {
    if (gender == null) {
      return Gender.UNKNOWN;
    }
    return Gender.valueOf(gender.name());
  }

  private boolean isCaseNumber(final Identifier identifier) {
    if (!identifier.hasType()) {
      return false;
    }
    final Coding coding = identifier.getType().getCodingFirstRep();
    return coding.is(CodeSystems.IDENTIFIER_TYPE, CodeSystems.IDENTIFIER_TYPE_VISIT_NUMBER);
  }

  @Nullable
  private String getCaseNumber(final org.hl7.fhir.r4.model.Patient fhirPatient) {
    final Optional<String> caseNumber = fhirPatient.getIdentifier().stream()
        .filter(this::isCaseNumber)
        .map(Identifier::getValue)
        .findFirst();
    return caseNumber.orElse(null);
  }

  private Patient toPatient(final org.hl7.fhir.r4.model.Patient fhirPatient) {
    final String caseNumber = getCaseNumber(fhirPatient);
    final String familyName;
    final String givenName;
    final List<HumanName> names = fhirPatient.getName();
    if (names.isEmpty()) {
      familyName = StandardValues.UNKNOWN;
      givenName = StandardValues.UNKNOWN;
    } else {
      final HumanName name = names.getFirst();
      familyName = name.getFamily();
      givenName = name.getGiven()
          .stream()
          .map(StringType::getValueNotNull)
          .collect(Collectors.joining(" "));
    }
    final Date birthDate = fhirPatient.getBirthDate();
    return Patient.builder()
        .id(fhirPatient.getIdElement().getIdPart())
        .caseNumber(caseNumber)
        .familyName(familyName)
        .givenName(givenName)
        .birthDate((birthDate == null) ? null : birthDate.toInstant())
        .gender(toGender(fhirPatient.getGender()))
        .build();
  }

  @Override
  public Collection<Patient> getPatients() {
    final IGenericClient client = fhirService.createClient();
    final Bundle bundle = client.search()
        .forResource(org.hl7.fhir.r4.model.Patient.class)
        .where(org.hl7.fhir.r4.model.Patient.ACTIVE.exactly().code(Boolean.TRUE.toString()))
        .and(org.hl7.fhir.r4.model.Patient.DECEASED.exactly().code(Boolean.FALSE.toString()))
        .sort().ascending(org.hl7.fhir.r4.model.Patient.FAMILY)
        .sort().ascending(org.hl7.fhir.r4.model.Patient.GIVEN)
        .returnBundle(Bundle.class)
        .execute();

    return fhirService.toListOfResourcesOfType(bundle, org.hl7.fhir.r4.model.Patient.class)
        .stream()
        .map(this::toPatient)
        .toList();
  }

  public Optional<Patient> findPatient(final String id) {
    return fhirService.findResourceById(org.hl7.fhir.r4.model.Patient.class, id)
        .map(this::toPatient);
  }

  @Override
  public Patient getPatient(final String id)
      throws NotFoundException {
    return findPatient(id)
        .orElseThrow(() -> new NotFoundException("Patient with id " + id + " not found"));
  }

}
