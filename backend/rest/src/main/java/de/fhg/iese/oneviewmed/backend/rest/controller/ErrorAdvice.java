package de.fhg.iese.oneviewmed.backend.rest.controller;

import ca.uhn.fhir.rest.client.exceptions.FhirClientConnectionException;
import ca.uhn.fhir.rest.server.exceptions.ResourceGoneException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import de.fhg.iese.oneviewmed.backend.data.service.common.NotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorAdvice {

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public void handleNotFoundException(final NotFoundException exception,
                                      final HttpServletResponse response)
      throws IOException {
    if (log.isWarnEnabled()) {
      log.warn("{}", exception.getMessage(), exception);
    }
    response.sendError(HttpStatus.NOT_FOUND.value());
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(ResourceNotFoundException.class)
  public void handleResourceNotFoundException(final ResourceNotFoundException exception,
                                              final HttpServletResponse response)
      throws IOException {
    if (log.isWarnEnabled()) {
      log.warn("{}", exception.getMessage(), exception);
    }
    response.sendError(HttpStatus.NOT_FOUND.value());
  }

  @ResponseStatus(HttpStatus.GONE)
  @ExceptionHandler(ResourceGoneException.class)
  public void handleResourceGoneException(final ResourceGoneException exception,
                                          final HttpServletResponse response)
      throws IOException {
    if (log.isWarnEnabled()) {
      log.warn("{}", exception.getMessage(), exception);
    }
    response.sendError(HttpStatus.GONE.value());
  }

  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  @ExceptionHandler(FhirClientConnectionException.class)
  public void handleFhirClientConnectionException(final FhirClientConnectionException exception,
                                                  final HttpServletResponse response)
      throws IOException {
    if (log.isWarnEnabled()) {
      log.warn("{}", exception.getMessage(), exception);
    }
    response.sendError(HttpStatus.SERVICE_UNAVAILABLE.value());
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({InterruptedException.class, ExecutionException.class})
  public void handleConcurrentException(final Exception exception,
                                        final HttpServletResponse response)
      throws IOException {
    if (log.isWarnEnabled()) {
      log.warn("{}", exception.getMessage(), exception);
    }
    response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

}
