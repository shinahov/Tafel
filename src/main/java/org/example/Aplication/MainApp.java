package org.example.Aplication;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.Db.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainApp extends Application {

    private DBController dbController;
    private Kalender kalender;

    private FamilienTableManager manager;// Lokale Variable f&uuml;r den Kalender

    @Override
    public void start(Stage primaryStage) {
        manager = new FamilienTableManager();
        manager.createTabels();
        dbController = new DBController();
        kalender = new Kalender();

        // Tabellen erstellen


        // Main Menu anzeigen
        showMainMenu(primaryStage);
    }

    private void showMainMenu(Stage primaryStage) {
        primaryStage.setTitle("Tafel Datenbank");

        // Layout erstellen
        GridPane gridPane = createGridPane();

        // UI-Elemente (Buttons)
        Button addFamilyButton = createButton("Neue Familie hinzuf\u00FCgen", 250);
        //addFamilyButton.setFont(new Font("Arial", 24));
        addFamilyButton.setPrefSize(500, 100);
        addFamilyButton.setStyle("-fx-font-size: 30px;");

        // Suche-Button
        Button searchButton = createButton("Suche", 250);
        searchButton.setPrefSize(500, 100);
        searchButton.setStyle("-fx-font-size: 30px;");

        Button showAllFamiliesButton = createButton("Alle Familien anzeigen", 250);
        showAllFamiliesButton.setPrefSize(500, 100);
        showAllFamiliesButton.setStyle("-fx-font-size: 30px;");

        Button deleteFamilyButton = createButton("Familie l\u00F6schen", 250);
        deleteFamilyButton.setPrefSize(500, 100);
        deleteFamilyButton.setStyle("-fx-font-size: 30px; -fx-text-fill: red;");

        Button registerVisitButton = createButton("Besuch registrieren", 250);
        registerVisitButton.setPrefSize(500, 100);
        registerVisitButton.setStyle("-fx-font-size: 30px; -fx-text-fill: blue;");

        // Blacklist-Buttons (aus dem urspr&uuml;nglichen Code)
        Button addToBlacklistButton = createButton("Person in die Blacklist einf\u00FCgen", 250);
        addToBlacklistButton.setPrefSize(500, 100);
        addToBlacklistButton.setStyle("-fx-font-size: 30px; -fx-text-fill: red;");

        Button showBlacklistButton = createButton("Blacklist anzeigen", 250);
        showBlacklistButton.setPrefSize(500, 100);
        showBlacklistButton.setStyle("-fx-font-size: 30px; -fx-text-fill: red;");

        // Neuer Button f&uuml;r das Reinigen des Protokolls
        Button cleanLogButton = createButton("Protokoll reinigen", 250);
        cleanLogButton.setPrefSize(500, 100);
        cleanLogButton.setStyle("-fx-font-size: 30px; -fx-text-fill: orange;");

        // Button-Aktionen
        addFamilyButton.setOnAction(e -> addFamily(primaryStage));
        searchButton.setOnAction(e -> openSearchWindow(primaryStage)); // &Ouml;ffnet das Suchfenster
        showAllFamiliesButton.setOnAction(e -> showAllFamilies(primaryStage));
        deleteFamilyButton.setOnAction(e -> deleteFamilyByNumber(primaryStage));
        registerVisitButton.setOnAction(e -> openCalendarWindow());

        // Blacklist-Button-Aktionen
        addToBlacklistButton.setOnAction(e -> addPersonToBlacklistWindow(primaryStage));  // Aktion zum Hinzuf&uuml;gen zur Blacklist
        showBlacklistButton.setOnAction(e -> showBlacklist(primaryStage));  // Aktion zum Anzeigen der Blacklist

        // Aktion f&uuml;r das Reinigen des Protokolls
        cleanLogButton.setOnAction(e -> cleanVisitLog());

        // Elemente zum GridPane hinzuf&uuml;gen
        gridPane.add(addFamilyButton, 0, 0);
        gridPane.add(searchButton, 0, 1);  // Hinzuf&uuml;gen des Such-Buttons
        gridPane.add(showAllFamiliesButton, 0, 2);
        gridPane.add(deleteFamilyButton, 1, 0);
        gridPane.add(registerVisitButton, 1, 1);
        gridPane.add(addToBlacklistButton, 1, 2);  // Hinzuf&uuml;gen des Blacklist-Buttons
        gridPane.add(showBlacklistButton, 1, 3);   // Hinzuf&uuml;gen des Blacklist-Buttons
        gridPane.add(cleanLogButton, 0, 3);        // Hinzuf&uuml;gen des neuen Protokoll-Reinigungs-Buttons

        // Szene erstellen und anzeigen
        Scene scene = new Scene(gridPane);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void cleanVisitLog() {
        // Bestätigungsdialog anzeigen, um versehentliches Löschen zu vermeiden
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("L\u00D6SCHBEST\u00C4TIGUNG");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("SIND SIE SICHER, DASS SIE ALLE BESUCHSEINTR\u00C4GE, DIE \u00C4LTER ALS 2 MONATE SIND, L\u00D6SCHEN M\u00D6CHTEN?");
        confirmationAlert.getDialogPane().setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        confirmationAlert.getDialogPane().setPrefSize(500, 300);


        // Zeige den Bestätigungsdialog an und prüfe, ob der Benutzer OK auswählt
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                DBController dbController = new DBController();
                LocalDateTime currentDateTime = LocalDateTime.now();

                // Besuchseinträge löschen, die älter als 2 Monate sind
                dbController.deleteOldVisits(currentDateTime);
                showAlert("BESUCHSPROTOKOLL GEL\u00D6SCHT", "ALLE BESUCHSEINTR\u00C4GE, DIE \u00C4LTER ALS 2 MONATE SIND, WURDEN GEL\u00D6SCHT.");
            } else {
                showAlert("ABGEBROCHEN", "DAS L\u00D6SCHEN DER BESUCHSEINTR\u00C4GE WURDE ABGEBROCHEN.");
            }
        });
    }




    private void openSearchWindow(Stage primaryStage) {
        Stage searchStage = new Stage();
        searchStage.setTitle("Familie suchen");
        searchStage.initModality(Modality.APPLICATION_MODAL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);  // Abstand zwischen den Spalten
        gridPane.setVgap(10);  // Abstand zwischen den Zeilen
        gridPane.setPadding(new Insets(20, 20, 20, 20));  // Abstand von den R&auml;ndern

        // Einheitliche Gr&ouml;&szlig;e f&uuml;r Labels, Textfelder und Buttons
        int labelFontSize = 18;
        int buttonFontSize = 18;
        int fieldWidth = 200;  // Breite der Textfelder
        int fieldHeight = 40;  // H&ouml;he der Textfelder und Buttons

        // AtomicReference f&uuml;r die ID der Familie initialisieren
        AtomicReference<Integer> id = new AtomicReference<>(-1);

        // Labels und Textfelder (erste Zeile)
        Label nummerLabel = new Label("Nummer:");
        nummerLabel.setStyle("-fx-font-size: " + labelFontSize + "px;");
        TextField nummerField = new TextField();
        nummerField.setPrefSize(fieldWidth, fieldHeight);
        nummerField.setStyle("-fx-font-size: " + labelFontSize + "px;");

        Label vornameLabel = new Label("Vorname:");
        vornameLabel.setStyle("-fx-font-size: " + labelFontSize + "px;");
        TextField vornameField = new TextField();
        vornameField.setPrefSize(fieldWidth, fieldHeight);
        vornameField.setStyle("-fx-font-size: " + labelFontSize + "px;");

        Label nachnameLabel = new Label("Nachname:");
        nachnameLabel.setStyle("-fx-font-size: " + labelFontSize + "px;");
        TextField nachnameField = new TextField();
        nachnameField.setPrefSize(fieldWidth, fieldHeight);
        nachnameField.setStyle("-fx-font-size: " + labelFontSize + "px;");

        // Suchen- und Zur&uuml;ck-Button (zweite Zeile)
        Button searchButton = new Button("Suchen");
        searchButton.setPrefSize(300, 50);
        searchButton.setStyle("-fx-font-size: " + buttonFontSize + "px;");

        Button backButton = new Button("Zur\u00FCck");
        backButton.setPrefSize(300, 50);
        backButton.setStyle("-fx-font-size: " + buttonFontSize + "px;");

        // Ergebnisanzeige (dritte Zeile)
        TextArea resultArea = new TextArea();
        resultArea.setPrefSize(800, 400);  // Gr&ouml;&szlig;er und h&ouml;her
        resultArea.setStyle("-fx-font-size: " + labelFontSize + "px;");
        resultArea.setEditable(false);

        // L&ouml;schen-, Anmerkung- und Bannen-Buttons (vierte Zeile)
        Button deleteButton = new Button("L\u00F6schen");
        deleteButton.setPrefSize(300, 50);
        deleteButton.setStyle("-fx-font-size: " + buttonFontSize + "px; -fx-text-fill: red;");
        deleteButton.setVisible(false);

        Button anmerkungButton = new Button("Anmerkung hinzuf\u00FCgen");
        anmerkungButton.setPrefSize(300, 50);
        anmerkungButton.setStyle("-fx-font-size: " + buttonFontSize + "px; -fx-text-fill: blue;");
        anmerkungButton.setVisible(false);

        // Tempor&auml;r Bannen- und Ban-Aufheben-Button
        Button banButton = new Button("Tempor\u00E4r bannen");
        banButton.setPrefSize(300, 50);
        banButton.setStyle("-fx-font-size: " + buttonFontSize + "px; -fx-text-fill: orange;");
        banButton.setVisible(false);

        Button unbanButton = new Button("Ban aufheben");
        unbanButton.setPrefSize(300, 50);
        unbanButton.setStyle("-fx-font-size: " + buttonFontSize + "px;");
        unbanButton.setVisible(false);

        // Layout erstellen
        gridPane.add(nummerLabel, 0, 0);
        gridPane.add(nummerField, 1, 0);
        gridPane.add(vornameLabel, 2, 0);
        gridPane.add(vornameField, 3, 0);
        gridPane.add(nachnameLabel, 4, 0);
        gridPane.add(nachnameField, 5, 0);

        gridPane.add(backButton, 0, 1, 2, 1);  // Zur&uuml;ck-Button
        gridPane.add(searchButton, 2, 1, 2, 1);  // Suchen-Button

        gridPane.add(resultArea, 0, 2, 6, 1);  // Ergebnisanzeige

        gridPane.add(deleteButton, 0, 3, 2, 1);  // L&ouml;schen-Button
        gridPane.add(anmerkungButton, 2, 3, 2, 1);  // Anmerkung-Button
        gridPane.add(banButton, 4, 3, 2, 1);  // Bannen-Button
        gridPane.add(unbanButton, 4, 4, 2, 1);  // Ban aufheben-Button

        // Event-Handling f&uuml;r die Suche
        searchButton.setOnAction(e -> {
            String nummer = nummerField.getText();
            String vorname = vornameField.getText();
            String nachname = nachnameField.getText();

            if (!nummer.isEmpty()) {
                // Suche nach Nummer
                Familie familie = dbController.searchByNumber(nummer);
                id.set(dbController.searchIdByNummer(nummer));
                if (familie != null) {
                    resultArea.setText(familie.toString());  // Verwende die toString()-Methode

                    // &Uuml;berpr&uuml;fung, ob die Familie gebannt ist
                    LocalDate banEndDate = dbController.getBanEndDateForFamily(id.get());
                    if (banEndDate != null) {
                        // Berechne die verbleibenden Tage bis zum Ende des Banns
                        long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), banEndDate);
                        resultArea.appendText("\nDiese Familie ist gebannt f\u00FCr " + remainingDays + " Tage.");
                        resultArea.setStyle("-fx-text-fill: red; -fx-font-size: 24px;");  // Markiere die Nachricht rot
                        unbanButton.setVisible(true);  // Zeige den Ban aufheben-Button an
                    }

                    deleteButton.setVisible(true);
                    anmerkungButton.setVisible(true);
                    banButton.setVisible(true);  // Zeige den Bann-Button an

                    // Anmerkung hinzuf&uuml;gen
                    anmerkungButton.setOnAction(addAnmerkungEvent -> addAnmerkung(familie));

                    // Familie l&ouml;schen
                    deleteButton.setOnAction(del -> {
                        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmationAlert.setTitle("Familie l\u00F6schen");
                        confirmationAlert.setHeaderText("Familie l\u00F6schen?");
                        confirmationAlert.setContentText("Wollen Sie wirklich die Familie mit der Nummer " + familie.getNummer() + " l\u00F6schen?");

                        DialogPane dialogPane = confirmationAlert.getDialogPane();
                        dialogPane.setStyle("-fx-font-size: 20px;");

                        confirmationAlert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                dbController.deleteFamily(familie.getNummer());
                                resultArea.setText("Familie mit der Nummer " + familie.getNummer() + " wurde gel\u00F6scht.");
                                deleteButton.setVisible(false);
                                anmerkungButton.setVisible(false);
                                banButton.setVisible(false);
                                unbanButton.setVisible(false);
                            }
                        });
                    });

                    // Tempor&auml;r bannen
                    banButton.setOnAction(banEvent -> {
                        LocalDate banUntil = showBanDatePickerWindow();

                        // Wenn ein Datum ausgew&auml;hlt wurde
                        if (banUntil != null) {
                            dbController.banFamily(id.get(), banUntil);
                            resultArea.appendText("\nDie Familie wurde bis zum " + banUntil + " gebannt.");
                            unbanButton.setVisible(true);
                        } else {
                            resultArea.appendText("\nKein Bann-Datum ausgew\u00E4hlt.");
                        }
                    });

                    // Ban aufheben
                    unbanButton.setOnAction(unbanEvent -> {
                        dbController.unbanFamily(id.get());
                        resultArea.appendText("\nDer Bann f\u00FCr die Familie wurde aufgehoben.");
                        unbanButton.setVisible(false);
                    });

                } else {
                    resultArea.setText("Keine Familie mit dieser Nummer gefunden.");
                    deleteButton.setVisible(false);
                    anmerkungButton.setVisible(false);
                    banButton.setVisible(false);
                    unbanButton.setVisible(false);
                }
            } else if (!vorname.isEmpty() && !nachname.isEmpty()) {
                // Suche nach Vor- und Nachname mit findFamilyByPerson
                Familie familie = dbController.findFamilyByPerson(vorname, nachname);
                if (familie != null) {
                    id.set(dbController.searchIdByNummer(familie.getNummer())); // ID der Familie holen
                    resultArea.setText(familie.toString());

                    // Überprüfen, ob die Familie gebannt ist
                    LocalDate banEndDate = dbController.getBanEndDateForFamily(id.get());
                    if (banEndDate != null) {
                        long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), banEndDate);
                        resultArea.appendText("\nDiese Familie ist gebannt für " + remainingDays + " Tage.");
                        resultArea.setStyle("-fx-text-fill: red; -fx-font-size: 24px;");
                        unbanButton.setVisible(true);
                    }
                    deleteButton.setVisible(true);
                    anmerkungButton.setVisible(true);
                    banButton.setVisible(true);
                } else {
                    resultArea.setText("Keine Familie mit diesem Vor- und Nachnamen gefunden.");
                    deleteButton.setVisible(false);
                    anmerkungButton.setVisible(false);
                    banButton.setVisible(false);
                    unbanButton.setVisible(false);
                }
            } else if (!nachname.isEmpty()) {
                // Suche nur nach Nachname mit findFamiliesByLastname
                List<Familie> familien = dbController.findFamiliesByLastname(nachname);
                if (!familien.isEmpty()) {
                    StringBuilder result = new StringBuilder();
                    for (Familie fam : familien) {
                        id.set(dbController.searchIdByNummer(fam.getNummer())); // ID der Familie holen
                        result.append(fam).append("\n");

                        // Überprüfen, ob die Familie gebannt ist
                        LocalDate banEndDate = dbController.getBanEndDateForFamily(id.get());
                        if (banEndDate != null) {
                            long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), banEndDate);
                            result.append("Diese Familie ist gebannt für ").append(remainingDays).append(" Tage.\n");
                        }
                    }
                    resultArea.setText(result.toString());
                    deleteButton.setVisible(false);
                    anmerkungButton.setVisible(false);
                    banButton.setVisible(false);
                    unbanButton.setVisible(false);
                } else {
                    resultArea.setText("Keine Familien mit diesem Nachnamen gefunden.");
                    deleteButton.setVisible(false);
                    anmerkungButton.setVisible(false);
                    banButton.setVisible(false);
                    unbanButton.setVisible(false);
                }
            } else {
                // Wenn keine Suchparameter angegeben sind
                resultArea.setText("Bitte mindestens eine Suchinformation eingeben.");
                deleteButton.setVisible(false);
                anmerkungButton.setVisible(false);
                banButton.setVisible(false);
                unbanButton.setVisible(false);
            }

        });

        // Zur&uuml;ck-Button
        backButton.setOnAction(e -> searchStage.close());  // Schlie&szlig;e das aktuelle Suchfenster

        // Scene erstellen und anzeigen
        Scene scene = new Scene(gridPane, 800, 600);
        searchStage.setScene(scene);
        searchStage.setMaximized(true);
        searchStage.showAndWait();
    }









    public void showBlacklist(Stage primaryStage) {
        Stage blacklistStage = createModalStage("Blacklist anzeigen", primaryStage);

        GridPane gridPane = createGridPane();

        List<Person> blacklistedPersons = dbController.getAllBlacklistedPersons();

        // TextArea, um die Blacklist anzuzeigen
        TextArea blacklistArea = new TextArea();
        blacklistArea.setPrefSize(800, 400);
        blacklistArea.setEditable(false);
        blacklistArea.setStyle("-fx-font-size: 30px; -fx-text-fill: red;");

        if (blacklistedPersons.isEmpty()) {
            blacklistArea.setText("Keine Personen in der Blacklist.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Person person : blacklistedPersons) {
                sb.append("Vorname: ").append(person.getVorname())
                        .append(", Nachname: ").append(person.getNachname());

                if (person.getAdresse() != null) {
                    sb.append(", Adresse: ").append(person.getAdresse());
                }
                sb.append("\n");
            }
            blacklistArea.setText(sb.toString());
        }

        // Button zum Entfernen einer Person aus der Blacklist
        Label vornameLabel = new Label("Vorname:");
        vornameLabel.setStyle("-fx-font-size: 30px;");  // Schriftgr&ouml;&szlig;e f&uuml;r das Label anpassen
        TextField vornameField = new TextField();
        vornameField.setPrefSize(500, 100);
        vornameField.setStyle("-fx-font-size: 30px;");  // Schriftgr&ouml;&szlig;e f&uuml;r das Textfeld anpassen

        Label nachnameLabel = new Label("Nachname:");
        nachnameLabel.setStyle("-fx-font-size: 30px;");  // Schriftgr&ouml;&szlig;e f&uuml;r das Label anpassen
        TextField nachnameField = new TextField();
        nachnameField.setPrefSize(500, 100);
        nachnameField.setStyle("-fx-font-size: 30px;");  // Schriftgr&ouml;&szlig;e f&uuml;r das Textfeld anpassen

        Button removeButton = new Button("Aus Blacklist entfernen");
        removeButton.setPrefSize(500, 100);  // Button-Gr&ouml;&szlig;e anpassen
        removeButton.setStyle("-fx-font-size: 30px;");

        removeButton.setOnAction(e -> {
            String vorname = vornameField.getText();
            String nachname = nachnameField.getText();

            if (!vorname.isEmpty() && !nachname.isEmpty()) {
                dbController.removePersonFromBlacklist(vorname, nachname);
                blacklistArea.setText("Person " + vorname + " " + nachname + " wurde aus der Blacklist entfernt.");
            } else {
                showAlert("Fehler", "Vorname und Nachname m\u00FCssen eingegeben werden.");
            }
        });

        gridPane.add(blacklistArea, 0, 0, 2, 1);
        gridPane.add(vornameLabel, 0, 1);
        gridPane.add(vornameField, 1, 1);
        gridPane.add(nachnameLabel, 0, 2);
        gridPane.add(nachnameField, 1, 2);
        gridPane.add(removeButton, 1, 3);

        Scene scene = new Scene(gridPane, 600, 400);
        blacklistStage.setScene(scene);
        blacklistStage.setMaximized(true);
        blacklistStage.showAndWait();
    }


    private void addPersonToBlacklistWindow(Stage primaryStage) {
        Stage blacklistStage = createModalStage("Person in die Blacklist einf\u00FCgen", primaryStage);

        GridPane gridPane = createGridPane();

        Label vornameLabel = new Label("Vorname:");
        vornameLabel.setStyle("-fx-font-size: 30px;");
        TextField vornameField = new TextField();
        vornameField.setPrefSize(500, 100);
        vornameField.setStyle("-fx-font-size: 30px;");

        Label nachnameLabel = new Label("Nachname:");
        nachnameLabel.setStyle("-fx-font-size: 30px;");
        TextField nachnameField = new TextField();
        nachnameField.setPrefSize(500, 100);
        nachnameField.setStyle("-fx-font-size: 30px;");

        Label adresseLabel = new Label("Adresse (optional):");
        adresseLabel.setStyle("-fx-font-size: 30px;");
        TextField adresseField = new TextField();
        adresseField.setPrefSize(500, 100);
        adresseField.setStyle("-fx-font-size: 30px;");

        Button insertButton = new Button("Einf\u00FCgen");
        insertButton.setPrefSize(500, 100);
        insertButton.setStyle("-fx-font-size: 30px;");

        Button backButton = new Button("Zur\u00FCck");
        backButton.setPrefSize(500, 100);
        backButton.setStyle("-fx-font-size: 30px;");

        // F&uuml;gen Sie die Eingabefelder und Buttons zum GridPane hinzu
        gridPane.add(vornameLabel, 0, 0);
        gridPane.add(vornameField, 1, 0);
        gridPane.add(nachnameLabel, 0, 1);
        gridPane.add(nachnameField, 1, 1);
        gridPane.add(adresseLabel, 0, 2);
        gridPane.add(adresseField, 1, 2);
        gridPane.add(insertButton, 1, 3);
        gridPane.add(backButton, 0, 3);

        // Aktion zum Einf&uuml;gen der Person in die Blacklist
        insertButton.setOnAction(e -> {
            String vorname = vornameField.getText();
            String nachname = nachnameField.getText();
            String adresse = adresseField.getText();

            if (vorname.isEmpty() || nachname.isEmpty()) {
                showAlert("Fehler", "Vorname und Nachname sind erforderlich.");
                return;
            }

            // Person erstellen und in die Blacklist einf&uuml;gen
            Person person = new Person(vorname, nachname, adresse.isEmpty() ? null : adresse);
            dbController.addPersonToBlacklist(person);
            showAlert("Erfolg", "Die Person wurde erfolgreich in die Blacklist aufgenommen.");
            blacklistStage.close();
        });

        backButton.setOnAction(e -> blacklistStage.close());

        Scene scene = new Scene(gridPane, 600, 400);
        blacklistStage.setScene(scene);
        blacklistStage.setMaximized(true);
        blacklistStage.showAndWait();
    }

    // Methode zum Anzeigen einer Warnung







    // Methode zum Hinzuf&uuml;gen einer neuen Familie
    public void addFamily(Stage primaryStage) {
        Stage addFamilyStage = createModalStage("Neue Familie hinzuf\u00FCgen", primaryStage);

        GridPane gridPane = createGridPane();

        Label nummerLabel = new Label("Nummer:");
        nummerLabel.setStyle("-fx-font-size: 30px;");
        TextField nummerField = new TextField();
        nummerField.setPrefSize(500, 100);
        nummerField.setStyle("-fx-font-size: 30px;");

        Label vornameLabel = new Label("Vorname (Hauptperson):");
        vornameLabel.setStyle("-fx-font-size: 30px;");
        TextField vornameField = new TextField();
        vornameField.setPrefSize(500, 100);
        vornameField.setStyle("-fx-font-size: 30px;");

        Label nachnameLabel = new Label("Nachname (Hauptperson):");
        nachnameLabel.setStyle("-fx-font-size: 30px;");
        TextField nachnameField = new TextField();
        nachnameField.setPrefSize(500, 100);
        nachnameField.setStyle("-fx-font-size: 30px;");

        Label adresseLabel = new Label("Adresse (Hauptperson):");
        adresseLabel.setStyle("-fx-font-size: 30px;");
        TextField adresseField = new TextField();
        adresseField.setPrefSize(500, 100);
        adresseField.setStyle("-fx-font-size: 30px;");

        Label anzahlPersonenLabel = new Label("Anzahl weiterer Personen:");
        anzahlPersonenLabel.setStyle("-fx-font-size: 30px;");
        TextField anzahlPersonenField = new TextField();
        anzahlPersonenField.setPrefSize(500, 100);
        anzahlPersonenField.setStyle("-fx-font-size: 30px;");

        Label anzahlKinderLabel = new Label("Anzahl Kinder:");
        anzahlKinderLabel.setStyle("-fx-font-size: 30px;");
        TextField anzahlKinderField = new TextField();
        anzahlKinderField.setPrefSize(500, 100);
        anzahlKinderField.setStyle("-fx-font-size: 30px;");

        Button saveButton = new Button("Speichern");
        saveButton.setPrefSize(500, 100);
        saveButton.setStyle("-fx-font-size: 30px;");

        Button backButton = new Button("Zur\u00FCck");
        backButton.setPrefSize(500, 100);
        backButton.setStyle("-fx-font-size: 30px;");

        Button anmerkungButton = new Button("Anmerkung hinzuf\u00FCgen");
        anmerkungButton.setPrefSize(500, 100);
        anmerkungButton.setStyle("-fx-font-size: 30px;");
        anmerkungButton.setVisible(false);

        gridPane.add(nummerLabel, 0, 0);
        gridPane.add(nummerField, 1, 0);
        gridPane.add(vornameLabel, 0, 1);
        gridPane.add(vornameField, 1, 1);
        gridPane.add(nachnameLabel, 0, 2);
        gridPane.add(nachnameField, 1, 2);
        gridPane.add(adresseLabel, 0, 3);
        gridPane.add(adresseField, 1, 3);
        gridPane.add(anzahlPersonenLabel, 0, 4);
        gridPane.add(anzahlPersonenField, 1, 4);
        gridPane.add(anzahlKinderLabel, 0, 5);
        gridPane.add(anzahlKinderField, 1, 5);
        gridPane.add(saveButton, 1, 6);
        gridPane.add(backButton, 0, 6);
        gridPane.add(anmerkungButton, 1, 7);

        saveButton.setOnAction(e -> {
            String nummerText = nummerField.getText();
            String vorname = vornameField.getText();
            String nachname = nachnameField.getText();
            String adresse = adresseField.getText();
            String anzahlPersonenText = anzahlPersonenField.getText();
            String anzahlKinderText = anzahlKinderField.getText();

            // &Uuml;berpr&uuml;fung, ob die Nummer eine g&uuml;ltige Ganzzahl ist
            int nummer = 0;
            try {
                nummer = Integer.parseInt(nummerText);
            } catch (NumberFormatException ex) {
                showAlert("Ung\u00FCltige Eingabe", "Die Familiennummer muss eine Zahl sein.");
                return;
            }

            if (vorname.isEmpty() || nachname.isEmpty()) {
                showAlert("Fehler", "Bitte alle Felder ausf\u00FCllen.");
                return;
            }

            // Hauptperson erstellen
            Person hauptperson = new Person(vorname, nachname, adresse);

            // Pr&uuml;fen, ob die Hauptperson bereits in einer anderen Familie existiert
            Familie existierendeFamilie = dbController.findFamilyByPerson(vorname, nachname);
            //System.out.println(existierendeFamilie);
            if (existierendeFamilie != null) {
                // Warnung anzeigen, dass die Person bereits existiert
                int finalNummer = nummer;
                showPersonExistsAlert(hauptperson, existierendeFamilie, () -> {
                    try {
                        familieSpeichern(finalNummer, vorname, nachname, adresse, anzahlPersonenText, anzahlKinderText, addFamilyStage);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                return;
            }

            try {
                familieSpeichern(nummer, vorname, nachname, adresse, anzahlPersonenText, anzahlKinderText, addFamilyStage);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        backButton.setOnAction(e -> addFamilyStage.close());

        Scene scene = new Scene(gridPane, 600, 400);
        addFamilyStage.setScene(scene);
        addFamilyStage.setMaximized(true);
        addFamilyStage.showAndWait();
    }

    private void showPersonExistsAlert(Person person, Familie existierendeFamilie, Runnable onConfirm) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Person bereits vorhanden");
        alert.setHeaderText("Die Person " + person.getVorname() + " " + person.getNachname() + " ist bereits in einer anderen Familie.");
        alert.setContentText("Familiennummer: " + existierendeFamilie.getNummer() + "\n"
                + "Vorname: " + existierendeFamilie.getHauptperson().getVorname() + "\n"
                + "Nachname: " + existierendeFamilie.getHauptperson().getNachname() + "\n"
                + "Adresse: " + existierendeFamilie.getHauptperson().getAdresse());

        ButtonType trotzdemHinzufugen = new ButtonType("Trotzdem hinzuf\u00FCgen");
        ButtonType zuruck = new ButtonType("Zur\u00FCck", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(trotzdemHinzufugen, zuruck);

        alert.showAndWait().ifPresent(response -> {
            if (response == trotzdemHinzufugen) {
                onConfirm.run();  // Methode ausf&uuml;hren, wenn der Benutzer auf "Trotzdem hinzuf&uuml;gen" klickt
            }
        });
    }

    private void familieSpeichern(int nummer, String vorname, String nachname, String adresse, String anzahlPersonenText, String anzahlKinderText, Stage addFamilyStage) throws SQLException {
        // Hier wird die Familie nach Best&auml;tigung durch den Benutzer gespeichert
        Familie familie = new Familie(String.valueOf(nummer), new Person(vorname, nachname, adresse));

        // Anzahl der Kinder validieren und hinzuf&uuml;gen
        int anzahlKinder = 0;
        try {
            anzahlKinder = Integer.parseInt(anzahlKinderText);
            familie.setAnzahlKinder(anzahlKinder);
        } catch (NumberFormatException ex) {
            showAlert("Ung\u00FCltige Eingabe", "Anzahl der Kinder muss eine Zahl sein.");
            return;
        }

        // Anzahl weiterer Personen validieren und hinzuf&uuml;gen
        int anzahlPersonen;
        try {
            anzahlPersonen = Integer.parseInt(anzahlPersonenText);
        } catch (NumberFormatException ex) {
            showAlert("Ung\u00FCltige Eingabe", "Anzahl weiterer Personen muss eine Zahl sein.");
            return;
        }

        for (int i = 1; i <= anzahlPersonen; i++) {
            // Dialogfenster für die Eingabe
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Weitere Person hinzufügen");
            dialog.setHeaderText("Person " + i + " hinzufügen");

            // Labels und Textfelder für Vorname, Nachname und Adresse
            Label vornameLabel = new Label("Vorname:");
            TextField vornameField = new TextField();
            Label nachnameLabel = new Label("Nachname:");
            TextField nachnameField = new TextField();
            Label adresseLabel = new Label("Adresse:");
            TextField adresseField = new TextField();

            // Layout für die Felder
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            grid.add(vornameLabel, 0, 0);
            grid.add(vornameField, 1, 0);
            grid.add(nachnameLabel, 0, 1);
            grid.add(nachnameField, 1, 1);
            grid.add(adresseLabel, 0, 2);
            grid.add(adresseField, 1, 2);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Ergebnisverarbeitung nach Klick auf OK
            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String vornamesto = vornameField.getText().trim();
                    String nachnamesto = nachnameField.getText().trim();
                    String adressesto = adresseField.getText().trim();

                    if (!vornamesto.isEmpty() && !nachnamesto.isEmpty() && !adressesto.isEmpty()) {
                        Person person = new Person(vornamesto, nachnamesto, adressesto);

                        // Prüfen, ob die Person in der Blacklist ist
                        if (isPersonInBlacklist(person)) {
                            showAlert("Warnung", "Die Person " + person.getVorname() + " " + person.getNachname() + " befindet sich in der Blacklist.");
                            return;
                        }

                        // Prüfen, ob die Person bereits in einer anderen Familie ist
                        Familie existierendeFamilie = dbController.findFamilyByPerson(person.getVorname(), person.getNachname());
                        if (existierendeFamilie != null) {
                            // Alert anzeigen, dass die Person bereits existiert
                            showPersonExistsAlert(person, existierendeFamilie, () -> {
                                familie.addPerson(person); // Person dennoch hinzufügen
                            });
                        } else {
                            familie.addPerson(person); // Person zur Familie hinzufügen
                        }
                    } else {
                        showAlert("Ungültige Eingabe", "Bitte alle Felder ausfüllen.");
                    }
                }
            });
        }



        // Familie in die Datenbank einf&uuml;gen
        ResultSet keyid = dbController.insertFamily(familie);
        int key = keyid.getInt(1);
        kinderSpeichern(anzahlKinder, key);

        // Info-Dialog anzeigen
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("Erfolgreich gespeichert");
        infoAlert.setHeaderText(null);
        infoAlert.setContentText("Die Familie mit der Nummer " + nummer + " wurde erfolgreich gespeichert.");
        infoAlert.showAndWait();

        addFamilyStage.close();
    }

    private void kinderSpeichern(int anzahlKinder, int familienId) {
        for (int i = 1; i <= anzahlKinder; i++) {
            // Erstelle ein neues Dialogfenster f&uuml;r die Eingabe von Kinderdaten
            Stage kinderStage = new Stage();
            kinderStage.setTitle("Kind " + i + " hinzuf\u00FCgen");

            GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(20));
            gridPane.setHgap(10);
            gridPane.setVgap(10);

            // Label und Eingabefelder f&uuml;r das Geschlecht
            Label geschlechtLabel = new Label("Geschlecht:");
            geschlechtLabel.setStyle("-fx-font-size: 18px;");
            TextField geschlechtField = new TextField();
            geschlechtField.setPromptText("Geschlecht eingeben (m\u00E4nnlich/weiblich)");
            geschlechtField.setPrefWidth(300);

            // Label und DatePicker f&uuml;r das Geburtsdatum
            Label geburtsdatumLabel = new Label("Geburtsdatum:");
            geburtsdatumLabel.setStyle("-fx-font-size: 18px;");
            DatePicker geburtsdatumPicker = new DatePicker();
            geburtsdatumPicker.setPromptText("Geburtsdatum ausw\u00E4hlen");
            geburtsdatumPicker.setPrefWidth(300);

            // Button zum Speichern
            Button saveButton = new Button("Speichern");
            saveButton.setPrefWidth(150);
            saveButton.setStyle("-fx-font-size: 18px;");

            // Speichern der Kinderdaten
            saveButton.setOnAction(e -> {
                String geschlecht = geschlechtField.getText().trim();
                LocalDate geburtsdatum = geburtsdatumPicker.getValue();

                if (geschlecht.isEmpty() || geburtsdatum == null) {
                    showAlert("Ung\u00FCltige Eingabe", "Bitte geben Sie alle erforderlichen Informationen ein.");
                    return;
                }

                // Kind in die Datenbank speichern
                Kinder kind = new Kinder(geschlecht, geburtsdatum);
                dbController.saveChild(kind, familienId); // Speichert das Kind in der Datenbank mit der Familien-ID

                // Info-Dialog anzeigen
                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setTitle("Erfolgreich gespeichert");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("Das Kind wurde erfolgreich gespeichert.");
                infoAlert.showAndWait();

                kinderStage.close();  // Schlie&szlig;t das Fenster nach dem Speichern
            });

            // Elemente zum GridPane hinzuf&uuml;gen
            gridPane.add(geschlechtLabel, 0, 0);
            gridPane.add(geschlechtField, 1, 0);
            gridPane.add(geburtsdatumLabel, 0, 1);
            gridPane.add(geburtsdatumPicker, 1, 1);
            gridPane.add(saveButton, 1, 2);

            // Szene erstellen und anzeigen
            Scene scene = new Scene(gridPane, 450, 200);
            kinderStage.setScene(scene);
            kinderStage.initModality(Modality.APPLICATION_MODAL); // Blockiert andere Fenster, bis dieses geschlossen wird
            kinderStage.showAndWait();
        }
    }


    private void openCalendarWindow() {
        // Neues Fenster f&uuml;r die Eingabe der Familiennummer
        Stage calendarStage = new Stage();
        calendarStage.setTitle("Besuch registrieren");

        // Layout erstellen
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(20);
        gridPane.setVgap(20);

        // Label und Textfeld f&uuml;r die Familiennummer
        Label familyNumberLabel = new Label("Geben Sie die Familiennummer ein:");
        familyNumberLabel.setStyle("-fx-font-size: 30px;");

        TextField familyNumberField = new TextField();
        familyNumberField.setPrefSize(400, 50);
        familyNumberField.setStyle("-fx-font-size: 24px;");

        // Button zum Fortfahren
        Button proceedButton = new Button("Weiter");
        proceedButton.setPrefSize(200, 100);
        proceedButton.setStyle("-fx-font-size: 30px;");

        // Aktion f&uuml;r den Weiter-Button
        proceedButton.setOnAction(e -> {
            String familyNumberText = familyNumberField.getText();
            try {
                int familyNumber = Integer.parseInt(familyNumberText);  // &Uuml;berpr&uuml;fe, ob es eine Ganzzahl ist

                // Suche nach der Familien-ID in der Datenbank
                int familyId = dbController.getFamilyIdByNumber(familyNumberText);  // Hole die ID der Familie basierend auf der Familiennummer

                if (familyId == -1) {
                    // Wenn keine Familie gefunden wurde
                    showAlert("Ung\u00FCltige Nummer", "Die eingegebene Familiennummer existiert nicht.");
                } else {
                    // &Uuml;berpr&uuml;fen, ob die Familie gebannt ist
                    LocalDate banEndDate = dbController.getBanEndDateForFamily(familyId);
                    if (banEndDate != null && banEndDate.isAfter(LocalDate.now())) {
                        // Wenn die Familie noch gebannt ist, zeige einen Alert an
                        showAlert("Familie gebannt", "Diese Familie ist bis zum " + banEndDate + " gebannt.");
                        return;  // Beende die Methode, um weitere Aktionen zu verhindern
                    }

                    // Wenn die Familie gefunden wurde, rufe die Besuchsdaten ab
                    List<BesuchsEintrag> visitDates = dbController.getVisitEntriesByFamilyId(familyId);

                    // Kalender-Instanz erstellen
                    Kalender kalender = new Kalender();

                    // Setze den Callback f&uuml;r das Datum, das beim Absenden des Kalenders zur&uuml;ckgegeben wird
                    kalender.setOnDateSubmit((LocalDate selectedDate, String bemerkung) -> {
                        // &Uuml;berpr&uuml;fe, ob das ausgew&auml;hlte Datum in der Zukunft liegt
                        if (selectedDate.isAfter(LocalDate.now())) {
                            // Zeige den Alarm an, aber schlie&szlig;e das Kalenderfenster nicht
                            showAlert("Ung\u00FCltiges Datum", "Das ausgew\u00E4hlte Datum liegt in der Zukunft. Bitte w\u00E4hlen Sie ein g\u00FCltiges Datum.");
                        } else {
                            // Wenn eine Bemerkung vorhanden ist, speichere mit Bemerkung
                            if (bemerkung != null && !bemerkung.isEmpty()) {
                                dbController.saveVisit(familyId, selectedDate, bemerkung);  // Speichere mit Bemerkung
                            } else {
                                dbController.saveVisit(familyId, selectedDate);  // Speichere ohne Bemerkung
                            }
                            // Schlie&szlig;e das Fenster nur, wenn das Datum korrekt war
                            calendarStage.close();
                        }
                    });

                    // Kalender starten und die Besuchsdaten &uuml;bergeben
                    kalender.start(calendarStage, visitDates);
                }

            } catch (NumberFormatException ex) {
                showAlert("Ung\u00FCltige Eingabe", "Bitte geben Sie eine g\u00FCltige Familiennummer ein.");
            }
        });

        // Elemente zum GridPane hinzuf&uuml;gen
        gridPane.add(familyNumberLabel, 0, 0);
        gridPane.add(familyNumberField, 0, 1);
        gridPane.add(proceedButton, 0, 2);

        // Szene erstellen und anzeigen
        Scene scene = new Scene(gridPane, 600, 400);
        calendarStage.setScene(scene);
        calendarStage.initModality(Modality.APPLICATION_MODAL);
        calendarStage.showAndWait();
    }




    private void openVisitWindow(int familyId, LocalDate selectedDate, Stage calendarStage) {
        // Neues Fenster f&uuml;r die Eingabe der Besuchsinformationen
        Stage visitStage = new Stage();
        visitStage.setTitle("Besuch registrieren");

        // Layout erstellen
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(20);
        gridPane.setVgap(20);

        // Checkbox f&uuml;r Bemerkung
        CheckBox bemerkungCheckBox = new CheckBox("Mit Bemerkung");
        bemerkungCheckBox.setStyle("-fx-font-size: 24px;");

        // Label und Textfeld f&uuml;r die Bemerkung (zun&auml;chst versteckt)
        Label bemerkungLabel = new Label("Bemerkung:");
        bemerkungLabel.setStyle("-fx-font-size: 30px;");
        bemerkungLabel.setVisible(false);

        TextField bemerkungField = new TextField();
        bemerkungField.setPrefSize(400, 50);
        bemerkungField.setStyle("-fx-font-size: 24px;");
        bemerkungField.setVisible(false);

        // Zeige das Bemerkungsfeld, wenn die Checkbox aktiviert ist
        bemerkungCheckBox.setOnAction(e -> {
            boolean isSelected = bemerkungCheckBox.isSelected();
            bemerkungLabel.setVisible(isSelected);
            bemerkungField.setVisible(isSelected);
        });

        // Button zum Speichern
        Button hinzufuegenButton = new Button("Besuch speichern");
        hinzufuegenButton.setPrefSize(200, 100);
        hinzufuegenButton.setStyle("-fx-font-size: 30px;");

        // Aktion f&uuml;r den Hinzuf&uuml;gen-Button
        hinzufuegenButton.setOnAction(e -> {
            String bemerkung = bemerkungCheckBox.isSelected() ? bemerkungField.getText().trim() : null;

            // Speichere den Besuch in der Datenbank (mit oder ohne Bemerkung)
            if (bemerkung != null && !bemerkung.isEmpty()) {
                dbController.saveVisit(familyId, selectedDate, bemerkung);
            } else {
                dbController.saveVisit(familyId, selectedDate);
            }

            // Schlie&szlig;e die Fenster nach dem Speichern
            visitStage.close();
            calendarStage.close();
        });

        // Elemente zum GridPane hinzuf&uuml;gen
        gridPane.add(bemerkungCheckBox, 0, 0);
        gridPane.add(bemerkungLabel, 0, 1);
        gridPane.add(bemerkungField, 0, 2);
        gridPane.add(hinzufuegenButton, 0, 3);

        // Szene erstellen und anzeigen
        Scene scene = new Scene(gridPane, 600, 400);
        visitStage.setScene(scene);
        visitStage.initModality(Modality.APPLICATION_MODAL);
        visitStage.showAndWait();
    }











    // Methode zum Anzeigen einer Warnung
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    private boolean isPersonInBlacklist(Person person) {
        return dbController.isPersonInBlacklist(person.getVorname(), person.getNachname());
    }






    // Methode zum Hinzuf&uuml;gen von Anmerkungen
    private void addAnmerkung(Familie familie) {
        Stage anmerkungStage = new Stage();
        anmerkungStage.setTitle("Anmerkung hinzuf\u00FCgen");
        anmerkungStage.initModality(Modality.APPLICATION_MODAL);

        GridPane gridPane = createGridPane();

        Label anmerkungLabel = new Label("Anmerkung:");
        anmerkungLabel.setStyle("-fx-font-size: 30px;");
        TextArea anmerkungArea = new TextArea();
        anmerkungArea.setPrefSize(500, 200);
        anmerkungArea.setStyle("-fx-font-size: 30px;");

        Button saveButton = new Button("Speichern");
        saveButton.setPrefSize(500, 100);
        saveButton.setStyle("-fx-font-size: 30px;");

        Button backButton = new Button("Zur\u00FCck");
        backButton.setPrefSize(500, 100);
        backButton.setStyle("-fx-font-size: 30px;");

        gridPane.add(anmerkungLabel, 0, 0);
        gridPane.add(anmerkungArea, 1, 0);
        gridPane.add(saveButton, 1, 1);
        gridPane.add(backButton, 0, 1);

        // Speichern der Anmerkung
        saveButton.setOnAction(e -> {
            String anmerkungText = anmerkungArea.getText();
            if (!anmerkungText.isEmpty()) {
                familie.setAnmerkungen(anmerkungText);
                dbController.addAnmerkungen(familie.getNummer(), anmerkungText);
                anmerkungStage.close();
            } else {
                System.out.println("Bitte eine Anmerkung eingeben.");
            }
        });

        // Fenster schlie&szlig;en
        backButton.setOnAction(e -> anmerkungStage.close());

        Scene scene = new Scene(gridPane, 600, 400);
        anmerkungStage.setScene(scene);
        anmerkungStage.setMaximized(true);
        anmerkungStage.showAndWait();
    }


    public void deleteFamilyByNumber(Stage primaryStage) {
        Stage deleteStage = createModalStage("Familie anhand der Nummer loeschen", primaryStage);

        GridPane gridPane = createGridPane();

        Label nummerLabel = new Label("Familiennummer:");
        nummerLabel.setStyle("-fx-font-size: 30px;");  // Schriftgr&ouml;&szlig;e f&uuml;r das Label anpassen

        TextField nummerField = new TextField();
        nummerField.setPrefSize(500, 100);  // Gr&ouml;&szlig;e des Textfelds anpassen
        nummerField.setStyle("-fx-font-size: 30px;");  // Schriftgr&ouml;&szlig;e f&uuml;r das Textfeld anpassen

        Button deleteButton = new Button("Loeschen");
        deleteButton.setPrefSize(500, 100);  // Gr&ouml;&szlig;e des Buttons anpassen
        deleteButton.setStyle("-fx-font-size: 30px;");  // Schriftgr&ouml;&szlig;e f&uuml;r den Button anpassen

        Button backButton = new Button("Zuruck");
        backButton.setPrefSize(500, 100);  // Gr&ouml;&szlig;e des Buttons anpassen
        backButton.setStyle("-fx-font-size: 30px;");  // Schriftgr&ouml;&szlig;e f&uuml;r den Button anpassen

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefSize(500, 200);  // Gr&ouml;&szlig;e des Textbereichs anpassen
        resultArea.setStyle("-fx-font-size: 30px;");  // Schriftgr&ouml;&szlig;e f&uuml;r den Textbereich anpassen


        gridPane.add(nummerLabel, 0, 0);
        gridPane.add(nummerField, 1, 0);
        gridPane.add(deleteButton, 1, 1);
        gridPane.add(backButton, 0, 1);
        gridPane.add(resultArea, 0, 2, 2, 1);

        deleteButton.setOnAction(e -> {
            String familienNummer = nummerField.getText();

            if (familienNummer.isEmpty()) {
                resultArea.setText("Bitte eine Familiennummer eingeben.");
                return;
            }

            // Suche die Familie anhand der Nummer
            Familie familie = dbController.searchByNumber(familienNummer);
            if (familie == null) {
                resultArea.setText("Keine Familie mit dieser Nummer gefunden.");
            } else {
                // Familie l&ouml;schen
                dbController.deleteFamily(familienNummer);
                resultArea.setText("Familie mit der Nummer " + familienNummer + " wurde gel\u00F6scht.");
            }
        });

        backButton.setOnAction(e -> deleteStage.close());

        Scene scene = new Scene(gridPane, 600, 400);
        deleteStage.setScene(scene);
        deleteStage.setMaximized(true);
        deleteStage.showAndWait();
    }



    // Methode zur Erstellung von Fenstern in einer eigenen Methode
    private Stage createModalStage(String title, Stage owner) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        return stage;
    }

    // Helper-Methode f&uuml;r Buttons
    private Button createButton(String text, int width) {
        Button button = new Button(text);
        button.setPrefWidth(width);
        return button;
    }

    // Helper-Methode f&uuml;r das GridPane
    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        return gridPane;
    }

    // Weitere Methoden zum Suchen, Anzeigen usw. bleiben unver&auml;ndert, k&ouml;nnen aber bei Bedarf angepasst werden.




// Method for adding a new family


    public void searchByNumber(Stage primaryStage) {
        Stage searchStage = new Stage();
        searchStage.setTitle("Nach Nummer suchen");
        searchStage.initModality(Modality.APPLICATION_MODAL);

        GridPane gridPane = createGridPane();

        Label nummerLabel = new Label("Nummer:");
        nummerLabel.setStyle("-fx-font-size: 30px;");

        TextField nummerField = new TextField();
        nummerField.setPrefSize(500, 100);
        nummerField.setStyle("-fx-font-size: 30px;");

        Button searchButton = new Button("Suchen");
        searchButton.setPrefSize(500, 100);
        searchButton.setStyle("-fx-font-size: 30px;");

        Button backButton = new Button("Zur\u00FCck");
        backButton.setPrefSize(500, 100);
        backButton.setStyle("-fx-font-size: 30px;");

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefSize(500, 200);
        resultArea.setStyle("-fx-font-size: 30px;");

        Button deleteButton = new Button("L\u00F6schen");
        deleteButton.setPrefSize(500, 100);
        deleteButton.setStyle("-fx-font-size: 30px; -fx-text-fill: red;");
        deleteButton.setVisible(false);

        Button anmerkungButton = new Button("Anmerkung hinzuf\u00FCgen");
        anmerkungButton.setPrefSize(500, 100);
        anmerkungButton.setStyle("-fx-font-size: 30px;");
        anmerkungButton.setVisible(false);

        gridPane.add(nummerLabel, 0, 0);
        gridPane.add(nummerField, 1, 0);
        gridPane.add(searchButton, 1, 1);
        gridPane.add(backButton, 0, 1);
        gridPane.add(resultArea, 0, 2, 2, 1);
        gridPane.add(deleteButton, 1, 3);
        gridPane.add(anmerkungButton, 1, 4);

        searchButton.setOnAction(e -> {
            String nummer = nummerField.getText();
            if (nummer.isEmpty()) {
                resultArea.setText("Bitte eine Nummer eingeben.");
                deleteButton.setVisible(false);
                anmerkungButton.setVisible(false);
                return;
            }

            Familie familie = dbController.searchByNumber(nummer);
            if (familie == null) {
                resultArea.setText("Keine Eintr\u00E4ge mit dieser Nummer gefunden.");
                deleteButton.setVisible(false);
                anmerkungButton.setVisible(false);
            } else {
                StringBuilder result = new StringBuilder();
                result.append("Familiennummer: ").append(familie.getNummer()).append("\n")
                        .append("Hauptperson: ").append(familie.getHauptperson().getVorname())
                        .append(" ").append(familie.getHauptperson().getNachname())
                        .append("\nAdresse: ").append(familie.getHauptperson().getAdresse())
                        .append("\nAnzahl der Kinder: ").append(familie.getAnzahlKinder()).append("\n")
                        .append("Anmerkungen: ").append(familie.getAnmerkungen()).append("\n");

                result.append("Familienmitglieder:\n");
                for (Person person : familie.getPersonen()) {
                    result.append("- ").append(person.getVorname()).append(" ")
                            .append(person.getNachname()).append(", Adresse: ")
                            .append(person.getAdresse()).append("\n");
                }

                result.append("Kinder:\n");
                for (Kinder kind : familie.getKinderList()) {
                    LocalDate geburtsdatum = kind.getGeburtsdatum();
                    LocalDate heute = LocalDate.now();

                    // Berechne das Alter in Jahren
                    int alter = Period.between(geburtsdatum, heute).getYears();

                    result.append("- Geschlecht: ").append(kind.getGeschlecht())
                            .append(", Alter: ").append(alter).append(" Jahre\n");
                }


                resultArea.setText(result.toString());

                deleteButton.setVisible(true);
                anmerkungButton.setVisible(true);

                anmerkungButton.setOnAction(addAnmerkungEvent -> addAnmerkung(familie));

                deleteButton.setOnAction(del -> {
                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Familie l\u00F6schen");
                    confirmationAlert.setHeaderText("Familie l\u00F6schen?");
                    confirmationAlert.setContentText("Wollen Sie wirklich die Familie mit der Nummer " + familie.getNummer() + " l\u00F6schen?");

                    DialogPane dialogPane = confirmationAlert.getDialogPane();
                    dialogPane.setStyle("-fx-font-size: 20px;");

                    confirmationAlert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            dbController.deleteFamily(familie.getNummer());
                            resultArea.setText("Familie mit der Nummer " + familie.getNummer() + " wurde gel\u00F6scht.");
                            deleteButton.setVisible(false);
                            anmerkungButton.setVisible(false);
                        }
                    });
                });
            }
        });

        backButton.setOnAction(e -> searchStage.close());

        Scene scene = new Scene(gridPane, 600, 400);
        searchStage.setScene(scene);
        searchStage.setMaximized(true);
        searchStage.showAndWait();
    }




    public void searchByLastName(Stage primaryStage) {
        Stage searchStage = new Stage();
        searchStage.setTitle("Nach Nachname suchen");
        searchStage.initModality(Modality.APPLICATION_MODAL);

        GridPane gridPane = createGridPane();

        Label nachnameLabel = new Label("Nachname:");
        nachnameLabel.setStyle("-fx-font-size: 30px;");

        TextField nachnameField = new TextField();
        nachnameField.setPrefSize(500, 100);
        nachnameField.setStyle("-fx-font-size: 30px;");

        Button searchButton = new Button("Suchen");
        searchButton.setPrefSize(500, 100);
        searchButton.setStyle("-fx-font-size: 30px;");

        Button backButton = new Button("Zur\u00FCck");
        backButton.setPrefSize(500, 100);
        backButton.setStyle("-fx-font-size: 30px;");

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefSize(500, 200);
        resultArea.setStyle("-fx-font-size: 30px;");

        gridPane.add(nachnameLabel, 0, 0);
        gridPane.add(nachnameField, 1, 0);
        gridPane.add(searchButton, 1, 1);
        gridPane.add(backButton, 0, 1);
        gridPane.add(resultArea, 0, 2, 2, 1);

        searchButton.setOnAction(e -> {
            String nachname = nachnameField.getText();
            if (nachname.isEmpty()) {
                resultArea.setText("Bitte einen Nachnamen eingeben.");
                return;
            }

            List<Familie> familien = dbController.searchByLastName(nachname);
            if (familien.isEmpty()) {
                resultArea.setText("Keine Eintr\u00E4ge mit diesem Nachnamen gefunden.");
            } else {
                StringBuilder result = new StringBuilder();
                for (Familie familie : familien) {
                    result.append("Familiennummer: ").append(familie.getNummer()).append("\n")
                            .append("Hauptperson: ").append(familie.getHauptperson().getVorname()).append(" ")
                            .append(familie.getHauptperson().getNachname()).append("\n")
                            .append("Adresse: ").append(familie.getHauptperson().getAdresse()).append("\n")
                            .append("Anzahl der Kinder: ").append(familie.getAnzahlKinder()).append("\n")
                            .append("Anmerkungen: ").append(familie.getAnmerkungen()).append("\n");

                    result.append("Familienmitglieder:\n");
                    for (Person person : familie.getPersonen()) {
                        result.append("- ").append(person.getVorname()).append(" ")
                                .append(person.getNachname()).append(", Adresse: ")
                                .append(person.getAdresse()).append("\n");
                    }

                    result.append("Kinder:\n");
                    for (Kinder kind : familie.getKinderList()) {
                        LocalDate geburtsdatum = kind.getGeburtsdatum();
                        LocalDate heute = LocalDate.now();

                        // Berechne das Alter in Jahren
                        int alter = Period.between(geburtsdatum, heute).getYears();

                        result.append("- Geschlecht: ").append(kind.getGeschlecht())
                                .append(", Alter: ").append(alter).append(" Jahre\n");
                    }


                    result.append("\n");
                }
                resultArea.setText(result.toString());
            }
        });

        backButton.setOnAction(e -> searchStage.close());

        Scene scene = new Scene(gridPane, 600, 400);
        searchStage.setScene(scene);
        searchStage.setMaximized(true);
        searchStage.showAndWait();
    }






    private void showAllFamilies(Stage primaryStage) {
        Stage allFamiliesStage = new Stage();
        allFamiliesStage.setTitle("Alle Familien anzeigen");

        GridPane gridPane = createGridPane();

        List<Familie> familien = dbController.getAllFamilies();

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefSize(1000, 600);
        resultArea.setStyle("-fx-font-size: 20px;");

        if (familien.isEmpty()) {
            resultArea.setText("Keine Familien gefunden.");
        } else {
            StringBuilder result = new StringBuilder();
            for (Familie familie : familien) {
                // Verwende die toString()-Methode von Familie, die bereits die Infos formatiert
                result.append(familie.toString()).append("\n\n");
            }
            resultArea.setText(result.toString());
        }

        Button backButton = new Button("Zur\u00FCck");
        backButton.setStyle("-fx-font-size: 30px;");  // Schriftgr&ouml;&szlig;e f&uuml;r den Button anpassen
        backButton.setPrefSize(400, 100);  // Setze die Breite auf 400 und die H&ouml;he auf 100
        backButton.setOnAction(e -> allFamiliesStage.close());

        gridPane.add(resultArea, 0, 0, 2, 1);  // Ergebnisanzeige &uuml;ber 2 Spalten
        gridPane.add(backButton, 0, 1);  // Button wird in Zeile 1, Spalte 0 platziert

        Scene scene = new Scene(gridPane, 800, 600);
        allFamiliesStage.setScene(scene);
        allFamiliesStage.setMaximized(true);
        allFamiliesStage.showAndWait();
    }







    public void searchByFullName(Stage primaryStage) {
        Stage searchStage = createModalStage("Nach Vor- und Nachnamen suchen", primaryStage);

        GridPane gridPane = createGridPane();

        Label vornameLabel = new Label("Vorname:");
        vornameLabel.setStyle("-fx-font-size: 30px;");  // Schriftgr&ouml;&szlig;e f&uuml;r das Label anpassen

        TextField vornameField = new TextField();
        vornameField.setPrefSize(500, 100);  // Gr&ouml;&szlig;e des Textfelds anpassen
        vornameField.setStyle("-fx-font-size: 30px;");  // Schriftgr&ouml;&szlig;e f&uuml;r das Textfeld anpassen

        Label nachnameLabel = new Label("Nachname:");
        nachnameLabel.setStyle("-fx-font-size: 30px;");  // Schriftgr&ouml;&szlig;e f&uuml;r das Label anpassen

        TextField nachnameField = new TextField();
        nachnameField.setPrefSize(500, 100);  // Gr&ouml;&szlig;e des Textfelds anpassen
        nachnameField.setStyle("-fx-font-size: 30px;");  // Schriftgr&ouml;&szlig;e f&uuml;r das Textfeld anpassen

        Button searchButton = new Button("Suchen");
        searchButton.setPrefSize(500, 100);  // Gr&ouml;&szlig;e des Buttons anpassen
        searchButton.setStyle("-fx-font-size: 30px;");  // Schriftgr&ouml;&szlig;e f&uuml;r den Button anpassen

        Button backButton = new Button("Zuruck");
        backButton.setPrefSize(500, 100);  // Gr&ouml;&szlig;e des Buttons anpassen
        backButton.setStyle("-fx-font-size: 30px;");  // Schriftgr&ouml;&szlig;e f&uuml;r den Button anpassen

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefSize(500, 200);  // Gr&ouml;&szlig;e des Textbereichs anpassen
        resultArea.setStyle("-fx-font-size: 30px;");  // Schriftgr&ouml;&szlig;e f&uuml;r den Textbereich anpassen


        gridPane.add(vornameLabel, 0, 0);
        gridPane.add(vornameField, 1, 0);
        gridPane.add(nachnameLabel, 0, 1);
        gridPane.add(nachnameField, 1, 1);
        gridPane.add(searchButton, 1, 2);
        gridPane.add(backButton, 0, 2);
        gridPane.add(resultArea, 0, 3, 2, 1);

        searchButton.setOnAction(e -> {
            String vorname = vornameField.getText();
            String nachname = nachnameField.getText();

            if (vorname.isEmpty() || nachname.isEmpty()) {
                resultArea.setText("Bitte sowohl Vor- als auch Nachnamen eingeben.");
                return;
            }

            // Fetch Familie objects from the database based on first and last name
            List<Familie> familien = dbController.searchByFullName(vorname, nachname);
            if (familien.isEmpty()) {
                resultArea.setText("Keine Eintraege mit diesem Vor- und Nachnamen gefunden.");
            } else {
                StringBuilder result = new StringBuilder();
                for (Familie familie : familien) {
                    result.append("Familiennummer: ").append(familie.getNummer()).append("\n")
                            .append("Hauptperson: ").append(familie.getHauptperson().getVorname()).append(" ")
                            .append(familie.getHauptperson().getNachname()).append("\n")
                            .append("Adresse: ").append(familie.getHauptperson().getAdresse()).append("\n")
                            .append("Anzahl der Kinder: ").append(familie.getAnzahlKinder()).append("\n");

                    result.append("Familienmitglieder:\n");
                    for (Person person : familie.getPersonen()) {
                        result.append("- ").append(person.getVorname()).append(" ")
                                .append(person.getNachname()).append(", Adresse: ")
                                .append(person.getAdresse())
                                //.append(", Blacklist: ").append(person.getBlackList() ? "Ja" : "Nein")
                                .append("\n");
                    }
                    result.append("\n"); // Separate each family entry
                }
                resultArea.setText(result.toString());
            }
        });

        backButton.setOnAction(e -> searchStage.close());

        Scene scene = new Scene(gridPane, 600, 400);
        searchStage.setScene(scene);
        searchStage.setMaximized(true);
        searchStage.showAndWait();
    }

    private LocalDate showBanDatePickerWindow() {
        // Neues Fenster erstellen
        Stage datePickerStage = new Stage();
        datePickerStage.setTitle("Bann-Datum ausw\u00E4hlen");

        // Layout
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));

        // Label f&uuml;r Erkl&auml;rung
        Label label = new Label("W\u00E4hlen Sie das Datum aus, bis wann die Familie gebannt werden soll:");
        label.setStyle("-fx-font-size: 18px;");

        // DatePicker f&uuml;r die Datumsauswahl
        DatePicker datePicker = new DatePicker();
        datePicker.setStyle("-fx-font-size: 18px;");
        datePicker.setValue(LocalDate.now()); // Setzt das aktuelle Datum als Standardwert

        // OK-Button
        Button okButton = new Button("OK");
        okButton.setPrefSize(100, 50);
        okButton.setStyle("-fx-font-size: 18px;");

        // Variable zum Speichern des ausgew&auml;hlten Datums
        final LocalDate[] selectedDate = new LocalDate[1];  // Array, um die Auswahl zu speichern

        // Aktion f&uuml;r den OK-Button
        okButton.setOnAction(e -> {
            selectedDate[0] = datePicker.getValue();  // Speichere das ausgew&auml;hlte Datum
            datePickerStage.close();  // Schlie&szlig;e das Fenster
        });

        vbox.getChildren().addAll(label, datePicker, okButton);

        Scene scene = new Scene(vbox, 400, 300);
        datePickerStage.setScene(scene);
        datePickerStage.initModality(Modality.APPLICATION_MODAL);  // Modales Fenster
        datePickerStage.showAndWait();  // Warte, bis das Fenster geschlossen wird

        return selectedDate[0];  // Gib das ausgew&auml;hlte Datum zur&uuml;ck
    }





    // Other search methods should use similar layout and styling adjustments

    public static void main(String[] args) {
        launch(args);
    }
}
