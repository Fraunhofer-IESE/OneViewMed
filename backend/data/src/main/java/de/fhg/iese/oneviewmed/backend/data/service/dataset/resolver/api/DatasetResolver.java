package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api;

import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetType;

public interface DatasetResolver {

  String getName();

  boolean supports(DatasetType type);

}
