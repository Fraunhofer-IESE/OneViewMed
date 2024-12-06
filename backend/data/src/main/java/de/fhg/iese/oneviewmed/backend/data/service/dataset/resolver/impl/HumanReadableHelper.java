package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;

import static java.util.function.Predicate.not;

import com.google.common.base.Strings;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Age;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Narrative;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Range;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Timing.UnitsOfTime;
import org.hl7.fhir.r4.model.Type;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;

public final class HumanReadableHelper {

  private static final ZoneId CET = ZoneId.of("CET");

  private static final String ALTERNATIVES_DELIMITER = " / ";
  private static final String LISTING_DELIMITER = ", ";

  private HumanReadableHelper() {
  }

  @Nullable
  private static String formatCoding(final IBaseCoding coding) {
    final String display = coding.getDisplay();
    if (!Strings.isNullOrEmpty(display)) {
      return display;
    }
    return coding.getCode();
  }

  @Nullable
  public static String toString(final UnitsOfTime unitsOfTime) {
    final String unitsOfTimeString = unitsOfTime.toString();
    return switch (unitsOfTimeString) {
      case "A" -> "Jährlich";
      case "MO" -> "Monatlich";
      case "WK" -> "Wöchentlich";
      case "D" -> "Täglich";
      case "H" -> "Stündlich";
      case "MIN" -> "Minütlich";
      case "S" -> "Sekündlich";
      default -> null;
    };
  }

  @Nullable
  public static String toString(final DomainResource domainResource) {
    final Narrative text = domainResource.getText();
    if (text.hasDiv()) {
      final String allText = text.getDiv().allText();
      return Strings.emptyToNull(allText);
    }
    return null;
  }

  @Nullable
  public static String toCodingsString(final Collection<? extends Coding> codings) {
    final String formatedCodings = codings
        .stream()
        .map(HumanReadableHelper::formatCoding)
        .map(Strings::emptyToNull)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.joining(ALTERNATIVES_DELIMITER));
    return Strings.emptyToNull(formatedCodings);
  }

  @Nullable
  public static String toString(final CodeableConcept codeableConcept) {
    final String text = codeableConcept.getText();
    if (!Strings.isNullOrEmpty(text)) {
      return text;
    }
    return toCodingsString(codeableConcept.getCoding());
  }

  @Nullable
  public static String toConceptsString(
      final Collection<? extends CodeableConcept> codeableConcepts) {
    if (codeableConcepts.isEmpty()) {
      return null;
    }
    final String concepts = codeableConcepts.stream()
        .map(HumanReadableHelper::toString)
        .map(Strings::emptyToNull)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.joining(LISTING_DELIMITER));
    return Strings.emptyToNull(concepts);
  }

  @Nullable
  public static String toReferencesString(final Collection<? extends Reference> references) {
    if (references.isEmpty()) {
      return null;
    }
    final String formatedReferences = references
        .stream()
        .map(Reference::getDisplay)
        .map(Strings::emptyToNull)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.joining(LISTING_DELIMITER));
    return Strings.emptyToNull(formatedReferences);
  }

  private static String toString(final DateTimeType dateTime) {
    return switch (dateTime.getPrecision()) {
      case YEAR, MONTH, DAY -> dateTime.getValueAsString();
      default -> {
        final Locale locale = LocaleContextHolder.getLocale();
        final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("E dd.MM.yyyy HH:mm", locale);
        yield formatter.format(dateTime.getValue().toInstant().atZone(CET));
      }
    };
  }

  @Nullable
  public static String toTimeString(final Type dataType) {
    return switch (dataType) {
      case final Period period -> toString(period.getStartElement()) + " - " +
          toString(period.getEndElement());
      case final Age age -> age.getDisplay();
      case final Range range -> range.toString();
      case final DateTimeType dateTime -> toString(dateTime);
      case final StringType stringType -> stringType.getValue();
      default -> null;
    };
  }

  public static String toString(final Address address) {
    final String text = address.getText();
    if (!Strings.isNullOrEmpty(text)) {
      return text;
    }
    return Stream.concat(
            address.getLine()
                .stream()
                .map(StringType::toString),
            Stream.of(
                Stream.of(address.getPostalCode(), address.getCity())
                    .filter(not(Strings::isNullOrEmpty))
                    .collect(Collectors.joining(" ")),
                address.getCountry()))
        .filter(not(Strings::isNullOrEmpty))
        .collect(Collectors.joining(", "));
  }

  public static String toString(final ContactPoint contactPoint) {
    final ContactPoint.ContactPointSystem system = contactPoint.getSystem();
    if (system == null) {
      return contactPoint.getValue();
    }
    return system.getDisplay() + ": " + contactPoint.getValue();
  }

  public static String toContactPointsString(
      final Collection<? extends ContactPoint> contactPoints) {
    return contactPoints.stream()
        .filter(Objects::nonNull)
        .sorted(Comparator.nullsLast(Comparator.comparing(ContactPoint::getRank)))
        .map(HumanReadableHelper::toString)
        .collect(Collectors.joining("\n"));
  }

  @Nullable
  private static String toString(@Nullable final BigDecimal decimal) {
    if (decimal == null) {
      return null;
    }
    return decimal.toString();
  }

  @Nullable
  public static String toString(final Quantity quantity) {
    final String display = quantity.getDisplay();
    if (!Strings.isNullOrEmpty(display)) {
      return display;
    }
    final String value = toString(quantity.getValue());
    final String unit = quantity.getUnit();
    final String text = (unit != null)
        ? (value + " " + unit)
        : value;
    return Strings.emptyToNull(text);
  }

}
