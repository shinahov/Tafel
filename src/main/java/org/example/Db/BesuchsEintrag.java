package org.example.Db;

import java.time.LocalDate;

public class BesuchsEintrag {
    private final LocalDate besuchsdatum;
    private final String bemerkung;

    public BesuchsEintrag(LocalDate besuchsdatum, String bemerkung) {
        this.besuchsdatum = besuchsdatum;
        this.bemerkung = bemerkung;
    }

    // Getter und Setter
    public LocalDate getBesuchsdatum() {
        return besuchsdatum;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    @Override
    public String toString() {
        return "Besuchsdatum: " + besuchsdatum + ", Bemerkung: " + bemerkung;
    }
}
