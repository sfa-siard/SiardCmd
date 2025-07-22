-- Schema creation
CREATE SCHEMA IF NOT EXISTS precisiontypesschema;
SET
    search_path TO precisiontypesschema;

-- Table creation with different precision types
CREATE TABLE typed_precision_test
(
    id               INT PRIMARY KEY,

    -- VARCHAR/CHAR/TEXT variations
    col_varchar_1    VARCHAR(1),
    col_varchar_255  VARCHAR(255),
    col_varchar_8000 VARCHAR(8000),
    col_text         TEXT, -- PostgreSQL's TEXT type can store unlimited length text
    col_char_10      CHAR(10),

    -- Numeric types with scale/precision
    col_numeric_10_2 NUMERIC(10, 2),
    col_numeric_8    NUMERIC(8),
    col_integer      INTEGER,

    -- Temporal types with precision
    col_timestamp_3  TIMESTAMP(3),
    col_time_2       TIME(2)
);

-- Insert sample data
INSERT INTO typed_precision_test (id,
                                  col_varchar_1,
                                  col_varchar_255,
                                  col_varchar_8000,
                                  col_text,
                                  col_char_10,
                                  col_numeric_10_2,
                                  col_numeric_8,
                                  col_integer,
                                  col_timestamp_3,
                                  col_time_2)
VALUES (1,
        'X', -- VARCHAR(1)
        'This is varchar(255)', -- VARCHAR(255)
        'Longer varchar(8000)', -- VARCHAR(8000)
        'Unlimited text field', -- TEXT
        'abc', -- CHAR(10), padded with spaces

        12345.67, -- NUMERIC(10, 2)
        99999999, -- NUMERIC(8)
        42, -- INTEGER

        '2023-08-01 12:34:56.789', -- TIMESTAMP(3)
        '12:34:00.12' -- TIME(2)
       );