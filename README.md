TafelAnwendung
Projektbeschreibung
Die TafelAnwendung ist eine Desktop-Anwendung, die mithilfe von JavaFX und SQLite entwickelt wurde. Die Anwendung dient der Verwaltung von Familien, die regelmäßig die Dienste einer Tafel in Anspruch nehmen. Dabei können Familienmitglieder erfasst, Besuche protokolliert und spezifische Hinweise wie z.B. Blacklists berücksichtigt werden.

Hauptfunktionen:
Familienverwaltung: Neue Familien und deren Mitglieder können hinzugefügt, bearbeitet und gelöscht werden.
Besuchsprotokoll: Besuchsaufzeichnungen der Familien werden automatisch erfasst und ältere Einträge können regelmäßig bereinigt werden.
Blacklist-Prüfung: Personen auf einer Blacklist werden beim Eintragen eines Besuchs gewarnt.
Automatische Löschung: Alte Besuchsprotokolle (älter als zwei Monate) können auf Knopfdruck gelöscht werden.
Architektur
Das Projekt besteht aus mehreren Schichten:

Frontend (UI): Die Benutzeroberfläche basiert auf JavaFX, um eine interaktive und benutzerfreundliche Oberfläche zu bieten.
Datenbank: Die Datenbank basiert auf SQLite, um lokal Besuche und Familieninformationen zu speichern.
Controller: Es gibt verschiedene Controller-Klassen, die die Logik zwischen der UI und der Datenbank steuern.
Voraussetzungen
Java 11 oder höher muss installiert sein.
JavaFX muss in das Projekt eingebunden werden, um die grafische Benutzeroberfläche zu ermöglichen.
SQLite wird als Datenbanksystem verwendet.
Weiterentwicklung
Diese Anwendung könnte zukünftig um zusätzliche Funktionen wie Berichte, erweiterte Blacklist-Funktionen und eine verbesserte Benutzeroberfläche erweitert werden.
