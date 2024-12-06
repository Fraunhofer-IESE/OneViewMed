package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;

import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetConfiguration;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticTableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.TableDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.ValueType;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.api.TableDatasetResolver;
import de.fhg.iese.oneviewmed.backend.data.service.fhir.FhirDocumentService;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.DocumentReference;
import org.springframework.stereotype.Component;

@Component
public class FhirDocumentsDatasetResolver
    extends AbstractDatasetResolver
    implements TableDatasetResolver {

  private final FhirDocumentService fhirDocumentService;

  protected FhirDocumentsDatasetResolver(final FhirDocumentService fhirDocumentService) {
    super(DatasetNames.DOCUMENTS);
    this.fhirDocumentService = fhirDocumentService;
  }

  @Override
  public TableDataset resolveTableDataset(final String patientId,
                                          final DatasetConfiguration configuration) {
    final StaticTableDataset table = new StaticTableDataset(Map.of(
        "title", ValueType.STRING,
        "url", ValueType.STRING,
        "type", ValueType.STRING,
        "field", ValueType.STRING,
        "date", ValueType.INSTANT
    ));
    fhirDocumentService.getDocuments(patientId)
        .stream()
        .flatMap(documentReference -> documentReference.getContent()
            .stream()
            .map(content -> {
              final Map<String, Object> row = HashMap.newHashMap(5);

              Optional.ofNullable(content.getAttachment())
                  .map(Attachment::getTitle)
                  .ifPresent(title -> row.put("title", title));

              Optional.ofNullable(content.getAttachment())
                  .map(Attachment::getUrl)
                  .ifPresent(url -> row.put("url", url));

              Optional.ofNullable(documentReference.getType())
                  .map(HumanReadableHelper::toString)
                  .ifPresent(type -> row.put("type", type));

              Optional.ofNullable(documentReference.getContext())
                  .map(DocumentReference.DocumentReferenceContextComponent::getPracticeSetting)
                  .map(HumanReadableHelper::toString)
                  .ifPresent(type -> row.put("field", type));

              Optional.ofNullable(documentReference.getDate())
                  .map(Date::toInstant)
                  .ifPresent(date -> row.put("date", date));

              return row;
            }))
        .forEach(table::addRow);
    return table;
  }

}
