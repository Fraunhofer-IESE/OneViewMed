package de.fhg.iese.oneviewmed.backend.data.service.dashboard;

import de.fhg.iese.oneviewmed.backend.data.entity.layout.Dashboard;
import de.fhg.iese.oneviewmed.backend.data.service.common.NotFoundException;
import org.springframework.lang.Nullable;

public interface DashboardService {

  int FULL_WIDTH = 12;

  Dashboard getDashboard(@Nullable String patientId)
      throws NotFoundException;

}
