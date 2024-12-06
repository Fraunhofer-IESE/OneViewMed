package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;

import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticTableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueType;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.TableDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirService;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ICoding;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

@Component
public class FhirEmergencyContactsResolver
    extends AbstractDatasetResolver
    implements TableDatasetResolver {

  private final FhirService fhirService;

  public FhirEmergencyContactsResolver(final FhirService fhirService) {
    super(DatasetNames.EMERGENCY_CONTACTS);
    this.fhirService = Objects.requireNonNull(fhirService);
  }

  private static boolean isEmergencyContact(final Patient.ContactComponent contact) {
    return contact.getRelationship()
        .stream()
        .anyMatch(relationship -> relationship.hasCoding(CodeSystems.CONTACT_ROLE,
            CodeSystems.CONTACT_ROLE_EMERGENCY_CONTACT));
  }

  private static boolean isRoleCoding(final ICoding coding) {
    return coding.hasSystem() &&
        (Objects.equals(coding.getSystem(), CodeSystems.ROLE_CODE) ||
            Objects.equals(coding.getSystem(), CodeSystems.SNOMED_SCT));
  }

  private static boolean hasRoleCoding(final CodeableConcept codeableConcept) {
    if (!codeableConcept.hasCoding()) {
      return false;
    }
    final Collection<Coding> codings = codeableConcept.getCoding();
    return codings.stream().anyMatch(FhirEmergencyContactsResolver::isRoleCoding);
  }

  private static Optional<String> findContactRole(final Patient.ContactComponent contact) {
    final List<CodeableConcept> roles = contact.getRelationship()
        .stream()
        .filter(FhirEmergencyContactsResolver::hasRoleCoding)
        .toList();
    return Optional.ofNullable(HumanReadableHelper.toConceptsString(roles));
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
    final Patient patient = fhirService.getResourceById(Patient.class, patientId);
    final List<Patient.ContactComponent> contacts = patient.getContact();
    if (contacts != null) {
      contacts.stream()
          .filter(FhirEmergencyContactsResolver::isEmergencyContact)
          .map(contact -> {
            final Map<String, Object> row = HashMap.newHashMap(4);

            Optional.ofNullable(contact.getName())
                .ifPresent(name -> row.put("name", name.getNameAsSingleString()));
            findContactRole(contact)
                .ifPresent(role -> row.put("role", role));
            Optional.ofNullable(contact.getAddress())
                .map(HumanReadableHelper::toString)
                .ifPresent(address -> row.put("address", address));
            Optional.ofNullable(contact.getTelecom())
                .map(HumanReadableHelper::toContactPointsString)
                .ifPresent(telecom -> row.put("telecom", telecom));

            return row;
          })
          .forEach(table::addRow);
    }
    return table;
  }

}
