package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticTableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueType;
import de.fhg.iese.oneviewmed.backend.data.service.common.DateUtils;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.TableDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirDateUtils;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirEncounterService;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirLocationService;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirMedicationService;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Encounter.EncounterLocationComponent;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Meta;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class FhirMedicationChronologyDatasetResolver extends AbstractMedicationResolver
    implements TableDatasetResolver, InitializingBean {
  private final FhirService fhirService;
  private final FhirEncounterService fhirEncounterService;
  private final FhirLocationService fhirLocationService;
  private final FhirMedicationService fhirMedicationService;
  private final Map<String, String> medicationSubstanceGroup;
  private final Map<String, String> medicationSubstanceGroupColor;

  public FhirMedicationChronologyDatasetResolver(final FhirService fhirService,
      final FhirEncounterService fhirEncounterService,
      final FhirLocationService fhirLocationService,
      final FhirMedicationService fhirMedicationService) {
    super(DatasetNames.MEDICATION_CHRONOLOGY, fhirMedicationService);
    this.fhirService = fhirService;
    this.fhirEncounterService = fhirEncounterService;
    this.fhirLocationService = fhirLocationService;
    this.fhirMedicationService = fhirMedicationService;
    this.medicationSubstanceGroup = new HashMap<>();
    this.medicationSubstanceGroupColor = new HashMap<>();
  }

  @Nullable
  private Encounter getCurrentEncounterForPatient(final IGenericClient client,
      final String patientId) {
    return fhirEncounterService.getCurrentEncounterForPatient(client, patientId);
  }

  private List<MedicationRequest> resolveMedicationRequests(final String patientId) {
    final IGenericClient client = fhirService.createClient();
    final Encounter encounter = getCurrentEncounterForPatient(client, patientId);
    if (encounter == null) {
      return List.of();
    }
    final Period encounterPeriod = encounter.getPeriod();
    final Bundle bundle = client.search().forResource(MedicationRequest.class)
        .where(MedicationRequest.PATIENT.hasId(patientId))
        .and(MedicationRequest.AUTHOREDON.afterOrEquals().day(encounterPeriod.getStart()))
        .sort()
        .descending(MedicationRequest.AUTHOREDON)
        .returnBundle(Bundle.class)
        .execute();
    return fhirService.toListOfResourcesOfType(bundle, MedicationRequest.class);
  }

  @Override
  public void afterPropertiesSet() {
    medicationSubstanceGroup.put("06312077", "Analgetikum");
    medicationSubstanceGroup.put("04560474", "Analgetikum");
    medicationSubstanceGroup.put("10203595", "Analgetikum");
    medicationSubstanceGroup.put("90045017", "Antibiotikum");
    medicationSubstanceGroup.put("12345678", "Zytostatikum");
    medicationSubstanceGroup.put("06959500", "Protonenpumpenhemmer");
    medicationSubstanceGroup.put("01401057", "Antibiotikum");

    medicationSubstanceGroupColor.put("Analgetikum", "#EEF02F");
    medicationSubstanceGroupColor.put("Antibiotikum", "#DAF7A6");
    medicationSubstanceGroupColor.put("Zytostatikum", "#3498DB");
    medicationSubstanceGroupColor.put("Protonenpumpenhemmer", "#FFC300");
  }

  public String getSubstanceGroupByCode(@Nullable final String substanceCode) {
    return medicationSubstanceGroup.getOrDefault(substanceCode, StandardValues.UNKNOWN);
  }

  public String getSubstanceGroupColor(@Nullable final String substanceGroup) {
    return medicationSubstanceGroupColor.get(substanceGroup);
  }

  public Map<String, Object> getMedicationRowValues(final Medication medication) {
    final Map<String, Object> row = new HashMap<>();
    Optional.ofNullable(medication.getCode())
        .map(CodeableConcept::getText)
        .ifPresent(display -> row.put("medicationName", display));
    Optional.ofNullable(medication.getCode())
        .map(CodeableConcept::getCoding)
        .map(HumanReadableHelper::toCodingsString)
        .ifPresent(brandName -> row.put("medicationBrandName", brandName));
    final String substanceCode = Optional.ofNullable(medication.getCode())
        .map(CodeableConcept::getCoding)
        .flatMap(codings -> codings.stream().findFirst())
        .map(Coding::getCode)
        .orElse(null);
    final String substanceGroup = getSubstanceGroupByCode(substanceCode);
    Optional.ofNullable(medication.getForm())
        .map(CodeableConcept::getCoding)
        .map(HumanReadableHelper::toCodingsString)
        .ifPresent(display -> row.put("medicationForm", display));
    row.put("substanceGroup", substanceGroup);
    row.put("substanceGroupColor", getSubstanceGroupColor(substanceGroup));
    return row;
  }

  private List<Map<String, Object>> getMedications(
      final Collection<? extends MedicationRequest> medicationRequests) {
    return medicationRequests.stream().map(medicationRequest -> {
      final Map<String, Object> row = new HashMap<>();
      final Optional<String> optionalMedicationId = Optional
          .ofNullable(medicationRequest.getMedicationReference().getReference());

      if (optionalMedicationId.isPresent()) {
        final String medicationId = optionalMedicationId.get();
        final Medication medication = fhirMedicationService.getMedicationPerId(medicationId);
        final Map<String, Object> medicationRowValues = getMedicationRowValues(medication);
        row.putAll(medicationRowValues);
      }
      Optional.ofNullable(medicationRequest.getMeta())
          .map(Meta::getSource)
          .map(this::trimSource)
          .ifPresent(source -> row.put("source", source));

      Optional.ofNullable(medicationRequest.getAuthoredOn())
          .map(Date::toInstant)
          .ifPresent(authoredOn -> row.put("authoredOn", authoredOn));

      Optional.ofNullable(medicationRequest.getDispenseRequest())
          .map(MedicationRequest.MedicationRequestDispenseRequestComponent::getValidityPeriod)
          .map(Period::getStart)
          .map(Date::toInstant)
          .ifPresent(dispenseStart -> row.put("dispenseStart", dispenseStart));

      Optional.ofNullable(medicationRequest.getDispenseRequest())
          .map(MedicationRequest.MedicationRequestDispenseRequestComponent::getValidityPeriod)
          .map(Period::getEnd)
          .map(Date::toInstant)
          .ifPresent(dispenseEnd -> row.put("dispenseEnd", dispenseEnd));

      final String dosageInstructionText = medicationRequest.getDosageInstruction().stream()
          .map(Dosage::getText)
          .collect(Collectors.joining());
      row.put("dosageInstructionText", dosageInstructionText);

      final String dosageNote = medicationRequest.getNote().stream()
          .map(Annotation::getText)
          .collect(Collectors.joining());
      row.put("dosageNote", dosageNote);

      return row;
    }).toList();
  }

  private Optional<Instant> getFirstDispenseStart(
      final Collection<? extends MedicationRequest> medicationRequests) {
    return medicationRequests.stream()
        .filter(MedicationRequest::hasDispenseRequest)
        .map(MedicationRequest::getDispenseRequest)
        .filter(MedicationRequest.MedicationRequestDispenseRequestComponent::hasValidityPeriod)
        .map(MedicationRequest.MedicationRequestDispenseRequestComponent::getValidityPeriod)
        .min(Comparator.comparing(Period::getStart))
        .map(Period::getStart)
        .map(Date::toInstant);
  }

  private Optional<Instant> getLastDispenseEnd(
      final Collection<? extends MedicationRequest> medicationRequests) {
    return medicationRequests.stream()
        .filter(MedicationRequest::hasDispenseRequest)
        .map(MedicationRequest::getDispenseRequest)
        .filter(MedicationRequest.MedicationRequestDispenseRequestComponent::hasValidityPeriod)
        .map(MedicationRequest.MedicationRequestDispenseRequestComponent::getValidityPeriod)
        .max(Comparator.comparing(Period::getEnd))
        .map(Period::getEnd)
        .map(Date::toInstant);
  }

  private List<Encounter> getStationStays(final String patientId,
                                          final Optional<Instant> optionalBegin,
                                          final Optional<Instant> optionalEnd) {

    if (optionalBegin.isEmpty() || optionalEnd.isEmpty()) {
      return List.of();
    }
    final Encounter currentEncounter = fhirEncounterService.getCurrentEncounterForPatient(patientId);
    final Optional<Instant> optionalCurrentEncounterStart = Optional.ofNullable(currentEncounter)
        .map(Encounter::getPeriod)
        .map(Period::getStart)
        .map(Date::toInstant);
    if (optionalCurrentEncounterStart.isEmpty()) {
      return List.of();
    }
    final Instant currentEncounterStart = optionalCurrentEncounterStart.get();
    final Instant begin = optionalBegin.get();
    final Instant end = optionalEnd.get();
    return fhirEncounterService.getStationStays(patientId)
        .stream()
        .filter(Encounter::hasPeriod)
        .filter(stationStay -> {
          // in progress stays don't have an end, therefore check the start only
          if (stationStay.getStatus() == Encounter.EncounterStatus.INPROGRESS) {
            final Instant start = Optional.ofNullable(stationStay.getPeriod())
                .map(Period::getStart)
                .map(Date::toInstant)
                .orElse(null);
            if (start == null) {
              return false;
            }
            return Objects.equals(currentEncounterStart, start) ||
                (start.isAfter(begin) && start.isBefore(end));
          }
          return FhirDateUtils.overlaps(stationStay.getPeriod(), begin, end);
        })
        .sorted(Comparator.comparing(e -> e.getPeriod().getStart()))
        .toList();
  }

  private String getStationName(final Encounter stationStay) {
    if (!stationStay.hasLocation()) {
      return Optional.ofNullable(stationStay.getServiceType())
          .map(HumanReadableHelper::toString)
          .orElse(StandardValues.UNKNOWN);
    }
    return Optional.ofNullable(stationStay.getLocationFirstRep())
        .map(EncounterLocationComponent::getLocation)
        .map(Reference::getReference)
        .map(fhirLocationService::getLocationById)
        .map(Location::getName)
        .orElse(StandardValues.UNKNOWN);
  }

  private List<Map<String, Object>> getStations(final String patientId,
      final Collection<? extends MedicationRequest> medicationRequests) {
    final Optional<Instant> firstStart = getFirstDispenseStart(medicationRequests);
    final Optional<Instant> lastEnd = getLastDispenseEnd(medicationRequests);
    final List<Encounter> stationStays = getStationStays(patientId, firstStart, lastEnd);
    final Map<String, Object> stations = new HashMap<>();
    for (final Encounter stationStay : stationStays) {
      final String name = getStationName(stationStay);
      final Instant start = stationStay.getPeriod().getStart().toInstant();
      final Instant end = Optional.ofNullable(stationStay.getPeriod().getEnd())
          .map(Date::toInstant)
          .orElseGet(Instant::now);
      final List<LocalDate> dates = DateUtils.datesBetween(start, end);
      for (final LocalDate date : dates) {
        stations.put(DateUtils.formatDate(date), name);
      }
    }
    return List.of(stations);
  }

  @Override
  public TableDataset resolveTableDataset(final String patientId,
      final DatasetConfiguration configuration) {
    final StaticTableDataset table = new StaticTableDataset(Map.of(
        "medications", ValueType.COLLECTION,
        "stations", ValueType.COLLECTION));

    final Collection<MedicationRequest> medicationRequests = resolveMedicationRequests(patientId);

    final List<Map<String, Object>> medications = getMedications(medicationRequests);
    final List<Map<String, Object>> stations = getStations(patientId, medicationRequests);
    table.addRow(Map.of("medications", medications, "stations", stations));

    return table;
  }

}
