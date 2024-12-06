package de.fhg.iese.oneviewmed.backend.data.entity.layout;

import java.util.Collection;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StaticDashboard
    implements Dashboard {

  private final String title;

  private final Collection<Group> groups;

}
