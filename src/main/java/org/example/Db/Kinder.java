package org.example.Db;

import java.time.LocalDate;
import java.time.Period;


public class Kinder {

    private String geschlecht;  // Geschlecht des Kindes
    private LocalDate geburtsdatum;  // Geburtsdatum des Kindes

    // Konstruktor
    public Kinder(String geschlecht, LocalDate geburtsdatum) {
        this.geschlecht = geschlecht;
        this.geburtsdatum = geburtsdatum;
    }

    // Getter für das Geschlecht
    public String getGeschlecht() {
        return geschlecht;
    }

    // Setter für das Geschlecht
    public void setGeschlecht(String geschlecht) {
        this.geschlecht = geschlecht;
    }

    // Getter für das Geburtsdatum
    public LocalDate getGeburtsdatum() {
        return geburtsdatum;
    }

    // Setter für das Geburtsdatum
    public void setGeburtsdatum(LocalDate geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }

    // Methode zur Berechnung des Alters basierend auf dem Geburtsdatum
    public int getAlter() {
        if (geburtsdatum == null) {
            return 0;  // Falls kein Geburtsdatum gesetzt ist
        }
        return Period.between(geburtsdatum, LocalDate.now()).getYears();  // Alter in Jahren
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Geschlecht: ").append(geschlecht).append(", ");

        // Geburtsdatum formatieren, falls vorhanden
        if (geburtsdatum != null) {
            sb.append("Geburtsdatum: ").append(geburtsdatum).append(", ");
        }

        // Alter anzeigen, wenn das Geburtsdatum gesetzt ist
        if (geburtsdatum != null) {
            sb.append("Alter: ").append(getAlter()).append(" Jahre");
        }

        return sb.toString();
    }

}

