package de.fhg.iese.oneviewmed.backend.data.service.patient;

import de.fhg.iese.oneviewmed.backend.data.entity.Patient;
import de.fhg.iese.oneviewmed.backend.data.service.common.NotFoundException;
import java.util.Collection;

public interface PatientsService {

  Collection<Patient> getPatients();

  Patient getPatient(String id)
      throws NotFoundException;

}
