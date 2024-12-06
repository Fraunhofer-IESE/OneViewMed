package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;


import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetType;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticTableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticValueDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueType;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.TableDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.ValueDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirDocumentService;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirProcedureService;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Component;

@Component
public class FhirSurgeryDatasetResolver
    extends AbstractDatasetResolver
    implements ValueDatasetResolver, TableDatasetResolver {

  private final FhirDocumentService fhirDocumentService;
  private final FhirProcedureService fhirProcedureService;

  public FhirSurgeryDatasetResolver(final FhirDocumentService fhirDocumentService,
                                    final FhirProcedureService fhirProcedureService) {
    super(DatasetNames.SURGERIES);
    this.fhirDocumentService = fhirDocumentService;
    this.fhirProcedureService = fhirProcedureService;
  }

  @Override
  public boolean supports(final DatasetType type) {
    return (type == DatasetType.VALUE) || (type == DatasetType.TABLE);
  }

  private Optional<Procedure> resolveMainSurgery(final String patientId) {
    return fhirProcedureService.getCompletedSurgeriesForPatient(patientId)
        .stream()
        .filter(fhirProcedureService::isMainSurgery)
        .findFirst();
  }

  @Override
  public ValueDataset resolveValueDataset(final String patientId,
                                          final DatasetConfiguration configuration) {
    final String mainSurgery = resolveMainSurgery(patientId)
        .map(procedure -> Optional.ofNullable(HumanReadableHelper.toString(procedure))
            .orElseGet(() -> HumanReadableHelper.toString(procedure.getCode())))
        .orElse(StandardValues.UNKNOWN);

    return StaticValueDataset.builder()
        .text(mainSurgery)
        .type(ValueType.STRING)
        .build();
  }

  @Override
  public TableDataset resolveTableDataset(final String patientId,
                                          final DatasetConfiguration configuration) {
    final Collection<Procedure> completedSurgeries =
        fhirProcedureService.getCompletedSurgeriesForPatient(patientId);
    final StaticTableDataset table = new StaticTableDataset(Map.of(
        "code", ValueType.STRING,
        "category", ValueType.STRING,
        "performed", ValueType.INSTANT,
        "notes", ValueType.COLLECTION,
        "report", ValueType.STRING
    ));
    completedSurgeries.stream()
        .map(procedure -> {
          final Map<String, Object> row = HashMap.newHashMap(5);

          Optional.ofNullable(procedure.getCode())
              .map(HumanReadableHelper::toString)
              .ifPresent(code -> row.put("code", code));

          Optional.ofNullable(procedure.getCategory())
              .map(HumanReadableHelper::toString)
              .ifPresent(category -> row.put("category", category));

          Optional.ofNullable(procedure.getPerformed())
              .map(performed -> {
                if (performed instanceof final DateTimeType dateTime) {
                  return dateTime.getValue().toInstant();
                }
                return HumanReadableHelper.toTimeString(performed);
              })
              .ifPresent(performed -> row.put("performed", performed));

          final Collection<String> notes = procedure.getNote()
              .stream()
              .map(Annotation::getText)
              .toList();
          row.put("notes", notes);

          final Optional<String> optionalReport = procedure.getReport()
              .stream()
              .map(Reference::getReference)
              .filter(reference -> reference.startsWith("DocumentReference"))
              .findFirst()
              .map(fhirDocumentService::getDocumentById)
              .map(DocumentReference::getContentFirstRep)
              .map(DocumentReference.DocumentReferenceContentComponent::getAttachment)
              .map(Attachment::getUrl);

          optionalReport.ifPresent(report -> row.put("report", report));

          return row;
        })
        .forEach(table::addRow);
    return table;
  }

}
