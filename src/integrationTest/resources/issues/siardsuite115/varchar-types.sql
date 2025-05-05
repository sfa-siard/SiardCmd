-- Create a test schema
CREATE SCHEMA "TestSchema";

-- Create a table with different VARCHAR types
CREATE TABLE "TestSchema"."VarCharTest"
(
    "Id" INT PRIMARY KEY,
    "text1" VARCHAR,         -- Default VARCHAR with no size specified
    "text2" VARCHAR(1),      -- VARCHAR with size 1
    "text3" VARCHAR(255),    -- VARCHAR with size 255
    "text4" VARCHAR(MAX)     -- VARCHAR(MAX) which should map to VARCHAR(2147483647)
);

-- Insert some test data
INSERT INTO "TestSchema"."VarCharTest" ("Id", "text1", "text2", "text3", "text4")
VALUES
    (1, 'a', 'b', 'Regular text', 'This is a longer text that would be stored as VARCHAR(MAX)'),
    (2, 'c', 'd', 'Another text', 'Another longer text example'),
    (3, 'e', 'f', 'Third example', ''),  -- Empty VARCHAR(MAX) to test empty file creation
    (4, 'g', 'h', 'Fourth example', NULL);  -- NULL VARCHAR(MAX) to test handling