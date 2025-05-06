CREATE SCHEMA "TestSchema";

-- VARCHAR(MAX) should map to VARCHAR(2147483647)
CREATE TABLE "TestSchema"."VarCharTest"
(
    "Id" INT PRIMARY KEY,
    "text1" VARCHAR,
    "text2" VARCHAR(1),
    "text3" VARCHAR(255),
    "text4" VARCHAR(MAX)
);

INSERT INTO "TestSchema"."VarCharTest" ("Id", "text1", "text2", "text3", "text4")
VALUES
    (1, 'a', 'b', 'Regular text', 'This is a longer text that would be stored as VARCHAR(MAX)'),
    (2, 'c', 'd', 'Another text', 'Another longer text example'),
    (3, 'e', 'f', 'Third example', ''),  -- Empty VARCHAR(MAX)
    (4, 'g', 'h', 'Fourth example', NULL);  -- NULL VARCHAR(MAX)