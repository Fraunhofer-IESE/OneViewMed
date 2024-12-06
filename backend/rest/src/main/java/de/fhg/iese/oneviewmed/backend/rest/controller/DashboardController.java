package de.fhg.iese.oneviewmed.backend.rest.controller;

import de.fhg.iese.oneviewmed.backend.data.service.common.NotFoundException;
import de.fhg.iese.oneviewmed.backend.data.service.dashboard.DashboardService;
import de.fhg.iese.oneviewmed.backend.rest.dto.layout.DashboardDto;
import de.fhg.iese.oneviewmed.backend.rest.mapper.DashboardMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "dashboard"
)
@RequestMapping("dashboard")
@RestController
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;

  private final DashboardMapper dashboardMapper;

  @Operation(
      summary = "Get dashboard",
      description = "Returns a dashboard"
  )
  @GetMapping
  public DashboardDto getDashboard(
      @Parameter @RequestHeader(value = HeaderNames.PATIENT_ID, required = false)
      final String patientId)
      throws NotFoundException {
    return dashboardMapper.toDto(dashboardService.getDashboard(patientId));
  }

}
