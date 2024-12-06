package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;

import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.DatasetResolver;

public abstract class AbstractDatasetResolver
    implements DatasetResolver {

  private final String name;

  protected AbstractDatasetResolver(final String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

}
