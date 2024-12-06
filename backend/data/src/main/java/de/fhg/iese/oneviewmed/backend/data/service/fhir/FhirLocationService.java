package de.fhg.iese.oneviewmed.backend.data.service.fhir;

import org.hl7.fhir.r4.model.Location;

public interface FhirLocationService {
  Location getLocationById(String id);
}
