package de.fhg.iese.oneviewmed.backend.data.service.fhir;

import org.hl7.fhir.r4.model.ICoding;

public interface FhirMedicalInformationService {

  boolean isInfectious(ICoding coding);

}
