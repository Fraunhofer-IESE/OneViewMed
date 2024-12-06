package de.fhg.iese.oneviewmed.backend.data.service.fhir;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl.CodeSystems;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Type;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultFhirProcedureService
    implements FhirProcedureService {

  private static final String EXTENSION_URL_MAIN_SURGERY =
      "https://oneviewmed.iese.fhg.de/fhir#surgery-main";

  private final FhirService fhirService;

  @Override
  public Collection<Procedure> getCompletedSurgeriesForPatient(final String patientId) {
    final IGenericClient fhirServiceClient = fhirService.createClient();
    final Bundle bundle = fhirServiceClient.search()
        .forResource(Procedure.class)
        .where(Procedure.SUBJECT.hasId(patientId))
        .and(Procedure.CATEGORY.exactly().code(CodeSystems.PROCEDURE_CATEGORY_SURGICAL_PROCEDURE))
        .and(Procedure.STATUS.exactly().code(Procedure.ProcedureStatus.COMPLETED.toCode()))
        .sort()
        .descending(Procedure.DATE)
        .returnBundle(Bundle.class)
        .execute();
    return fhirService.toListOfResourcesOfType(bundle, Procedure.class);
  }

  @Override
  public boolean isMainSurgery(final Procedure procedure) {
    return fhirService.getBoolExtensionValue(procedure, EXTENSION_URL_MAIN_SURGERY)
        .orElse(Boolean.FALSE);
  }

  private Optional<Date> toEndDate(final Type type) {
    if (type.isDateTime()) {
      return Optional.of(type.castToDateTime(type))
          .map(DateTimeType::getValue);
    } else if (type instanceof Period) {
      return Optional.of(type.castToPeriod(type))
          .map(Period::getEnd);
    }
    return Optional.empty();
  }

  private Optional<Instant> getPerformedEnd(final Procedure procedure) {
    return Optional.ofNullable(procedure.getPerformed())
        .flatMap(this::toEndDate)
        .map(Date::toInstant);
  }

  @Override
  public Optional<Long> getDaysSince(final Procedure procedure) {
    return getPerformedEnd(procedure)
        .map(end -> Duration.between(end, Instant.now()))
        .map(Duration::toDays);
  }

}
