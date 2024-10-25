package org.example.Db;

import java.util.ArrayList;
import java.util.List;

    public class Familie {
        private String nummer;
        private Person hauptperson;
        private List<Person> personen;
        private int anzahlKinder;
        private String anmerkungen; // Neue Eigenschaft für Anmerkungen (Kommentare)
        private List<Kinder> kinderList; // Liste von Kindern

        // Konstruktor
        public Familie(String nummer, Person hauptperson) {
            this.nummer = nummer;
            this.hauptperson = hauptperson;
            this.personen = new ArrayList<>();
            this.personen.add(hauptperson); // Hauptperson hinzufügen
            this.kinderList = new ArrayList<>(); // Kinderliste initialisieren
        }

        // Methode zum Hinzufügen weiterer Personen
        public void addPerson(Person person) {
            this.personen.add(person);
        }

        // Methode zum Hinzufügen von Kindern
        public void addKind(Kinder kind) {
            this.kinderList.add(kind);
        }

        // Getter und Setter
        public String getNummer() {
            return nummer;
        }

        public void setNummer(String nummer) {
            this.nummer = nummer;
        }

        public Person getHauptperson() {
            return hauptperson;
        }

        public void setHauptperson(Person hauptperson) {
            this.hauptperson = hauptperson;
        }

        public List<Person> getPersonen() {
            return personen;
        }

        public void setPersonen(List<Person> personen) {
            this.personen = personen;
        }

        public int getAnzahlKinder() {
            return anzahlKinder;
        }

        public void setAnzahlKinder(int anzahlKinder) {
            this.anzahlKinder = anzahlKinder;
        }

        public List<Kinder> getKinderList() {
            return kinderList;
        }

        public void setKinderList(List<Kinder> kinderList) {
            this.kinderList = kinderList;
        }

        public String getAnmerkungen() {
            return anmerkungen;
        }

        public void setAnmerkungen(String anmerkungen) {
            this.anmerkungen = anmerkungen;
        }


        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Familiennummer: ").append(nummer).append("\n");
            sb.append("Hauptperson: ").append(hauptperson.toString()).append("\n");

            // Anzahl der Kinder anzeigen, wenn vorhanden
            if (anzahlKinder > 0) {
                sb.append("Anzahl der Kinder: ").append(anzahlKinder).append("\n");
            }

            // Anmerkungen nur anzeigen, wenn sie nicht null oder leer sind
            if (anmerkungen != null && !anmerkungen.trim().isEmpty()) {
                sb.append("Anmerkungen: ").append(anmerkungen).append("\n");
            }

            // Weitere Personen anzeigen, wenn es welche gibt und die Hauptperson nicht enthalten ist
            if (personen != null && !personen.isEmpty()) {
                sb.append("Weitere Personen:\n");
                for (Person person : personen) {
                    // Stelle sicher, dass die Hauptperson nicht in der Liste der weiteren Personen vorkommt
                    if (!person.equals(hauptperson)) {
                        sb.append("- ").append(person).append("\n");
                    }
                }
            }

            // Kinder anzeigen, wenn es welche gibt
            if (kinderList != null && !kinderList.isEmpty()) {
                sb.append("Kinder:\n");
                for (Kinder kind : kinderList) {
                    sb.append("- ").append(kind.toString()).append("\n");
                }
            }

            return sb.toString();
        }


    }

