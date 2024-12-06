package de.fhg.iese.oneviewmed.backend.data.entity.dataset;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
public class StaticValueDataset
    implements ValueDataset {

  @Nullable
  private final ValueType type;

  @Nullable
  private final Number number;

  private final String text;

  @Nullable
  @Override
  public Number getValueAsNumber() {
    return number;
  }

  @Override
  public String getValueAsText() {
    return text;
  }

  @Override
  public ValueType getValueType() {
    return type;
  }
}
