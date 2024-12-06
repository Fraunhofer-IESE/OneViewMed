package de.fhg.iese.oneviewmed.backend.data.entity.dataset;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;

public enum ValueType {
    STRING(String.class),
    INTEGER(Integer.class),
    DOUBLE(Double.class),
    BOOLEAN(Boolean.class),
    INSTANT(Instant.class),
    DURATION(Duration.class),
    COLLECTION(Collection.class);

    private final Class<?> type;

    ValueType(final Class<?> type) {
      this.type = type;
    }

    boolean matches(final Object value) {
      return type.isAssignableFrom(value.getClass());
    }
}
