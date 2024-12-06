package de.fhg.iese.oneviewmed.backend.data.entity.layout;

import java.util.Collection;

public interface Dashboard {

  String getTitle();

  Collection<Group> getGroups();

}
