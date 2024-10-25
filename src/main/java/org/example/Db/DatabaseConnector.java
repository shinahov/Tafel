package org.example.Db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    // Methode zum Herstellen der Verbindung zur SQLite-Datenbank
    public static Connection connect() {
        Connection connection = null;
        try {
            // SQLite connection string
            String url = "jdbc:sqlite:familien.db"; // Pfad zur SQLite-Datenbank anpassen
            connection = DriverManager.getConnection(url);
            System.out.println("Verbindung zur Datenbank erfolgreich.");
        } catch (SQLException e) {
            System.out.println("Fehler bei der Verbindung zur Datenbank: " + e.getMessage());
        }
        return connection;
    }


}
