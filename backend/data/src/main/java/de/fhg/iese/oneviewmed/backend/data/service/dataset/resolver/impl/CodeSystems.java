package de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl;

public final class CodeSystems {

  public static final String OBSERVATION_CATEGORY =
      "http://terminology.hl7.org/CodeSystem/observation-category";

  public static final String CONTACT_ROLE = "http://terminology.hl7.org/CodeSystem/v2-0131";

  public static final String CONTACT_ROLE_EMERGENCY_CONTACT = "C";

  public static final String ROLE_CODE = "http://terminology.hl7.org/CodeSystem/v3-RoleCode";

  public static final String ROLE_CODE_GUARD = "GUARD";

  public static final String IDENTIFIER_TYPE = "http://terminology.hl7.org/CodeSystem/v2-0203";

  public static final String IDENTIFIER_TYPE_VISIT_NUMBER = "VN";

  public static final String PROCEDURE_CATEGORY = "http://hl7.org/fhir/ValueSet/procedure-category";

  public static final String PROCEDURE_CATEGORY_SURGICAL_PROCEDURE = "387713003";

  public static final String ENCOUNTER_CLASS =
      "http://terminology.hl7.org/ValueSet/encounter-class";

  public static final String ENCOUNTER_CLASS_INPATIENT_ENCOUNTER = "IMP";

  public static final String ENCOUNTER_CLASS_INPATIENT_NON_ACUTE = "NONAC";

  public static final String ENCOUNTER_TYPE_CONTACT_LEVEL = "https://www.medizininformatik-initiative.de/fhir/core/modul-fall/CodeSystem/Kontaktebene";

  public static final String ENCOUNTER_TYPE_LEVEL_HOSPITAL = "einrichtungskontakt";

  public static final String ENCOUNTER_TYPE_LEVEL_STATION = "abteilungskontakt";

  public static final String CONSENT_CATEGORY =
      "http://terminology.hl7.org/CodeSystem/consentcategorycodes";

  public static final String CONSENT_CATEGORY_DO_NOT_RESUSCITATE = "dnr";
  public static final String CONSENT_CATEGORY_HEALTH_CARE_DIRECTIVE = "hcd";
  public static final String CONSENT_CATEGORY_POLST = "polst";

  public static final String SNOMED_SCT = "http://snomed.info/sct";

  public static final String LOINC = "http://loinc.org";

  public static final String LOINC_BODY_WEIGHT = "29463-7";

  public static final String LOINC_BODY_HEIGHT = "8302-2";

  public static final String DIAGNOSIS_ROLE =
      "http://terminology.hl7.org/CodeSystem/diagnosis-role";

  public static final String PRINCIPAL_DIAGNOSIS = "CC";

  private CodeSystems() {
  }

}
