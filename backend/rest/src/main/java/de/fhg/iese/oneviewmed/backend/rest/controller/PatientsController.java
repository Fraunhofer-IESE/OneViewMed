package de.fhg.iese.oneviewmed.backend.rest.controller;

import de.fhg.iese.oneviewmed.backend.data.service.common.NotFoundException;
import de.fhg.iese.oneviewmed.backend.data.service.patient.PatientsService;
import de.fhg.iese.oneviewmed.backend.rest.dto.PatientDto;
import de.fhg.iese.oneviewmed.backend.rest.mapper.PatientMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "patients"
)
@RestController
@RequiredArgsConstructor
public class PatientsController {

  private final PatientsService patientsService;

  private final PatientMapper patientMapper;

  @Operation(
      summary = "List patients",
      description = "Returns information about all available patients"
  )
  @GetMapping("patients")
  public Collection<PatientDto> listPatients() {
    return patientsService.getPatients()
        .stream()
        .map(patientMapper::toDto)
        .toList();
  }

  @Operation(
      summary = "Get patient",
      description = "Returns information about the patient"
  )
  @GetMapping("patient")
  public PatientDto getPatient(
      @Parameter @RequestHeader(HeaderNames.PATIENT_ID) final String patientId)
      throws NotFoundException {
    return patientMapper.toDto(patientsService.getPatient(patientId));
  }

}
