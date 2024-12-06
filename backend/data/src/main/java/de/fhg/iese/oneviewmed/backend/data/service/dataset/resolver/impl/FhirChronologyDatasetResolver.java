package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;


import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticTableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueType;
import de.fhg.iese.oneviewmed.backend.data.service.common.DateUtils;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.TableDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirDateUtils;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirDocumentService;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirEncounterService;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirLocationService;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirProcedureService;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Encounter.EncounterLocationComponent;
import org.hl7.fhir.r4.model.Encounter.EncounterStatus;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class FhirChronologyDatasetResolver
    extends AbstractDatasetResolver
    implements TableDatasetResolver {

  private static final ZoneId UTC = ZoneId.of("UTC");

  private final FhirEncounterService fhirEncounterService;
  private final FhirProcedureService fhirProcedureService;
  private final FhirDocumentService fhirDocumentService;
  private final FhirLocationService fhirLocationService;

  public FhirChronologyDatasetResolver(final FhirEncounterService fhirEncounterService,
                                       final FhirProcedureService fhirProcedureService,
                                       final FhirDocumentService fhirDocumentService,
                                       final FhirLocationService fhirLocationService) {
    super(DatasetNames.CHRONOLOGY);
    this.fhirEncounterService = fhirEncounterService;
    this.fhirProcedureService = fhirProcedureService;
    this.fhirDocumentService = fhirDocumentService;
    this.fhirLocationService = fhirLocationService;
  }

  @Nullable
  private Instant getStart(final Encounter encounter) {
    final Period period = encounter.getPeriod();
    if ((period == null) || (period.getStart() == null)) {
      return null;
    }
    return period.getStart().toInstant();
  }

  private int daysToNow(final Encounter encounter) {
    final Instant start = getStart(encounter);
    if (start == null) {
      return 0;
    }
    return Optional.ofNullable(DateUtils.daysBetween(start, Instant.now()))
        .map(Math::abs)
        .map(Long::intValue)
        .orElse(0);
  }

  private static Map<String, String> toEventMap(final Procedure surgery) {
    final String type = Optional.ofNullable(surgery.getCategory())
        .map(HumanReadableHelper::toString)
        .orElse(StandardValues.UNKNOWN);
    final String title = Optional.ofNullable(surgery.getCode())
        .map(HumanReadableHelper::toString)
        .orElse(StandardValues.UNKNOWN);
    return Map.of("type", type, "title", title);
  }

  private Map<String, String> toVisitMap(final Encounter visit) {
    final String type = Optional.ofNullable(visit.getType())
        .map(HumanReadableHelper::toConceptsString)
        .orElse(StandardValues.UNKNOWN);
    final String title = fhirEncounterService.getTitleForEncounter(visit)
        .orElse(StandardValues.UNKNOWN);
    return Map.of("type", type, "title", title);
  }

  private static Optional<EncounterLocationComponent> getLocation(final Encounter stationStay) {
    if (!stationStay.hasLocation()) {
      return Optional.empty();
    }
    return Optional.ofNullable(stationStay.getLocationFirstRep());
  }

  @Override
  public TableDataset resolveTableDataset(final String patientId,
                                          final DatasetConfiguration configuration) {
    final StaticTableDataset table = new StaticTableDataset(Map.of(
        "date", ValueType.INSTANT,
        "station", ValueType.COLLECTION,
        "events", ValueType.COLLECTION,
        "documents", ValueType.COLLECTION,
        "visits", ValueType.COLLECTION
    ));

    final Encounter currentEncounter = fhirEncounterService.getCurrentEncounterForPatient(patientId);
    if (currentEncounter == null) {
      return table;
    }
    final Instant currentEncounterStart = getStart(currentEncounter);
    final int days = daysToNow(currentEncounter);

    final List<Encounter> stationStays =
        fhirEncounterService.getStationStays(patientId)
            .stream()
            .filter(Encounter::hasPeriod)
            .toList();

    final Collection<Procedure> events =
        fhirProcedureService.getCompletedSurgeriesForPatient(patientId);

    final Collection<DocumentReference> documents = fhirDocumentService.getDocuments(patientId);

    final List<Encounter> visits = fhirEncounterService.getVisits(patientId)
        .stream()
        .filter(Encounter::hasPeriod)
        .toList();

    LocalDate currentDate = LocalDate.now(Clock.systemUTC());
    for (int i = 0; i < days; i++) {
      currentDate = currentDate.minusDays(1L);
      final Instant dayBegin = ZonedDateTime.of(currentDate, LocalTime.MIN, UTC).toInstant();
      final Instant dayEnd = ZonedDateTime.of(currentDate, LocalTime.MAX, UTC).toInstant();

      final Map<String, Object> row = HashMap.newHashMap(5);
      row.put("date", dayBegin);

      stationStays.stream()
          .filter(stationStay -> {
            // in progress stays don't have an end, therefore check the start only
            if (stationStay.getStatus() == EncounterStatus.INPROGRESS) {
              final Instant start = getStart(stationStay);
              if (start == null) {
                return false;
              }
              return Objects.equals(currentEncounterStart, start) || dayBegin.isAfter(start);
            }
            return FhirDateUtils.overlaps(stationStay.getPeriod(), dayBegin, dayEnd);
          })
          .findFirst()
          .ifPresent(stationStay -> {
            final String stationType = Optional.ofNullable(stationStay.getServiceType())
                .map(HumanReadableHelper::toString)
                .orElse(StandardValues.UNKNOWN);
            final String stationName = FhirChronologyDatasetResolver.getLocation(stationStay)
                .map(EncounterLocationComponent::getLocation)
                .map(Reference::getReference)
                .map(fhirLocationService::getLocationById)
                .map(Location::getName)
                .orElse(StandardValues.UNKNOWN);
            row.put("station", List.of(Map.of("type", stationType, "name", stationName)));
          });

      final List<Map<String, String>> eventOfDay = events.stream()
          .filter(surgery -> FhirDateUtils.overlaps(surgery.getPerformed(), dayBegin, dayEnd))
          .map(FhirChronologyDatasetResolver::toEventMap)
          .toList();
      row.put("events", eventOfDay);

      final List<Map<String, String>> documentsOfDay = documents.stream()
          .filter(
              documentReference -> FhirDateUtils.overlaps(documentReference.getDate().toInstant(),
                  dayBegin, dayEnd))
          .flatMap(documentReference -> {
            final Optional<String> optionalType = Optional.ofNullable(documentReference.getType())
                .map(HumanReadableHelper::toString);

            return documentReference.getContent()
                .stream()
                .map(content -> {
                  final Map<String, String> document = HashMap.newHashMap(3);

                  optionalType.ifPresent(type -> document.put("type", type));

                  Optional.ofNullable(content.getAttachment())
                      .map(Attachment::getTitle)
                      .ifPresent(title -> document.put("title", title));

                  Optional.ofNullable(content.getAttachment())
                      .map(Attachment::getUrl)
                      .ifPresent(url -> document.put("url", url));

                  return document;
                });
          })
          .toList();
      row.put("documents", documentsOfDay);

      final List<Map<String, String>> visitsOfDay = visits.stream()
          .filter(visit -> FhirDateUtils.overlaps(visit.getPeriod(), dayBegin, dayEnd))
          .map(this::toVisitMap)
          .toList();
      row.put("visits", visitsOfDay);

      table.addRow(row);
    }

    return table;
  }

}
