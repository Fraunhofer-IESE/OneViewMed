package de.fhg.iese.oneviewmed.backend.data.entity.layout.visualization;

import java.util.List;

public interface Table {

  List<TableColumn> getColumns();

  boolean isHeaderVisible();

}
