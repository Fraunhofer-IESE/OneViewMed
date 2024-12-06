package de.fhg.iese.oneviewmed.backend.data.service.common;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import org.springframework.lang.Nullable;

public final class DateUtils {

  private DateUtils() {
  }

  @Nullable
  public static Long daysBetween(@Nullable final Instant start, @Nullable final Instant end) {
    if ((start == null) || (end == null)) {
      return null;
    }
    return ChronoUnit.DAYS.between(start, end);
  }

  public static List<LocalDate> datesBetween(final Instant start, final Instant end) {
    final LocalDate startDate = start.atZone(ZoneOffset.UTC).toLocalDate();
    final LocalDate endDate = end.atZone(ZoneOffset.UTC).toLocalDate();
    return startDate.datesUntil(endDate).toList();
  }

  public static String formatDate(final TemporalAccessor localDate) {
    return DateTimeFormatter.ISO_DATE.format(localDate);
  }

}
