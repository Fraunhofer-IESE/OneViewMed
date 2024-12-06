package de.fhg.iese.oneviewmed.backend.rest.controller;

import de.fhg.iese.oneviewmed.backend.data.service.common.NotFoundException;
import de.fhg.iese.oneviewmed.backend.data.service.patient.PatientOverviewService;
import de.fhg.iese.oneviewmed.backend.rest.dto.PatientOverviewDto;
import de.fhg.iese.oneviewmed.backend.rest.mapper.PatientOverviewMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "patient-overview"
)
@RestController
@RequiredArgsConstructor
public class PatientOverviewController {

  private final PatientOverviewService patientOverviewService;

  private final PatientOverviewMapper patientOverviewMapper;

  @Operation(
      summary = "Get patient overview",
      description = "Returns a medical overview for a patient"
  )
  @GetMapping("patient-overview")
  public PatientOverviewDto getPatientOverview(
      @Parameter @RequestHeader(HeaderNames.PATIENT_ID) final String patientId)
      throws NotFoundException, ExecutionException, InterruptedException {
    return patientOverviewMapper.toDto(patientOverviewService.getPatientOverview(patientId));
  }

}
