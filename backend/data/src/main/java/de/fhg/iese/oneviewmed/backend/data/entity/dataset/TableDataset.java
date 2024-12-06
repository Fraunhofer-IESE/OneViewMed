package de.fhg.iese.oneviewmed.backend.data.entity.dataset;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TableDataset {

  Collection<String> getColumnNames();

  ValueType getColumnType(String name);

  List<Map<String, Object>> getValues();

}
