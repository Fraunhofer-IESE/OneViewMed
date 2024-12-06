package de.fhg.iese.oneviewmed.backend.data.service.dashboard;

import de.fhg.iese.oneviewmed.backend.data.entity.dataset.DatasetType;
import de.fhg.iese.oneviewmed.backend.data.entity.dataset.StaticDataset;
import de.fhg.iese.oneviewmed.backend.data.entity.layout.Dashboard;
import de.fhg.iese.oneviewmed.backend.data.entity.layout.Group;
import de.fhg.iese.oneviewmed.backend.data.entity.layout.Orientation;
import de.fhg.iese.oneviewmed.backend.data.entity.layout.StaticDashboard;
import de.fhg.iese.oneviewmed.backend.data.entity.layout.StaticGroup;
import de.fhg.iese.oneviewmed.backend.data.entity.layout.StaticTile;
import de.fhg.iese.oneviewmed.backend.data.entity.layout.Tile;
import de.fhg.iese.oneviewmed.backend.data.entity.layout.visualization.StaticKeyValueEntry;
import de.fhg.iese.oneviewmed.backend.data.entity.layout.visualization.StaticKeyValueList;
import de.fhg.iese.oneviewmed.backend.data.entity.layout.visualization.StaticTable;
import de.fhg.iese.oneviewmed.backend.data.entity.layout.visualization.StaticTableColumn;
import de.fhg.iese.oneviewmed.backend.data.entity.layout.visualization.StaticVisualization;
import de.fhg.iese.oneviewmed.backend.data.entity.layout.visualization.VisualizationType;
import de.fhg.iese.oneviewmed.backend.data.service.common.NotFoundException;
import de.fhg.iese.oneviewmed.backend.data.service.dataset.resolver.impl.DatasetNames;
import de.fhg.iese.oneviewmed.backend.data.service.patient.PatientsService;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultDashboardService implements DashboardService {

  private static final Duration LONG_REFRESH_INTERVAL = Duration.ofHours(1L);
  private static final Duration SHORT_REFRESH_INTERVAL = Duration.ofMinutes(3L);

  private final PatientsService patientsService;

  private static Tile patientInformationTile() {
    return StaticTile.builder()
        .id("d0f8a7e8-2789-4249-9a29-ae9f42094d2b")
        .orientations(Set.of(
            Orientation.HORIZONTAL,
            Orientation.SQUARE))
        .minWidth(6)
        .minHeight(1)
        .title("Allgemeines")
        .dataset(StaticDataset
            .builder()
            .name(DatasetNames.PATIENT_INFORMATION)
            .type(DatasetType.TABLE)
            .build())
        .visualization(StaticVisualization
            .builder()
            .type(VisualizationType.KEY_VALUE_LIST)
            .keyValueList(StaticKeyValueList
                .builder()
                .entries(List.of(
                    new StaticKeyValueEntry(
                        "height",
                        "Größe"),
                    new StaticKeyValueEntry(
                        "weight",
                        "Gewicht"),
                    new StaticKeyValueEntry(
                        "communicationLanguage",
                        "Sprache"),
                    new StaticKeyValueEntry(
                        "therapyLimitation",
                        "Therapiebegrenzung"),
                    new StaticKeyValueEntry(
                        "patientProvision",
                        "Patientenverfügung"),
                    new StaticKeyValueEntry(
                        "care",
                        "Betreuung")
                ))
                .build())
            .build())
        .refreshInterval(LONG_REFRESH_INTERVAL)
        .build();
  }

  private static Tile emergencyContactsTile() {
    return StaticTile.builder()
        .id("f1d0e9e19-18fb-46fc-bcf1-ef84080f0de3")
        .orientations(Set.of(
            Orientation.HORIZONTAL,
            Orientation.SQUARE))
        .minWidth(6)
        .minHeight(1)
        .title("Notfallkontakt")
        .dataset(StaticDataset
            .builder()
            .name(DatasetNames.EMERGENCY_CONTACTS)
            .type(DatasetType.TABLE)
            .build())
        .visualization(StaticVisualization
            .builder()
            .type(VisualizationType.TABLE)
            .table(StaticTable
                .builder()
                .columns(List.of(
                    StaticTableColumn.builder()
                        .title("Name")
                        .name("name")
                        .build(),
                    StaticTableColumn.builder()
                        .title("Rolle")
                        .name("role")
                        .build()))
                .headerVisible(false)
                .build())
            .build())
        .refreshInterval(LONG_REFRESH_INTERVAL)
        .build();
  }

  private static Tile practitionersTile() {
    return StaticTile.builder()
        .id("3de04e19-fec5-4820-aac4-f39e0a1b4348")
        .orientations(Set.of(
            Orientation.HORIZONTAL,
            Orientation.SQUARE))
        .minWidth(6)
        .minHeight(1)
        .title("Behandelnde")
        .dataset(StaticDataset
            .builder()
            .name(DatasetNames.PRACTITIONERS)
            .type(DatasetType.TABLE)
            .build())
        .visualization(StaticVisualization
            .builder()
            .type(VisualizationType.TABLE)
            .table(StaticTable
                .builder()
                .columns(List.of(
                    StaticTableColumn.builder()
                        .title("Name")
                        .name("name")
                        .build(),
                    StaticTableColumn.builder()
                        .title("Rolle")
                        .name("role")
                        .build()))
                .headerVisible(false)
                .build())
            .build())
        .refreshInterval(LONG_REFRESH_INTERVAL)
        .build();
  }

  private static Tile infectionsTile() {
    return StaticTile.builder()
        .id("72e6fd3a-7e46-403a-9d76-00155858a12f")
        .orientations(Set.of(
            Orientation.HORIZONTAL,
            Orientation.SQUARE))
        .minWidth(4)
        .minHeight(1)
        .title("Infektionen")
        .dataset(StaticDataset
            .builder()
            .name(DatasetNames.LABORATORY_RESULTS)
            .configuration(Map.of("onlyInfectious", Boolean.TRUE))
            .type(DatasetType.TABLE)
            .build())
        .visualization(StaticVisualization
            .builder()
            .type(VisualizationType.TABLE)
            .table(StaticTable
                .builder()
                .columns(List.of(
                    StaticTableColumn.builder()
                        .title("Name")
                        .name("name")
                        .build()))
                .headerVisible(false)
                .build())
            .build())
        .refreshInterval(LONG_REFRESH_INTERVAL)
        .build();
  }

  private static Tile complicationsTile() {
    return StaticTile.builder()
        .id("8fe3f5c3-3bc0-4693-b6c9-4c1e676131bb")
        .orientations(Set.of(
            Orientation.HORIZONTAL,
            Orientation.SQUARE))
        .minWidth(4)
        .minHeight(1)
        .title("Komplikationen")
        .dataset(StaticDataset
            .builder()
            .name(DatasetNames.COMPLICATIONS)
            .type(DatasetType.VALUE)
            .build())
        .visualization(StaticVisualization
            .builder()
            .type(VisualizationType.VALUE)
            .build())
        .refreshInterval(SHORT_REFRESH_INTERVAL)
        .build();
  }

  private static Tile allergiesTile() {
    return StaticTile.builder()
        .id("e0236d05-9a6d-45ae-a8a1-dea4a86e434a")
        .orientations(Set.of(
            Orientation.HORIZONTAL,
            Orientation.SQUARE))
        .minWidth(4)
        .minHeight(1)
        .title("Allergien und Unverträglichkeiten")
        .dataset(StaticDataset
            .builder()
            .name(DatasetNames.ALLERGY_INTOLERANCE)
            .type(DatasetType.TABLE)
            .build())
        .visualization(StaticVisualization
            .builder()
            .type(VisualizationType.KEY_VALUE_LIST)
            .keyValueList(StaticKeyValueList.builder()
                .entries(
                    List.of(
                        new StaticKeyValueEntry(
                            "allergies",
                            "Allergien"),
                        new StaticKeyValueEntry(
                            "intolerances",
                            "Unverträglichkeiten")))
                .build())
            .build())
        .refreshInterval(LONG_REFRESH_INTERVAL)
        .build();
  }

  private static Tile diagnoseTile() {
    return StaticTile.builder()
        .id("f08d4d39-ba62-4351-8b40-1463540ae420")
        .orientations(Set.of(
            Orientation.HORIZONTAL,
            Orientation.SQUARE))
        .minWidth(FULL_WIDTH)
        .minHeight(1)
        .title("Diagnose")
        .dataset(StaticDataset
            .builder()
            .name(DatasetNames.DIAGNOSIS)
            .type(DatasetType.TABLE)
            .build())
        .visualization(StaticVisualization
            .builder()
            .type(VisualizationType.TABLE)
            .table(StaticTable
                .builder()
                .columns(List.of(
                    StaticTableColumn.builder()
                        .title("Diagnose")
                        .name("code")
                        .build(),
                    StaticTableColumn.builder()
                        .title("Kategorie")
                        .name("category")
                        .build(),
                    StaticTableColumn.builder()
                        .title("Datum")
                        .name("recorded")
                        .build()
                ))
                .headerVisible(true)
                .build())
            .build())
        .refreshInterval(SHORT_REFRESH_INTERVAL)
        .build();
  }

  private static Tile laboratoryResultsTile() {
    return StaticTile.builder()
        .id("4fec2de1-ad21-4b1c-8c1e-ccccd1ecc519")
        .orientations(Set.of(
            Orientation.HORIZONTAL,
            Orientation.SQUARE))
        .minWidth(FULL_WIDTH)
        .minHeight(1)
        .title("Laborergebnisse")
        .dataset(StaticDataset
            .builder()
            .name(DatasetNames.LABORATORY_RESULTS)
            .type(DatasetType.TABLE)
            .build())
        .visualization(StaticVisualization
            .builder()
            .type(VisualizationType.TABLE)
            .table(StaticTable
                .builder()
                .columns(List.of(
                    StaticTableColumn.builder()
                        .title("Werttyp")
                        .name("name")
                        .build(),
                    StaticTableColumn.builder()
                        .title("Wert")
                        .name("value")
                        .build(),
                    StaticTableColumn.builder()
                        .title("Datum")
                        .name("issued")
                        .build(),
                    StaticTableColumn.builder()
                        .title("Status")
                        .name("status")
                        .build()
                ))
                .headerVisible(true)
                .build())
            .build())
        .refreshInterval(SHORT_REFRESH_INTERVAL)
        .build();
  }

  private static Tile eventsAndSurgeriesTile() {
    return StaticTile.builder()
        .id("c24ad44f-68ed-4f70-b302-8f44f6ee02f3")
        .orientations(Set.of(
            Orientation.HORIZONTAL,
            Orientation.SQUARE))
        .minWidth(FULL_WIDTH)
        .minHeight(1)
        .title("Ereignisse und Operationen")
        .dataset(StaticDataset
            .builder()
            .name(DatasetNames.SURGERIES)
            .type(DatasetType.TABLE)
            .build())
        .visualization(StaticVisualization
            .builder()
            .type(VisualizationType.TABLE)
            .table(StaticTable
                .builder()
                .headerVisible(true)
                .columns(List.of(
                    StaticTableColumn.builder()
                        .title("Ereignis")
                        .name("code")
                        .linkName("report")
                        .build(),
                    StaticTableColumn.builder()
                        .title("Kategorie")
                        .name("category")
                        .build(),
                    StaticTableColumn.builder()
                        .title("Datum")
                        .name("performed")
                        .build()))
                .build())
            .build())
        .refreshInterval(LONG_REFRESH_INTERVAL)
        .build();
  }

  private static Group overviewGroup() {
    return StaticGroup.builder()
        .title("Übersicht")
        .tiles(List.of(
            patientInformationTile(),
            emergencyContactsTile(),
            practitionersTile(),
            infectionsTile(),
            complicationsTile(),
            allergiesTile(),
            diagnoseTile(),
            eventsAndSurgeriesTile(),
            laboratoryResultsTile()
        ))
        .build();
  }

  private static Tile medicationStatementTile() {
    return StaticTile.builder()
        .id("f62f69d2-80c2-40a5-9809-2d7200bdb569")
        .orientations(Set.of(
            Orientation.HORIZONTAL,
            Orientation.SQUARE))
        .minWidth(6)
        .minHeight(1)
        .title("Aufnahmemedikation")
        .dataset(StaticDataset
            .builder()
            .name(DatasetNames.MEDICATION_STATEMENT)
            .type(DatasetType.TABLE)
            .build())
        .visualization(StaticVisualization
            .builder()
            .type(VisualizationType.TABLE)
            .table(StaticTable
                .builder()
                .headerVisible(true)
                .columns(List.of(
                    StaticTableColumn
                        .builder()
                        .title("Medikament")
                        .name("medicationName")
                        .build(),
                    StaticTableColumn
                        .builder()
                        .title("Dosierung")
                        .name("dosageInstructionText")
                        .build())
                )
                .build())
            .build())
        .refreshInterval(LONG_REFRESH_INTERVAL)
        .build();
  }

  private static Tile medicationActiveTile() {
    return StaticTile.builder()
        .id("336b7927-357d-4916-9a50-4756188fa063")
        .orientations(Set.of(
            Orientation.HORIZONTAL,
            Orientation.SQUARE))
        .minWidth(6)
        .minHeight(1)
        .title("Aktuelle Medikation")
        .dataset(StaticDataset
            .builder()
            .name(DatasetNames.ACTIVE_MEDICATION)
            .type(DatasetType.TABLE)
            .build())
        .visualization(StaticVisualization
            .builder()
            .type(VisualizationType.TABLE)
            .table(StaticTable
                .builder()
                .headerVisible(true)
                .columns(List.of(
                    StaticTableColumn
                        .builder()
                        .title("Medikament")
                        .name("medicationName")
                        .build(),
                    StaticTableColumn
                        .builder()
                        .title("Dosierung")
                        .name("dosageInstructionText")
                        .build())

                )
                .build())
            .build())
        .refreshInterval(LONG_REFRESH_INTERVAL)
        .build();
  }

  private static Tile medicationChronologyTile() {
    return StaticTile.builder()
        .id("e60afc10-f979-4fc7-b75c-7029fc56169a")
        .orientations(Set.of(
            Orientation.HORIZONTAL,
            Orientation.SQUARE))
        .minWidth(FULL_WIDTH)
        .minHeight(1)
        .title("Chronologie")
        .dataset(StaticDataset
            .builder()
            .name(DatasetNames.MEDICATION_CHRONOLOGY)
            .type(DatasetType.TABLE)
            .build())
        .visualization(StaticVisualization
            .builder()
            .type(VisualizationType.MEDICATION_CHRONOLOGY)
            .build())
        .refreshInterval(LONG_REFRESH_INTERVAL)
        .build();
  }

  private static Group medicationGroup() {
    return StaticGroup.builder()
        .title("Medikation")
        .tiles(List.of(
            medicationStatementTile(),
            medicationActiveTile(),
            medicationChronologyTile()
        ))
        .build();

  }

  private static Group vitalDataGroup() {
    return StaticGroup.builder()
        .title("Vitaldaten")
        .tiles(List.of(
            StaticTile.builder()
                .id("ae356cbe-7ccb-492f-8583-857baea96bcf")
                .orientations(Set.of(
                    Orientation.HORIZONTAL,
                    Orientation.SQUARE))
                .minWidth(FULL_WIDTH)
                .minHeight(1)
                .title("Letzte Vitalparameter")
                .dataset(StaticDataset
                    .builder()
                    .name(DatasetNames.VITAL_SIGNS)
                    .type(DatasetType.TABLE)
                    .build())
                .visualization(StaticVisualization
                    .builder()
                    .type(VisualizationType.TABLE)
                    .table(StaticTable
                        .builder()
                        .columns(List.of(
                            StaticTableColumn
                                .builder()
                                .title("Datum")
                                .name("date")
                                .build(),
                            StaticTableColumn
                                .builder()
                                .title("Wert")
                                .name("value")
                                .build()))
                        .build())
                    .build())
                .refreshInterval(SHORT_REFRESH_INTERVAL)
                .build()))
        .build();
  }

  private static Group cronologyGroup() {
    return StaticGroup.builder()
        .title("Chronologie")
        .tiles(List.of(
            StaticTile.builder()
                .id("d61de7b9-cf94-41c0-89f5-cc6ef6a63659")
                .orientations(Set.of(Orientation.HORIZONTAL,
                    Orientation.SQUARE))
                .minWidth(FULL_WIDTH)
                .minHeight(1)
                .title("Chronologie")
                .dataset(
                    StaticDataset
                        .builder()
                        .name(DatasetNames.CHRONOLOGY)
                        .type(DatasetType.TABLE)
                        .build())
                .visualization(
                    StaticVisualization
                        .builder()
                        .type(VisualizationType.CHRONOLOGY)
                        .build())
                .refreshInterval(LONG_REFRESH_INTERVAL)
                .build()))
        .build();
  }

  private static Group documentsGroup() {
    return StaticGroup.builder()
        .title("Dokumente")
        .tiles(List.of(
            StaticTile.builder()
                .id("52b068da-e34b-4007-a5a8-65f1f53608b2")
                .orientations(Set.of(Orientation.HORIZONTAL,
                    Orientation.SQUARE))
                .minWidth(FULL_WIDTH)
                .minHeight(1)
                .title("Dokumente")
                .dataset(
                    StaticDataset
                        .builder()
                        .name(DatasetNames.DOCUMENTS)
                        .type(DatasetType.TABLE)
                        .build())
                .visualization(
                    StaticVisualization
                        .builder()
                        .type(VisualizationType.TABLE)
                        .table(
                            StaticTable.builder()
                                .headerVisible(true)
                                .columns(List.of(
                                    StaticTableColumn
                                        .builder()
                                        .title("Titel")
                                        .name("title")
                                        .linkName("url")
                                        .build(),
                                    StaticTableColumn
                                        .builder()
                                        .title("Typ")
                                        .name("type")
                                        .build(),
                                    StaticTableColumn
                                        .builder()
                                        .title("Fachrichtung")
                                        .name("field")
                                        .build(),
                                    StaticTableColumn
                                        .builder()
                                        .title("Erstellungsdatum")
                                        .name("date")
                                        .build()))
                                .build())
                        .build())
                .refreshInterval(LONG_REFRESH_INTERVAL)
                .build()))
        .build();
  }

  @Override
  public Dashboard getDashboard(@Nullable final String patientId) throws NotFoundException {

    final String title = (patientId == null)
        ? "Übersicht"
        : ("Übersicht für " + patientsService.getPatient(patientId).getFullName());

    return StaticDashboard.builder()
        .title(title)
        .groups(List.of(
            overviewGroup(),
            medicationGroup(),
            vitalDataGroup(),
            cronologyGroup(),
            documentsGroup()))
        .build();
  }

}
