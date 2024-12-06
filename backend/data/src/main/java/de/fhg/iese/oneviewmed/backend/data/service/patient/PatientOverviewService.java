package de.fhg.iese.oneviewmed.backend.data.service.patient;

import de.fhg.iese.oneviewmed.backend.data.entity.PatientOverview;
import de.fhg.iese.oneviewmed.backend.data.service.common.NotFoundException;
import java.util.concurrent.ExecutionException;

public interface PatientOverviewService {

  PatientOverview getPatientOverview(String patientId)
      throws NotFoundException, ExecutionException, InterruptedException;

}
