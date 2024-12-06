package de.fhg.iese.oneviewmed.backend.data.service.fhir;

import java.util.Collection;
import java.util.Optional;
import org.hl7.fhir.r4.model.Procedure;

public interface FhirProcedureService {

  Collection<Procedure> getCompletedSurgeriesForPatient(String patientId);

  boolean isMainSurgery(Procedure procedure);

  Optional<Long> getDaysSince(Procedure procedure);

}
