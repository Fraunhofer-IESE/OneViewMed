package de.fhg.iese.oneviewmed.backend.data.entity.dataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

@EqualsAndHashCode
public class StaticTableDataset
    implements TableDataset {

  private final Map<String, ValueType> columns;

  private final List<Map<String, Object>> values = new ArrayList<>();

  public StaticTableDataset(final Map<String, ValueType> columns) {
    this.columns = Map.copyOf(columns);
  }

  @Override
  public Collection<String> getColumnNames() {
    return columns.keySet();
  }

  @Override
  public ValueType getColumnType(final String name) {
    final ValueType type = columns.get(name);
    if (type == null) {
      throw new IllegalArgumentException("Column " + name + " doesn't exist");
    }
    return type;
  }

  @Override
  public List<Map<String, Object>> getValues() {
    return Collections.unmodifiableList(values);
  }

  private void checkType(@Nullable final Object value, final ValueType type) {
    if (value == null) {
      return;
    }
    if (!type.matches(value)) {
      throw new IllegalArgumentException("Invalid value " + value + " for column type " + type);
    }
  }

  public void addRow(final Map<String, Object> row) {
    row.forEach((name, value) -> {
      final ValueType type = columns.get(name);
      if (type == null) {
        throw new IllegalArgumentException("Column " + name + " is not defined");
      }
      checkType(value, type);
    });
    values.add(row);
  }

  @Override
  public String toString() {
    return "StaticTableDataset(rows=" + values.size() + ')';
  }

}
