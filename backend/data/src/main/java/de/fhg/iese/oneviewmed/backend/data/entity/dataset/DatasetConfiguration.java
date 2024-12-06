package de.fhg.iese.oneviewmed.backend.data.entity.dataset;

import java.time.Instant;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public interface DatasetConfiguration {

  Optional<String> getString(String name);

  Optional<Boolean> getBool(String name);

  OptionalInt getInt(String name);

  OptionalDouble getDouble(String name);

  Optional<Instant> getInstant(String name);

}
