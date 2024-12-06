package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticValueDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueType;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.ValueDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirEncounterService;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirService;
import java.time.Duration;
import java.time.Instant;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Period;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class FhirHospitalStayDatasetResolver
    extends AbstractDatasetResolver
    implements ValueDatasetResolver {

  private final FhirService fhirService;
  private final FhirEncounterService fhirEncounterService;

  public FhirHospitalStayDatasetResolver(final FhirService fhirService,
                                         final FhirEncounterService fhirEncounterService) {
    super(DatasetNames.CURRENT_HOSPITAL_STAY);
    this.fhirService = fhirService;
    this.fhirEncounterService = fhirEncounterService;
  }

  @Nullable
  private Encounter getCurrentEncounterForPatient(final String patientId) {
    final IGenericClient fhirServiceClient = fhirService.createClient();
    return fhirEncounterService.getCurrentEncounterForPatient(fhirServiceClient, patientId);
  }

  private String formatDuration(final Duration duration) {
    return DurationFormatUtils.formatDurationISO(duration.toMillis());
  }

  @Override
  public ValueDataset resolveValueDataset(final String patientId,
                                          final DatasetConfiguration configuration) {
    final Encounter encounter = getCurrentEncounterForPatient(patientId);
    if (encounter == null) {
      return StaticValueDataset.builder()
          .text(StandardValues.UNKNOWN)
          .type(ValueType.STRING)
          .build();
    }
    final Period period = encounter.getPeriod();
    if (period == null) {
      return StaticValueDataset.builder()
          .text(StandardValues.UNKNOWN)
          .type(ValueType.STRING)
          .build();
    }
    final Instant start = period.getStart().toInstant();
    final Duration duration = Duration.between(start, Instant.now());
    return StaticValueDataset.builder()
        .text(formatDuration(duration))
        .type(ValueType.DURATION)
        .build();
  }
}
