package org.example.Db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;


    public class FamilienTableManager {

        // Methode, um die Tabelle "familien" zu erstellen
        public static void createFamilyTable() {
            String sql = "CREATE TABLE IF NOT EXISTS familien (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nummer TEXT NOT NULL UNIQUE," +  // Familiennummer als eindeutiger Bezeichner
                    "vorname TEXT NOT NULL," +        // Hauptperson Vorname
                    "nachname TEXT NOT NULL," +       // Hauptperson Nachname
                    "adresse TEXT NOT NULL," +        // Hauptperson Adresse
                    "anzahl_kinder INTEGER," +        // Anzahl der Kinder
                    "anmerkungen TEXT" +              // Anmerkungen über die Familie
                    ");";

            // Verbindung zur Datenbank herstellen und Tabelle erstellen
            try (Connection conn = DatabaseConnector.connect();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Tabelle 'familien' erfolgreich erstellt.");
            } catch (SQLException e) {
                System.out.println("Fehler beim Erstellen der Tabelle 'familien': " + e.getMessage());
            }
        }

        public static void createKinderTable() {
            String sql = "CREATE TABLE IF NOT EXISTS kinder (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +       // Eindeutige ID für jedes Kind
                    "geschlecht TEXT NOT NULL," +                   // Geschlecht des Kindes
                    "geburtsdatum DATE NOT NULL," +                 // Geburtsdatum des Kindes (als DATE-Typ)
                    "familien_id INTEGER NOT NULL," +               // Fremdschlüssel zur Familie
                    "FOREIGN KEY (familien_id) REFERENCES familien(id) ON DELETE CASCADE" +  // Wenn die Familie gelöscht wird, werden auch die Kinder gelöscht
                    ");";

            // Verbindung zur Datenbank herstellen und Tabelle erstellen
            try (Connection conn = DatabaseConnector.connect();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Tabelle 'kinder' erfolgreich erstellt.");
            } catch (SQLException e) {
                System.out.println("Fehler beim Erstellen der Tabelle 'kinder': " + e.getMessage());
            }
        }



        // Methode, um die Tabelle "personen" zu erstellen
        public static void createPersonTable() {
            String sql = "CREATE TABLE IF NOT EXISTS personen (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "familiennummer TEXT NOT NULL," +  // Verweis auf die Familiennummer
                    "vorname TEXT NOT NULL," +
                    "nachname TEXT NOT NULL," +
                    "adresse TEXT NOT NULL," +
                    "FOREIGN KEY (familiennummer) REFERENCES familien(nummer) ON DELETE CASCADE" +
                    ");";

            // Verbindung zur Datenbank herstellen und Tabelle erstellen
            try (Connection conn = DatabaseConnector.connect();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Tabelle 'personen' erfolgreich erstellt.");
            } catch (SQLException e) {
                System.out.println("Fehler beim Erstellen der Tabelle 'personen': " + e.getMessage());
            }
        }

        public static void createVisitTable() {
            String sql = "CREATE TABLE IF NOT EXISTS besuchsprotokoll (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +              // Eindeutige ID für jeden Eintrag
                    "familien_id INTEGER NOT NULL," +                        // Fremdschlüssel zur Familien-ID
                    "besuchsdatum TEXT NOT NULL," +                          // Das Besuchsdatum im Format YYYY-MM-DD
                    "anmerkung TEXT," +                                      // Optionales Feld für Anmerkungen (z. B. Waschmittel)
                    "FOREIGN KEY (familien_id) REFERENCES familien(id) ON DELETE CASCADE" +  // Beziehung zu Familien-Tabelle
                    ");";

            // Verbindung zur Datenbank herstellen und Tabelle erstellen
            try (Connection conn = DatabaseConnector.connect();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Tabelle 'besuchsprotokoll' erfolgreich erstellt.");
            } catch (SQLException e) {
                System.out.println("Fehler beim Erstellen der Tabelle 'besuchsprotokoll': " + e.getMessage());
            }
        }


        // Methode, um die Tabelle "blacklist" zu erstellen
        public static void createBlacklistTable() {
            String sql = "CREATE TABLE IF NOT EXISTS blacklist (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "vorname TEXT NOT NULL," +
                    "nachname TEXT NOT NULL," +
                    "adresse TEXT," + // Adresse kann NULL sein
                    "familiennummer TEXT," +  // Optional, Foreign Key zur Familiennummer, kann NULL sein
                    "FOREIGN KEY (familiennummer) REFERENCES familien(nummer) ON DELETE SET NULL" +
                    ");";

            try (Connection conn = DatabaseConnector.connect();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Tabelle 'blacklist' erfolgreich erstellt.");
            } catch (SQLException e) {
                System.out.println("Fehler beim Erstellen der Tabelle 'blacklist': " + e.getMessage());
            }
        }

        public static void createBanTempTable() {
            String sql = "CREATE TABLE IF NOT EXISTS bantemp (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +              // Eindeutige ID für jeden Eintrag
                    "familien_id INTEGER NOT NULL," +                        // Fremdschlüssel zur Familien-ID
                    "ban_until TEXT NOT NULL," +                             // Datum, bis wann die Familie gebannt ist (YYYY-MM-DD)
                    "FOREIGN KEY (familien_id) REFERENCES familien(id) ON DELETE CASCADE" +  // Beziehung zur Familien-Tabelle
                    ");";

            // Verbindung zur Datenbank herstellen und Tabelle erstellen
            try (Connection conn = DatabaseConnector.connect();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Tabelle 'bantemp' erfolgreich erstellt.");
            } catch (SQLException e) {
                System.out.println("Fehler beim Erstellen der Tabelle 'bantemp': " + e.getMessage());
            }
        }

        public void createTabels(){
            createFamilyTable();    // Familien Tabelle erstellen
            createPersonTable();    // Personen Tabelle erstellen
            createBlacklistTable(); // Blacklist Tabelle erstellen
            createKinderTable();
            createBanTempTable();
            createVisitTable();
        }




        public static void main(String[] args) {
            // Tabellen erstellen
            createFamilyTable();    // Familien Tabelle erstellen
            createPersonTable();    // Personen Tabelle erstellen
            createBlacklistTable(); // Blacklist Tabelle erstellen
            createKinderTable();
            createBanTempTable();
            createVisitTable();     // Besuchsprotokoll Tabelle erstellen
        }
    }


