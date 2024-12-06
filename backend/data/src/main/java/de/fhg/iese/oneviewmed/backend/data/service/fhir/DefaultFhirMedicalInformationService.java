package de.fhg.iese.oneviewmed.backend.data.service.fhir;

import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl.CodeSystems;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.ICoding;
import org.springframework.stereotype.Service;

@Service
public class DefaultFhirMedicalInformationService
    implements FhirMedicalInformationService {

  private static final Set<String> REPORTABLE_MICRO_VIRUSES = readReportableVirusesCodes();

  /* Reads LOINC codes for individual viruses that are considered reportable by the
   * Centers for Disease Control and Prevention (CDC) (https://loinc.org/LG103-8)
   */
  private static Set<String> readReportableVirusesCodes() {
    try (
        final InputStream inputStream = DefaultFhirMedicalInformationService.class.getResourceAsStream(
            "/LG103-8.csv");
        final InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        final CsvReader<CsvRecord> csv = CsvReader.builder().ofCsvRecord(reader)) {
      return csv.stream()
          .map(record -> record.getField(0))
          .collect(Collectors.toSet());
    } catch (final IOException e) {
      throw new IllegalStateException("Failed to read viruses from file", e);
    }
  }

  private static boolean matchesAnyCodeOf(final ICoding coding, final String system,
                                          final Collection<String> codes) {
    return coding.hasSystem()
        && Objects.equals(coding.getSystem(), system)
        && coding.hasCode()
        && codes.contains(coding.getCode());
  }

  @Override
  public boolean isInfectious(final ICoding coding) {
    return matchesAnyCodeOf(coding, CodeSystems.LOINC, REPORTABLE_MICRO_VIRUSES);
  }

}
