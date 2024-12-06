package de.fhg.iese.oneviewmed.backend.data.service.fhir;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Location;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultFhirLocationService implements FhirLocationService {
  private final FhirService fhirService;

  @Override
  public Location getLocationById(final String id) {
    return fhirService.getResourceById(Location.class, id);
  }
}
