CREATE SCHEMA file_types;

-- Create table to store all files
CREATE TABLE file_types.all_files (
    id SERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_data BYTEA NOT NULL
);

-- Create table for PDF files
CREATE TABLE file_types.pdf_files (
    id SERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    file_data BYTEA NOT NULL
);

-- Create table for JPG files
CREATE TABLE file_types.jpg_files (
    id SERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    file_data BYTEA NOT NULL
);
