ALTER SESSION SET CONTAINER=XEPDB1;
-- ====================================
-- 1. Create Users
-- ====================================
CREATE USER testuser IDENTIFIED BY testpassword;
CREATE USER otheruser IDENTIFIED BY otherpassword;

GRANT CREATE SESSION, CREATE TABLE, CREATE TYPE, CREATE PROCEDURE TO testuser;
GRANT CREATE SESSION, CREATE TABLE, CREATE TYPE, CREATE PROCEDURE TO otheruser;

-- Optionally allow them to store data
-- GRANT UNLIMITED TABLESPACE TO testuser;
-- GRANT UNLIMITED TABLESPACE TO otheruser;

-- ====================================
-- 2. Create Objects in OTHERUSER schema
-- ====================================

-- Connect to otheruser (or use CURRENT_SCHEMA if running from sys)
ALTER SESSION SET CURRENT_SCHEMA = otheruser;

CREATE TABLE simple_table (
                              id NUMBER,
                              value VARCHAR2(100)
);

ALTER SESSION SET CURRENT_SCHEMA = testuser;

CREATE TABLE simple_table (
                              id NUMBER,
                              value VARCHAR2(100)
);

CREATE OR REPLACE PACKAGE overload_demo AS
  PROCEDURE log_msg(p_msg VARCHAR2);
  PROCEDURE log_msg(p_msg VARCHAR2, p_level NUMBER);
  PROCEDURE log_msg(p_msg VARCHAR2, p_level NUMBER, p_user VARCHAR2);
END overload_demo;

GRANT INSERT ON otheruser.simple_table TO testuser; -- INSERT instead of SELECT
