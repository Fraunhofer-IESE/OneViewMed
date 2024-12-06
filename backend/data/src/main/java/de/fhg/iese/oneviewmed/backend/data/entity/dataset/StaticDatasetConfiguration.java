package de.fhg.iese.oneviewmed.backend.data.entity.dataset;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import org.springframework.lang.Nullable;

public class StaticDatasetConfiguration implements DatasetConfiguration {

  private final Map<String, String> values;

  public StaticDatasetConfiguration(@Nullable final Map<String, String> values) {
    this.values = (values == null) ? Collections.emptyMap() : Map.copyOf(values);
  }

  @Override
  public Optional<String> getString(final String name) {
    final String value = values.get(name);
    return Optional.ofNullable(value);
  }

  @Override
  public Optional<Boolean> getBool(final String name) {
    final String value = values.get(name);
    if (value == null) {
      return Optional.empty();
    }
    final Boolean boolValue = Boolean.valueOf(value);
    return Optional.of(boolValue);
  }

  @Override
  public OptionalInt getInt(final String name) {
    final String value = values.get(name);
    if (value == null) {
      return OptionalInt.empty();
    }
    try {
      final int intValue = Integer.parseInt(value);
      return OptionalInt.of(intValue);
    } catch (final NumberFormatException e) {
      throw new IllegalStateException("Config value for " + name + " is not a integer value");
    }
  }

  @Override
  public OptionalDouble getDouble(final String name) {
    final String value = values.get(name);
    if (value == null) {
      return OptionalDouble.empty();
    }
    try {
      final double doubleValue = Double.parseDouble(value);
      return OptionalDouble.of(doubleValue);
    } catch (final NumberFormatException e) {
      throw new IllegalStateException("Config value for " + name + " is not a float value");
    }
  }

  @Override
  public Optional<Instant> getInstant(final String name) {
    return getString(name)
        .map(Instant::parse);
  }

  public static DatasetConfiguration none() {
    return new StaticDatasetConfiguration(Collections.emptyMap());
  }

}
