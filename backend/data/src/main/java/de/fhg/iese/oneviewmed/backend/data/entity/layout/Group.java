package de.fhg.iese.oneviewmed.backend.data.entity.layout;

import java.util.Collection;

public interface Group {

  String getTitle();

  Collection<Tile> getTiles();

}
