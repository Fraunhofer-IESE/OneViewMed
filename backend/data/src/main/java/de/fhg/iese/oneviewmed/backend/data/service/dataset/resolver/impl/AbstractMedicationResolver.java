package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;

import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirMedicationService;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Medication;
import org.springframework.lang.Nullable;

public abstract class AbstractMedicationResolver extends AbstractDatasetResolver {
  private final FhirMedicationService fhirMedicationService;

  protected AbstractMedicationResolver(final String name,
                                       final FhirMedicationService fhirMedicationService) {
    super(name);
    this.fhirMedicationService = fhirMedicationService;
  }

  @Nullable
  protected String trimSource(@Nullable final String source) {
    if (source == null) {
      return null;
    }
    final int splitIndex = source.indexOf('#');
    if (splitIndex == -1) {
      return source;
    }
    return source.substring(0, splitIndex);
  }

  protected Date getEncounterStart(@Nullable final Encounter encounter) {
    if ((encounter == null) || !encounter.hasPeriod()) {
      return new Date();
    }
    return encounter.getPeriod().getStart();
  }

  protected Map<String, Object> getMedicationProperties(final String medicationId) {
    final Map<String, Object> row = HashMap.newHashMap(3);
    final Medication medication = fhirMedicationService.getMedicationPerId(medicationId);

    final String medicationName = Optional.ofNullable(medication.getCode())
        .map(CodeableConcept::getText)
        .orElse(StandardValues.UNKNOWN);
    row.put("medicationName", medicationName);

    Optional.ofNullable(medication.getCode())
        .map(CodeableConcept::getCoding)
        .map(HumanReadableHelper::toCodingsString)
        .ifPresent(medicationBrandName -> row.put("medicationBrandName",
            medicationBrandName));

    Optional.ofNullable(medication.getForm())
        .map(CodeableConcept::getCoding)
        .map(HumanReadableHelper::toCodingsString)
        .ifPresent(display -> row.put("medicationForm", display));

    return row;
  }
}
