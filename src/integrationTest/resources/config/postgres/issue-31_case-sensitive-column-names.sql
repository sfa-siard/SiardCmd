-- Schritt 1: Tabelle mit case-sensitiven Spaltennamen erstellen
CREATE TABLE BeispielTabelle (
                                 "Name" VARCHAR(50) COLLATE "de_DE.utf8",
                                 "name" VARCHAR(50) COLLATE "de_DE.utf8",
                                 "Alter" INT
);

-- Schritt 2: Datensätze hinzufügen
INSERT INTO BeispielTabelle ("Name", "name", "Alter") VALUES
                                                          ('Max', 'max', 25),
                                                          ('John', 'john', 30),
                                                          ('Maria', 'maria', 28);