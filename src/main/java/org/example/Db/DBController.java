package org.example.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

    public class DBController {
        private final Connection connection;

        // Konstruktor, um die Verbindung zu erstellen
        public DBController() {
            this.connection = DatabaseConnector.connect();
        }

        // Methode zum Einfügen von Daten
        public ResultSet insertFamily(Familie familie) {
            //System.out.println(familie.getHauptperson().getNachname());
            ResultSet key = null;
            String sql = "INSERT INTO familien (nummer, vorname, nachname, adresse, anzahl_kinder) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, familie.getNummer());
                pstmt.setString(2, familie.getHauptperson().getVorname());
                pstmt.setString(3, familie.getHauptperson().getNachname());
                pstmt.setString(4, familie.getHauptperson().getAdresse());
                pstmt.setInt(5, familie.getAnzahlKinder()); // Anzahl der Kinder

                pstmt.executeUpdate();
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                key = generatedKeys;

                // Insert additional family members if any
                for (Person person : familie.getPersonen()) {
                    insertPerson(familie.getNummer(), person);
                }
            } catch (SQLException e) {
                System.out.println("Fehler beim Einfügen der Familie: " + e.getMessage());
            }
            return key;
        }

        public int getFamilyIdByNumber(String familyNumber) {
            String sql = "SELECT id FROM familien WHERE nummer = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, familyNumber);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return rs.getInt("id");  // Gibt die ID der Familie zurück
                }
            } catch (SQLException e) {
                System.out.println("Fehler beim Abrufen der Familien-ID: " + e.getMessage());
            }
            return -1;  // Gibt -1 zurück, wenn keine Familie gefunden wurde
        }


        public Familie findFamilyByPerson(String vorname, String nachname) {
            String sql = "SELECT f.nummer, f.vorname, f.nachname, f.adresse FROM personen p " +
                    "JOIN familien f ON p.familiennummer = f.nummer " +
                    "WHERE p.vorname = ? AND p.nachname = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, vorname);
                pstmt.setString(2, nachname);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    Person hauptperson = new Person(rs.getString("vorname"), rs.getString("nachname"), rs.getString("adresse"));
                    return new Familie(rs.getString("nummer"), hauptperson);  // Gibt die Familie zurück, falls die Person existiert
                }
            } catch (SQLException e) {
                System.out.println("Fehler beim Suchen nach der Person: " + e.getMessage());
            }
            return null;  // Gibt null zurück, wenn die Person nicht existiert
        }

        public List<Familie> findFamiliesByLastname(String nachname) {
            List<Familie> familien = new ArrayList<>();
            String sql = "SELECT f.nummer, f.vorname, f.nachname, f.adresse FROM personen p " +
                    "JOIN familien f ON p.familiennummer = f.nummer " +
                    "WHERE p.nachname = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, nachname);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    Person hauptperson = new Person(rs.getString("vorname"), rs.getString("nachname"), rs.getString("adresse"));
                    Familie familie = new Familie(rs.getString("nummer"), hauptperson);
                    familien.add(familie);  // Fügt die Familie zur Liste hinzu, falls der Nachname übereinstimmt
                }
            } catch (SQLException e) {
                System.out.println("Fehler beim Suchen nach Familien mit dem Nachnamen: " + e.getMessage());
            }
            return familien;  // Gibt die Liste zurück, die Familien enthalten kann oder leer sein kann
        }


        public void saveChild(Kinder kind, int familyId) {
            String sql = "INSERT INTO kinder (geschlecht, geburtsdatum, familien_id) VALUES (?, ?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, kind.getGeschlecht());  // Geschlecht setzen
                pstmt.setString(2, kind.getGeburtsdatum().toString());  // Geburtsdatum im Format YYYY-MM-DD
                pstmt.setInt(3, familyId);  // Familien-ID setzen

                pstmt.executeUpdate();
                System.out.println("Kind erfolgreich gespeichert.");
            } catch (SQLException e) {
                System.out.println("Fehler beim Speichern des Kindes: " + e.getMessage());
            }
        }

        public List<Kinder> getChildrenByFamilyId(int familyId) {
            String sql = "SELECT geschlecht, geburtsdatum FROM kinder WHERE familien_id = ?";
            List<Kinder> kinderList = new ArrayList<>();

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, familyId);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    String geschlecht = rs.getString("geschlecht");
                    LocalDate geburtsdatum = LocalDate.parse(rs.getString("geburtsdatum"));
                    Kinder kind = new Kinder(geschlecht, geburtsdatum);
                    kinderList.add(kind);
                }
            } catch (SQLException e) {
                System.out.println("Fehler beim Abrufen der Kinder: " + e.getMessage());
            }

            return kinderList;  // Gibt die Liste der Kinder zurück
        }




        public boolean isPersonInBlacklist(String vorname, String nachname) {
            String sql = "SELECT COUNT(*) FROM blacklist WHERE vorname = ? AND nachname = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, vorname);
                pstmt.setString(2, nachname);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return rs.getInt(1) > 0;  // Wenn der Wert größer als 0 ist, ist die Person in der Blacklist
                }
            } catch (SQLException e) {
                System.out.println("Fehler bei der Überprüfung der Blacklist: " + e.getMessage());
            }
            return false;
        }


        public void addPersonToBlacklist(Person person) {
            String sql = "INSERT INTO blacklist (vorname, nachname, adresse, familiennummer) VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                // Vorname und Nachname sind Pflichtfelder und dürfen nicht null sein
                pstmt.setString(1, person.getVorname());
                pstmt.setString(2, person.getNachname());

                // Wenn die Adresse null oder leer ist, wird sie in der Datenbank als null gespeichert
                if (person.getAdresse() != null && !person.getAdresse().isEmpty()) {
                    pstmt.setString(3, person.getAdresse());
                } else {
                    pstmt.setNull(3, java.sql.Types.VARCHAR);
                }

                // Da die Familiennummer in diesem Fall nicht verwendet wird, setzen wir sie auf null
                pstmt.setNull(4, java.sql.Types.VARCHAR);  // Familiennummer wird nicht gesetzt (null)

                pstmt.executeUpdate();
                System.out.println("Person wurde erfolgreich in die Blacklist aufgenommen.");
            } catch (SQLException e) {
                System.out.println("Fehler beim Hinzufügen der Person zur Blacklist: " + e.getMessage());
            }
        }


        // Methode zum Hinzufügen von Anmerkungen zu einer Familie
        public void addAnmerkungen(String familienNummer, String anmerkung) {
            String sql = "UPDATE familien SET anmerkungen = ? WHERE nummer = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, anmerkung);
                pstmt.setString(2, familienNummer);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Anmerkungen zur Familie mit der Nummer " + familienNummer + " wurden erfolgreich hinzugefügt.");
                } else {
                    System.out.println("Keine Familie mit der Nummer " + familienNummer + " gefunden.");
                }
            } catch (SQLException e) {
                System.out.println("Fehler beim Hinzufügen der Anmerkungen: " + e.getMessage());
            }
        }



        // Methode zum Einfügen einer zusätzlichen Person
        private void insertPerson(String familienNummer, Person person) {
            String sql = "INSERT INTO personen (familiennummer, vorname, nachname, adresse) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, familienNummer);
                pstmt.setString(2, person.getVorname());
                pstmt.setString(3, person.getNachname());
                pstmt.setString(4, person.getAdresse());
                //pstmt.setBoolean(5, person.getBlackList());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Fehler beim Einfügen der Person: " + e.getMessage());
            }
        }

        public List<Person> getAllBlacklistedPersons() {
            List<Person> blacklistedPersons = new ArrayList<>();
            String sql = "SELECT * FROM blacklist";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    Person person = new Person(rs.getString("vorname"), rs.getString("nachname"), rs.getString("adresse"));
                    blacklistedPersons.add(person);
                }
            } catch (SQLException e) {
                System.out.println("Fehler beim Abrufen der Blacklist-Personen: " + e.getMessage());
            }

            return blacklistedPersons;
        }

        public void removePersonFromBlacklist(String vorname, String nachname) {
            String sql = "DELETE FROM blacklist WHERE vorname = ? AND nachname = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, vorname);
                pstmt.setString(2, nachname);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Person erfolgreich aus der Blacklist entfernt.");
                } else {
                    System.out.println("Keine Übereinstimmung in der Blacklist gefunden.");
                }
            } catch (SQLException e) {
                System.out.println("Fehler beim Entfernen der Person aus der Blacklist: " + e.getMessage());
            }
        }


        public void deleteFamily(String familienNummer) {
            String sql = "DELETE FROM familien WHERE nummer = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, familienNummer);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Familie mit der Nummer " + familienNummer + " wurde erfolgreich gelöscht.");
                } else {
                    System.out.println("Keine Familie mit der Nummer " + familienNummer + "gefunden.");
                }
            } catch (SQLException e) {
                System.out.println("Fehler beim Löschen der Familie: " + e.getMessage());
            }
        }


        // Methode zum Suchen von Familien nach Vorname und Nachname
        // Methode zum Suchen von Familien nach Vor- und Nachname
        public List<Familie> searchByFullName(String vorname, String nachname) {
            List<Familie> familien = new ArrayList<>();
            String sql = "SELECT * FROM familien WHERE vorname LIKE ? AND nachname LIKE ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, "%" + vorname + "%");
                pstmt.setString(2, "%" + nachname + "%");
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    Familie familie = mapToFamilie(rs);
                    familie.setPersonen(getPersonenByFamilyNumber(familie.getNummer()));

                    // Hole die Familien-ID und füge die Kinder hinzu
                    int familienId = rs.getInt("id");
                    List<Kinder> kinderListe = getKinderByFamilyId(familienId);
                    familie.setKinderList(kinderListe);

                    familien.add(familie);
                }
            } catch (SQLException e) {
                System.out.println("Fehler bei der Suche nach Vor- und Nachname: " + e.getMessage());
            }
            return familien;
        }


        // Methode zum Suchen von Familien nach Nummer
        public Familie searchByNumber(String nummer) {
            String sql = "SELECT * FROM familien WHERE nummer = ?";
            Familie familie = null;
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, nummer);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    // Mappe die Familie
                    familie = mapToFamilie(rs);
                    familie.setPersonen(getPersonenByFamilyNumber(nummer));

                    // Hole die Familien-ID, um die Kinder zu holen
                    int familienId = rs.getInt("id");
                    List<Kinder> kinderListe = getKinderByFamilyId(familienId); // Kinder basierend auf der Familien-ID holen
                    familie.setKinderList(kinderListe); // Kinder zur Familie hinzufügen
                }
            } catch (SQLException e) {
                System.out.println("Fehler bei der Suche nach Nummer: " + e.getMessage());
            }
            return familie;
        }


        // Methode zum Suchen von Familien nach Nachname
        public List<Familie> searchByLastName(String nachname) {
            List<Familie> familien = new ArrayList<>();
            String sql = "SELECT * FROM familien WHERE nachname LIKE ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, "%" + nachname + "%");
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    Familie familie = mapToFamilie(rs);
                    familie.setPersonen(getPersonenByFamilyNumber(familie.getNummer()));

                    // Hole die Familien-ID und füge die Kinder hinzu
                    int familienId = rs.getInt("id");
                    List<Kinder> kinderListe = getKinderByFamilyId(familienId);
                    familie.setKinderList(kinderListe);

                    familien.add(familie);
                }
            } catch (SQLException e) {
                System.out.println("Fehler bei der Suche nach Nachname: " + e.getMessage());
            }
            return familien;
        }


        // Methode zum Abrufen aller Familien
        public List<Familie> getAllFamilies() {
            List<Familie> familien = new ArrayList<>();
            String sql = "SELECT * FROM familien";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    Familie familie = mapToFamilie(rs);
                    familie.setPersonen(getPersonenByFamilyNumber(familie.getNummer()));
                    familie.setKinderList(getKinderByFamilyId(rs.getInt("id"))); // Kinder hinzufügen
                    familien.add(familie);
                }
            } catch (SQLException e) {
                System.out.println("Fehler beim Abrufen aller Familien: " + e.getMessage());
            }
            return familien;
        }

        // Hilfsmethode zum Mappen eines ResultSets auf ein Familie-Objekt (mit Anmerkungen)
        private Familie mapToFamilie(ResultSet rs) throws SQLException {
            Person hauptperson = new Person(rs.getString("vorname"), rs.getString("nachname"), rs.getString("adresse"));
            Familie familie = new Familie(rs.getString("nummer"), hauptperson);
            familie.setAnzahlKinder(rs.getInt("anzahl_kinder"));
            familie.setAnmerkungen(rs.getString("anmerkungen"));  // Anmerkungen setzen
            return familie;
        }

        // Hilfsmethode zum Abrufen der Personen für eine Familie
        private List<Person> getPersonenByFamilyNumber(String familienNummer) {
            List<Person> personen = new ArrayList<>();
            String sql = "SELECT * FROM personen WHERE familiennummer = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, familienNummer);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    Person person = new Person(rs.getString("vorname"), rs.getString("nachname"), rs.getString("adresse"));
                    personen.add(person);
                }
            } catch (SQLException e) {
                System.out.println("Fehler beim Abrufen der Personen: " + e.getMessage());
            }
            return personen;
        }

        // Hilfsmethode zum Abrufen der Kinder für eine Familie (basierend auf familien_id)
        private List<Kinder> getKinderByFamilyId(int familienId) {
            List<Kinder> kinderList = new ArrayList<>();
            String sql = "SELECT * FROM kinder WHERE familien_id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, familienId);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    System.out.println("Geschlecht: " + rs.getString("geschlecht"));
                    System.out.println("Geburtsdatum (raw): " + rs.getString("geburtsdatum"));  // Zur Debugging-Zwecken
                    LocalDate geburtsdatum = LocalDate.parse(rs.getString("geburtsdatum"));
                    Kinder kind = new Kinder(rs.getString("geschlecht"), geburtsdatum);
                    kinderList.add(kind);
                }
            } catch (SQLException e) {
                System.out.println("Fehler beim Abrufen der Kinder: " + e.getMessage());
            }

            return kinderList;
        }



        public List<BesuchsEintrag> getVisitEntriesByFamilyId(int familienId) {
            List<BesuchsEintrag> visitEntries = new ArrayList<>();
            String sql = "SELECT besuchsdatum, anmerkung FROM besuchsprotokoll WHERE familien_id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, familienId);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    // Das Datum und die Bemerkung abrufen
                    String dateString = rs.getString("besuchsdatum");
                    LocalDate visitDate = LocalDate.parse(dateString);
                    String bemerkung = rs.getString("anmerkung");

                    // BesuchsEintrag erstellen und zur Liste hinzufügen
                    BesuchsEintrag eintrag = new BesuchsEintrag(visitDate, bemerkung);
                    visitEntries.add(eintrag);
                }
            } catch (SQLException e) {
                System.out.println("Fehler beim Abrufen der Besuchsdaten: " + e.getMessage());
            }

            return visitEntries;
        }


        public void saveVisit(int familyId, LocalDate visitDate) {
            String sql = "INSERT INTO besuchsprotokoll (familien_id, besuchsdatum) VALUES (?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, familyId);  // Setze die Familien-ID
                pstmt.setString(2, visitDate.toString());  // Setze das Besuchsdatum als String im Format YYYY-MM-DD

                pstmt.executeUpdate();
                System.out.println("Besuch erfolgreich gespeichert.");
            } catch (SQLException e) {
                System.out.println("Fehler beim Speichern des Besuchs: " + e.getMessage());
            }
        }

        public void saveVisit(int familienId, LocalDate besuchsdatum, String anmerkung) {
            String sql = "INSERT INTO besuchsprotokoll (familien_id, besuchsdatum, anmerkung) VALUES (?, ?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, familienId);
                pstmt.setString(2, besuchsdatum.toString());  // Konvertiere das LocalDate in String
                pstmt.setString(3, anmerkung);  // Anmerkung zum Besuch

                pstmt.executeUpdate();
                System.out.println("Besuch erfolgreich gespeichert.");
            } catch (SQLException e) {
                System.out.println("Fehler beim Speichern des Besuchs: " + e.getMessage());
            }
        }

        public void banFamily(int familyId, LocalDate banUntil) {
            String sql = "INSERT INTO bantemp (familien_id, ban_until) VALUES (?, ?)";

            try (Connection conn = DatabaseConnector.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, familyId);  // Setze die Familien-ID
                pstmt.setString(2, banUntil.toString());  // Speichere das Ban-Enddatum im Format YYYY-MM-DD
                pstmt.executeUpdate();
                System.out.println("Familie mit ID " + familyId + " bis zum " + banUntil + " gebannt.");
            } catch (SQLException e) {
                System.out.println("Fehler beim Bann der Familie: " + e.getMessage());
            }
        }


        public LocalDate getBanEndDateForFamily(int familyId) {
            String sql = "SELECT ban_until FROM bantemp WHERE familien_id = ?";
            LocalDate banEndDate = null;

            try (Connection conn = DatabaseConnector.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, familyId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    // Hole das Datum im Format 'YYYY-MM-DD' (als String)
                    String dateString = rs.getString("ban_until");
                    // Konvertiere das Datum in LocalDate
                    banEndDate = LocalDate.parse(dateString);  // Erwartet das Format 'YYYY-MM-DD'
                }
            } catch (SQLException e) {
                System.out.println("Fehler beim Abrufen des Bann-Enddatums: " + e.getMessage());
            } catch (DateTimeParseException e) {
                System.out.println("Fehler beim Parsen des Datums: " + e.getMessage());
            }

            return banEndDate;  // Gibt das Ban-Enddatum zurück oder null, wenn kein Ban existiert
        }

        public void unbanFamily(int familyId) {
            String sql = "DELETE FROM bantemp WHERE familien_id = ?";

            try (Connection conn = DatabaseConnector.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, familyId);  // Setze die Familien-ID
                int affectedRows = pstmt.executeUpdate();  // Lösche den Bann-Eintrag

                if (affectedRows > 0) {
                    System.out.println("Der Bann für die Familie mit ID " + familyId + " wurde aufgehoben.");
                } else {
                    System.out.println("Kein Bann für die Familie mit ID " + familyId + " gefunden.");
                }
            } catch (SQLException e) {
                System.out.println("Fehler beim Aufheben des Banns: " + e.getMessage());
            }
        }



        public int searchIdByNummer(String familienNummer) {
            String sql = "SELECT id FROM familien WHERE nummer = ?";
            int familyId = -1;

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, familienNummer);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    familyId = rs.getInt("id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return familyId;  // Gibt die ID zurück oder -1, wenn die Familie nicht gefunden wurde
        }

        public void deleteOldVisits(LocalDateTime currentDateTime) {
            String sql = "DELETE FROM besuchsprotokoll WHERE besuchsdatum < ?";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                // Berechne das Datum, das zwei Monate vor dem aktuellen Datum liegt
                LocalDateTime twoMonthsAgo = currentDateTime.minusMonths(2);

                // Setze den Parameter für das PreparedStatement mit dem formatieren Datum
                pstmt.setString(1, twoMonthsAgo.toString());

                // Führe das Löschen aus
                int affectedRows = pstmt.executeUpdate();
                System.out.println(affectedRows + " alte Besuchseinträge wurden gelöscht.");

            } catch (SQLException e) {
                System.out.println("Fehler beim Löschen alter Besuchseinträge: " + e.getMessage());
            }
        }

    }


