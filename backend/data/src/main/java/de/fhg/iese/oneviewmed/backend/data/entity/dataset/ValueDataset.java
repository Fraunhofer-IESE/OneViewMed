package de.fhg.iese.oneviewmed.backend.data.entity.dataset;

import org.springframework.lang.Nullable;

public interface ValueDataset {

  @Nullable
  ValueType getValueType();

  @Nullable
  Number getValueAsNumber();

  @Nullable
  default String getValueAsText() {
    return String.valueOf(getValueAsNumber());
  }

}
