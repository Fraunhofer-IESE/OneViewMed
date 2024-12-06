package de.fhg.iese.oneviewmed.backend.data.service.fhir;

import java.util.List;
import org.hl7.fhir.r4.model.DocumentReference;

public interface FhirDocumentService {

  List<DocumentReference> getDocuments(String patientId);

  DocumentReference getDocumentById(String id);
}
