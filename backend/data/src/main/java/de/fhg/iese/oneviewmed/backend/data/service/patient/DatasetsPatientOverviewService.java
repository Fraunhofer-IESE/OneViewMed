package de.fhg.iese.oneviewmed.backend.data.service.patient;

import de.fhg.iese.oneviewmed.backend.data.entity.PatientOverview;
import de.fhg.iese.oneviewmed.backend.data.entity.StaticPatientOverview;
import de.fhg.iese.oneviewmed.backend.data.entity.StaticStationStay;
import de.fhg.iese.oneviewmed.backend.data.entity.StationStay;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticDatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueDataset;
import de.fhg.iese.oneviewmed.backend.data.service.common.NotFoundException;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.DatasetsService;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl.DatasetNames;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl.HumanReadableHelper;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl.StandardValues;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirEncounterService;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirProcedureService;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Period;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DatasetsPatientOverviewService
    implements PatientOverviewService {

  private final DatasetsService datasetsService;

  private final FhirEncounterService fhirEncounterService;
  private final FhirProcedureService fhirProcedureService;

  @Override
  public PatientOverview getPatientOverview(final String patientId)
      throws ExecutionException, InterruptedException {

    final CompletableFuture<String> diagnosisFuture = CompletableFuture.supplyAsync(
        () -> getDiagnosis(patientId));

    final CompletableFuture<String> surgeryFuture = CompletableFuture.supplyAsync(
        () -> getSurgery(patientId));

    final CompletableFuture<List<String>> importantEventsFuture = CompletableFuture.supplyAsync(
        () -> getImportantEvents(patientId));

    final CompletableFuture<Boolean> allergiesFuture = CompletableFuture.supplyAsync(
        () -> hasAllergies(patientId));

    final CompletableFuture<String> infectionFuture = CompletableFuture.supplyAsync(
        () -> getInfection(patientId));

    final CompletableFuture<Boolean> complicationsFuture = CompletableFuture.supplyAsync(
        () -> hasComplications(patientId));

    final CompletableFuture<Long> postSurgeryDaysFuture = CompletableFuture.supplyAsync(
        () -> postSurgeryDays(patientId));

    final CompletableFuture<List<StationStay>> stationStaysFuture = CompletableFuture.supplyAsync(
        () -> getStationStays(patientId).reversed());

    final CompletableFuture<Void> allFuture =
        CompletableFuture.allOf(diagnosisFuture, surgeryFuture, importantEventsFuture,
            allergiesFuture, infectionFuture, complicationsFuture, postSurgeryDaysFuture,
            stationStaysFuture);
    allFuture.join();

    return StaticPatientOverview.builder()
        .diagnosis(diagnosisFuture.get())
        .surgery(surgeryFuture.get())
        .importantEvents(importantEventsFuture.get())
        .allergies(allergiesFuture.get())
        .infection(infectionFuture.get())
        .complication(complicationsFuture.get())
        .postSurgeryDays(postSurgeryDaysFuture.get())
        .stationStays(stationStaysFuture.get())
        .build();
  }

  private boolean hasAllergies(final String patientId) {
    try {
      final TableDataset allergiesTable =
          datasetsService.getTableDataset(DatasetNames.ALLERGY_INTOLERANCE, patientId,
              StaticDatasetConfiguration.none());
      final List<Map<String, Object>> values = allergiesTable.getValues();
      if (values.isEmpty()) {
        return false;
      }
      final Map<String, Object> firstValue = values.getFirst();
      final Object object = firstValue.get("allergies");
      if (object instanceof final List<?> allergies) {
        return !allergies.isEmpty();
      }
      return false;
    } catch (final NotFoundException e) {
      throw new CompletionException(e);
    }
  }

  private boolean hasComplications(final String patientId) {
    try {
      final TableDataset complicationsTable =
          datasetsService.getTableDataset(DatasetNames.COMPLICATIONS, patientId,
              StaticDatasetConfiguration.none());
      return !complicationsTable.getValues().isEmpty();
    } catch (final NotFoundException e) {
      throw new CompletionException(e);
    }
  }

  @Nullable
  private String getDiagnosis(final String patientId) {
    try {
      final ValueDataset diagnosisValue =
          datasetsService.getValueDataset(DatasetNames.DIAGNOSIS, patientId,
              StaticDatasetConfiguration.none());
      return diagnosisValue.getValueAsText();
    } catch (final NotFoundException e) {
      throw new CompletionException(e);
    }
  }

  @Nullable
  private String getSurgery(final String patientId) {
    try {
      final ValueDataset surgeriesValue =
          datasetsService.getValueDataset(DatasetNames.SURGERIES, patientId,
              StaticDatasetConfiguration.none());
      return surgeriesValue.getValueAsText();
    } catch (final NotFoundException e) {
      throw new CompletionException(e);
    }
  }

  private List<String> getImportantEvents(final String patientId) {
    try {
      final TableDataset complicationsTable =
          datasetsService.getTableDataset(DatasetNames.COMPLICATIONS, patientId,
              StaticDatasetConfiguration.none());
      return complicationsTable.getValues()
          .stream()
          .map(row -> (String) row.get("description"))
          .toList();
    } catch (final NotFoundException e) {
      throw new CompletionException(e);
    }
  }

  @Nullable
  private String getInfection(final String patientId) {
    try {
      final TableDataset laboratoryResultsTable =
          datasetsService.getTableDataset(DatasetNames.LABORATORY_RESULTS, patientId,
              new StaticDatasetConfiguration(Map.of("onlyInfectious", Boolean.TRUE.toString())));
      final List<Map<String, Object>> rows = laboratoryResultsTable.getValues();
      if (rows.isEmpty()) {
        return null;
      }
      return (String) rows.getFirst().get("name");
    } catch (final NotFoundException e) {
      throw new CompletionException(e);
    }
  }

  @Nullable
  private Long postSurgeryDays(final String patientId) {
    return fhirProcedureService.getCompletedSurgeriesForPatient(patientId)
        .stream()
        .filter(fhirProcedureService::isMainSurgery)
        .map(fhirProcedureService::getDaysSince)
        .flatMap(Optional::stream)
        .min(Long::compareTo)
        .orElse(null);
  }

  private List<StationStay> getStationStays(final String patientId) {
    final Encounter currentEncounter = fhirEncounterService.getCurrentEncounterForPatient(patientId);
    if (currentEncounter == null) {
      return List.of();
    }
    final Instant currentEncounterStart = currentEncounter.getPeriod().getStart().toInstant();

    final List<Encounter> stationStays = fhirEncounterService.getStationStays(patientId)
            .stream()
            .filter(Encounter::hasPeriod)
            .filter(encounter -> {
              final Period period = encounter.getPeriod();
              final Instant startDate = period.getStart().toInstant();
              return startDate.equals(currentEncounterStart)
                  || startDate.isAfter(currentEncounterStart);
            })
            .toList();

    return stationStays
        .stream()
        .<StationStay>map(encounter -> {
          final Period period = encounter.getPeriod();
          final Instant startDate = period.getStart().toInstant();
          final Instant endDate = Optional.ofNullable(period.getEnd())
              .map(Date::toInstant)
              .orElseGet(Instant::now);
          final String department = Optional.ofNullable(encounter.getServiceType())
              .map(HumanReadableHelper::toString)
              .orElse(StandardValues.UNKNOWN);

          final long difference = Math.abs(endDate.toEpochMilli() - startDate.toEpochMilli());
          // convert to days rounding upwards
          final long days = Math.ceilDiv(difference, 86400000L);

          return StaticStationStay.builder()
              .name(department)
              .days(days)
              .build();
        })
        .toList();
  }

}
