-- Schema creation
CREATE SCHEMA IF NOT EXISTS varcharschema;
SET
search_path TO varcharschema;

-- Table creation with different varchar/text types
CREATE TABLE varchartest
(
    id    INT PRIMARY KEY,
    text2 VARCHAR(1),
    text3 VARCHAR(255),
    text4 VARCHAR(8000),
    text5 TEXT -- PostgreSQL's TEXT type can store unlimited length text
);

-- Insert sample data
INSERT INTO varchartest (id, text2, text3, text4, text5)
VALUES (1, 'b', 'Regular text', 'Another longer text example',
        'This is a longer text that would be stored as VARCHAR(MAX)'),
       (2, 'd', 'Another text', 'Another longer text example',
        'This is a longer text that would be stored as VARCHAR(MAX)');
