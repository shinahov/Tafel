package org.example.Aplication;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.Db.BesuchsEintrag;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiConsumer;

public class Kalender extends Application {

    private List<BesuchsEintrag> highlightedDates;
    private BiConsumer<LocalDate, String> onDateSubmit;  // Callback, um das Datum und ggf. die Bemerkung an MainApp zurückzugeben

    // Methode, um den Callback zu setzen
    public void setOnDateSubmit(BiConsumer<LocalDate, String> onDateSubmit) {
        this.onDateSubmit = onDateSubmit;
    }

    @Override
    public void start(Stage primaryStage) {
        start(primaryStage, null); // Ruft die überladene Methode auf
    }

    // Überladene Methode, die eine Liste von Besuchsdaten akzeptiert
    public void start(Stage primaryStage, List<BesuchsEintrag> visitDates) {
        if (visitDates != null) {
            this.highlightedDates = visitDates; // Dynamisch übergebene Daten speichern
        } else {
            this.highlightedDates = List.of(); // Keine Daten zu markieren
        }

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now()); // Setze das aktuelle Datum als Standardwert

        // Highlighting der Tage, die in der Besuchsliste enthalten sind
        Callback<DatePicker, DateCell> dayCellFactory = (final DatePicker picker) -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                // Suche, ob es einen Besuchseintrag für das aktuelle Datum gibt
                for (BesuchsEintrag eintrag : highlightedDates) {
                    if (eintrag.getBesuchsdatum().equals(item)) {
                        this.setStyle("-fx-background-color: #ff9999;"); // Rot färben
                    }
                }
            }
        };
        datePicker.setDayCellFactory(dayCellFactory);

        datePicker.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-pref-width: 300px;" +
                        "-fx-pref-height: 40px;"
        );

        // Checkbox für Bemerkung
        CheckBox bemerkungCheckBox = new CheckBox("Mit Bemerkung");
        bemerkungCheckBox.setStyle("-fx-font-size: 24px;");

        // Button zum Abschicken
        Button submitButton = new Button("Besuch registrieren");
        submitButton.setPrefSize(500, 100);
        submitButton.setStyle("-fx-font-size: 30px;");

        // Aktion, um das Datum über den Callback zurückzugeben
        submitButton.setOnAction(e -> {
            LocalDate selectedDate = datePicker.getValue();  // Hole das ausgewählte Datum

            if (bemerkungCheckBox.isSelected()) {
                // Wenn die Checkbox aktiviert ist, öffne das Bemerkungsfenster
                openBemerkungWindow(selectedDate, primaryStage);
            } else {
                if (onDateSubmit != null) {
                    onDateSubmit.accept(selectedDate, null);  // Rückgabe des Datums ohne Bemerkung an MainApp
                }
                primaryStage.close();
            }
        });

        // Box zum Anzeigen der besuchten Tage und deren Bemerkungen
        VBox visitedDaysBox = new VBox();
        visitedDaysBox.setSpacing(10);
        Label visitedDaysLabel = new Label("Besuchte Tage:");
        visitedDaysLabel.setStyle("-fx-font-size: 30px;");
        visitedDaysBox.getChildren().add(visitedDaysLabel);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        for (BesuchsEintrag eintrag : highlightedDates) {
            // Label für jedes besuchte Datum mit Bemerkung
            Label dateLabel = new Label(eintrag.getBesuchsdatum().format(formatter) +
                    (eintrag.getBemerkung() != null && !eintrag.getBemerkung().isEmpty()
                            ? " - Bemerkung: " + eintrag.getBemerkung() : ""));
            dateLabel.setStyle("-fx-font-size: 24px;");
            visitedDaysBox.getChildren().add(dateLabel);
        }

        VBox vbox = new VBox(datePicker, bemerkungCheckBox, submitButton, visitedDaysBox);
        vbox.setSpacing(20);

        Scene scene = new Scene(vbox, 600, 600);

        primaryStage.setTitle("Kalender mit besuchten Tagen");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openBemerkungWindow(LocalDate selectedDate, Stage parentStage) {
        // Neues Fenster für die Eingabe der Bemerkung
        Stage bemerkungStage = new Stage();
        bemerkungStage.setTitle("Bemerkung hinzufügen");

        // Layout erstellen
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(20));
        vbox.setSpacing(20);

        // Label und Textfeld für die Bemerkung
        Label bemerkungLabel = new Label("Bemerkung:");
        bemerkungLabel.setStyle("-fx-font-size: 30px;");

        TextField bemerkungField = new TextField();
        bemerkungField.setPrefSize(400, 50);
        bemerkungField.setStyle("-fx-font-size: 24px;");

        // Button zum Hinzufügen
        Button hinzufuegenButton = new Button("Hinzufügen");
        hinzufuegenButton.setPrefSize(200, 100);
        hinzufuegenButton.setStyle("-fx-font-size: 30px;");

        // Aktion für den Hinzufügen-Button
        hinzufuegenButton.setOnAction(e -> {
            String bemerkung = bemerkungField.getText().trim();

            // Rückgabe des Datums und der Bemerkung an MainApp
            if (onDateSubmit != null) {
                onDateSubmit.accept(selectedDate, bemerkung);
            }

            // Schließe das Bemerkungsfenster und das Kalenderfenster
            bemerkungStage.close();
            parentStage.close();
        });

        // Elemente zum VBox hinzufügen
        vbox.getChildren().addAll(bemerkungLabel, bemerkungField, hinzufuegenButton);

        // Szene erstellen und anzeigen
        Scene scene = new Scene(vbox, 600, 300);
        bemerkungStage.setScene(scene);
        bemerkungStage.initModality(Modality.APPLICATION_MODAL);
        bemerkungStage.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
