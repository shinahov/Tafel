package org.example.Test;



import org.example.Db.DatabaseConnector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class DatenbankTest {

    // Methode zum Überprüfen, ob die Tabelle 'familien' alle benötigten Spalten enthält
    public static boolean checkFamilyTableStructure() {
        Set<String> expectedColumns = new HashSet<>();
        expectedColumns.add("id");
        expectedColumns.add("nummer");
        expectedColumns.add("vorname");
        expectedColumns.add("nachname");
        expectedColumns.add("adresse");
        expectedColumns.add("anzahl_kinder");
        expectedColumns.add("anmerkungen");

        Set<String> actualColumns = new HashSet<>();

        String sql = "PRAGMA table_info(familien);";

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String columnName = rs.getString("name");
                actualColumns.add(columnName);
            }

        } catch (SQLException e) {
            System.out.println("Fehler beim Abrufen der Tabellenspalten: " + e.getMessage());
            return false;
        }

        // Überprüfen, ob alle erwarteten Spalten existieren
        return actualColumns.containsAll(expectedColumns);
    }

    // Hauptmethode zum Testen
    public static void main(String[] args) {
        if (checkFamilyTableStructure()) {
            System.out.println("Alle erforderlichen Spalten sind vorhanden.");
        } else {
            System.out.println("Es fehlen eine oder mehrere Spalten.");
        }
    }
}
