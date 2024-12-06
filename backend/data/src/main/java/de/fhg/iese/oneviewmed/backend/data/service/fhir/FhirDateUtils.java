package de.fhg.iese.oneviewmed.backend.data.service.fhir;

import java.time.Instant;
import java.util.Date;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Type;
import org.springframework.lang.Nullable;

public final class FhirDateUtils {

  private FhirDateUtils() {
  }

  public static boolean overlaps(@Nullable final Instant time, final Instant begin,
                                 final Instant end) {
    if (time == null) {
      return false;
    }
    return begin.isBefore(time) && end.isAfter(time);
  }

  public static boolean overlaps(@Nullable final Period period, final Instant begin,
                                 final Instant end) {
    if (period == null) {
      return false;
    }
    final Date periodBegin = period.getStart();
    final Date periodEnd = period.getEnd();
    if ((periodBegin == null) || (periodEnd == null)) {
      return false;
    }
    return periodBegin.toInstant().isBefore(end) && periodEnd.toInstant().isAfter(begin);
  }

  public static boolean overlaps(@Nullable final Type type, final Instant begin,
                                 final Instant end) {
    if (type == null) {
      return false;
    }
    if (type.isDateTime()) {
      return overlaps(type.castToDateTime(type).getValue().toInstant(), begin, end);
    } else if (type instanceof Period) {
      return overlaps(type.castToPeriod(type), begin, end);
    }
    return false;
  }

}
