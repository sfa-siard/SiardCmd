-- Schema creation
CREATE SCHEMA IF NOT EXISTS bitschema;
USE bitschema;

-- Table creation with different bit columns
CREATE TABLE bittest (
    id INT PRIMARY KEY,
    bit1 bit(1),        -- Single bit (0 or 1)
    bit8 bit(8),        -- 8 bits (0 to 255)
    bit64 bit(64)       -- 64 bits (maximum size)
);

-- Test data with different bit patterns
INSERT INTO bittest VALUES (1, b'1', b'10101010', b'1111111111111111111111111111111111111111111111111111111111111111');
INSERT INTO bittest VALUES (2, b'0', b'00000001', b'0000000000000000000000000000000000000000000000000000000000000001');
