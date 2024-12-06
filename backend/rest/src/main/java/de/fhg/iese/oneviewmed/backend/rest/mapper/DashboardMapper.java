package de.fhg.iese.oneviewmed.backend.rest.mapper;

import de.fhg.iese.oneviewmed.backend.data.entity.layout.Dashboard;
import de.fhg.iese.oneviewmed.backend.rest.dto.layout.DashboardDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.ERROR, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DashboardMapper {

  DashboardDto toDto(Dashboard dashboard);

}
