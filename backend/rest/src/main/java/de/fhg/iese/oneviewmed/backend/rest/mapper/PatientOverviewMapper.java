package de.fhg.iese.oneviewmed.backend.rest.mapper;

import de.fhg.iese.oneviewmed.backend.data.entity.PatientOverview;
import de.fhg.iese.oneviewmed.backend.data.entity.StationStay;
import de.fhg.iese.oneviewmed.backend.rest.dto.PatientOverviewDto;
import de.fhg.iese.oneviewmed.backend.rest.dto.StationStayDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PatientOverviewMapper {

  StationStayDto toDto(StationStay stationStay);

  PatientOverviewDto toDto(PatientOverview patientOverview);

}
