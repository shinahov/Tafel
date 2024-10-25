package org.example.Db;

    public class Person {
        private String vorname;
        private String nachname;
        private String adresse;


        // Konstruktor
        public Person(String vorname, String nachname, String adresse) {
            this.vorname = vorname;
            this.nachname = nachname;
            this.adresse = adresse;
        }

        // Getter und Setter
        public String getVorname() {
            return vorname;
        }

        public void setVorname(String vorname) {
            this.vorname = vorname;
        }

        public String getNachname() {
            return nachname;
        }

        public void setNachname(String nachname) {
            this.nachname = nachname;
        }

        public String getAdresse() {
            return adresse;
        }

        public void setAdresse(String adresse) {
            this.adresse = adresse;
        }

        @Override
        public String toString() {
            return "Vorname: " + vorname + ", Nachname: " + nachname + ", Adresse: " + adresse;
        }
    }

