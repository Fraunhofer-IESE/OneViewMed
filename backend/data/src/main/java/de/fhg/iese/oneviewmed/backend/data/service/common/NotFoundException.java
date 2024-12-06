package de.fhg.iese.oneviewmed.backend.data.service.common;

import java.io.Serial;

public class NotFoundException
    extends Exception {

  @Serial
  private static final long serialVersionUID = 2785432486489563117L;

  public NotFoundException(final String message) {
    super(message);
  }

}
