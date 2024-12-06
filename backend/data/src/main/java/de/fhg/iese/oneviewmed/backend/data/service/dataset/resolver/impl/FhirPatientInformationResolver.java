package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticTableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueType;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.TableDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirService;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Patient.PatientCommunicationComponent;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.hl7.fhir.r4.model.codesystems.ObservationCategory;
import org.springframework.stereotype.Component;

@Component
public class FhirPatientInformationResolver
    extends AbstractDatasetResolver
    implements TableDatasetResolver {

  private static final String EXTENSION_URL_CADAVERIC_DONOR =
      "http://hl7.org/fhir/StructureDefinition/patient-cadavericDonor";

  private final FhirService fhirService;

  public FhirPatientInformationResolver(final FhirService fhirService) {
    super(DatasetNames.PATIENT_INFORMATION);
    this.fhirService = fhirService;
  }

  private Optional<String> getLastVitalSignValue(final String patientId, final String system,
                                                 final String code) {
    final IGenericClient client = fhirService.createClient();
    final Bundle bundle = client.search()
        .forResource(Observation.class)
        .where(Observation.PATIENT.hasId(patientId))
        .and(Observation.CATEGORY.exactly().systemAndCode(CodeSystems.OBSERVATION_CATEGORY,
            ObservationCategory.VITALSIGNS.toCode()))
        .and(Observation.CODE.exactly().systemAndCode(system, code))
        .and(Observation.STATUS.exactly().codes(Observation.ObservationStatus.FINAL.toCode()))
        .sort().descending(Observation.DATE)
        .count(1)
        .returnBundle(Bundle.class)
        .execute();
    return fhirService.toListOfResourcesOfType(bundle, Observation.class)
        .stream()
        .map(Observation::getValueQuantity)
        .map(HumanReadableHelper::toString)
        .findFirst();
  }

  private Optional<String> getLastBodyHeight(final String patientId) {
    return getLastVitalSignValue(patientId, CodeSystems.LOINC,
        CodeSystems.LOINC_BODY_HEIGHT);
  }

  private Optional<String> getLastBodyWeight(final String patientId) {
    return getLastVitalSignValue(patientId, CodeSystems.LOINC,
        CodeSystems.LOINC_BODY_WEIGHT);
  }

  private String getLanguage(final Patient patient) {
    final Optional<PatientCommunicationComponent> communicationComponent =
        patient.getCommunication()
            .stream()
            .filter(PatientCommunicationComponent::getPreferred)
            .findFirst();
    return communicationComponent.map(PatientCommunicationComponent::getLanguage)
        .map(HumanReadableHelper::toString)
        .orElse(StandardValues.UNKNOWN);
  }

  private boolean hasCare(final String patientId) {
    final IGenericClient client = fhirService.createClient();
    final Bundle bundle = client.search()
        .forResource(RelatedPerson.class)
        .where(RelatedPerson.PATIENT.hasId(patientId))
        .and(RelatedPerson.RELATIONSHIP.exactly()
            .systemAndCode(CodeSystems.ROLE_CODE, CodeSystems.ROLE_CODE_GUARD))
        .count(1)
        .returnBundle(Bundle.class)
        .execute();
    return bundle.getTotal() > 0;
  }

  @Override
  public TableDataset resolveTableDataset(final String patientId,
                                          final DatasetConfiguration configuration) {
    final StaticTableDataset tableDataset = new StaticTableDataset(Map.of(
        "height", ValueType.STRING,
        "weight", ValueType.STRING,
        "communicationLanguage", ValueType.STRING,
        "therapyLimitation", ValueType.STRING,
        "patientProvision", ValueType.STRING,
        "care", ValueType.BOOLEAN
    ));
    fhirService.findResourceById(Patient.class, patientId)
        .ifPresent(patient -> {

          getLastBodyHeight(patientId)
              .ifPresent(height -> tableDataset.addRow(Map.of("height", height)));

          getLastBodyWeight(patientId)
              .ifPresent(weight -> tableDataset.addRow(Map.of("weight", weight)));

          tableDataset.addRow(Map.of("communicationLanguage", getLanguage(patient)));

          final String therapyLimitation = hasTherapyLimitation(patientId)
              ? StandardValues.YES
              : StandardValues.NO;
          tableDataset.addRow(Map.of("therapyLimitation", therapyLimitation));

          final String patientProvision = isCadavericDonor(patient).isPresent()
              ? StandardValues.AVAILABLE
              : StandardValues.NOT_AVAILABLE;
          tableDataset.addRow(Map.of("patientProvision", patientProvision));

          tableDataset.addRow(Map.of("care", hasCare(patientId)));
        });
    return tableDataset;
  }

  private Boolean hasTherapyLimitation(final String patientId) {
    final IGenericClient client = fhirService.createClient();
    final Bundle bundle = client.search()
        .forResource(Consent.class)
        .where(Consent.PATIENT.hasId(patientId))
        .returnBundle(Bundle.class)
        .execute();
    return fhirService.toListOfResourcesOfType(bundle, Consent.class)
        .stream()
        .map(Consent::getCategory)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .anyMatch(category -> category.hasCoding(CodeSystems.CONSENT_CATEGORY,
            CodeSystems.CONSENT_CATEGORY_DO_NOT_RESUSCITATE)
            || category.hasCoding(CodeSystems.CONSENT_CATEGORY,
            CodeSystems.CONSENT_CATEGORY_HEALTH_CARE_DIRECTIVE)
            || category.hasCoding(CodeSystems.CONSENT_CATEGORY, CodeSystems.CONSENT_CATEGORY_POLST)
        );
  }

  private Optional<Boolean> isCadavericDonor(final Patient patient) {
    return fhirService.getBoolExtensionValue(patient, EXTENSION_URL_CADAVERIC_DONOR);
  }

}
