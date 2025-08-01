ALTER SESSION SET CONTAINER=XEPDB1;

-- Create test user
CREATE USER test IDENTIFIED BY test;
GRANT CONNECT, RESOURCE, DBA TO test;
GRANT CREATE SESSION TO test;
GRANT UNLIMITED TABLESPACE TO test;

-- Create a simple table
CREATE TABLE TEST.SAMPLE_TABLE (
    ID NUMBER PRIMARY KEY,
    NAME VARCHAR2(100)
);

-- Insert test data
INSERT INTO TEST.SAMPLE_TABLE VALUES (1, 'Test');

-- Create a table with a ROWID column
CREATE TABLE TEST.ROWID_TABLE (
    ROW_NO NUMBER PRIMARY KEY,
    ROW_DATA ROWID
);

-- Insert ROWID data from the main table
INSERT INTO TEST.ROWID_TABLE (ROW_NO, ROW_DATA)
SELECT 1, ROWID FROM TEST.SAMPLE_TABLE WHERE ID = 1;