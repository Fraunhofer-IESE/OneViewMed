package de.fhg.iese.oneviewmed.backend.rest.mapper;

import de.fhg.iese.oneviewmed.backend.data.entity.Patient;
import de.fhg.iese.oneviewmed.backend.rest.dto.PatientDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PatientMapper {

  @BeanMapping(ignoreUnmappedSourceProperties = "fullName")
  PatientDto toDto(Patient patient);

}
