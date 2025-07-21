ALTER
    SESSION SET CONTAINER=XEPDB1;

CREATE
    USER testuser IDENTIFIED BY testpassword;
GRANT CREATE
    SESSION, CREATE TABLE, CREATE TYPE, CREATE PROCEDURE TO testuser;


ALTER
    SESSION SET CURRENT_SCHEMA = testuser;


CREATE TABLE simple_table
(
    id    NUMBER,
    value VARCHAR2(100)
);

CREATE
    OR REPLACE PACKAGE overload_demo AS
    PROCEDURE log_msg(p_msg VARCHAR2);
    PROCEDURE log_msg(p_msg VARCHAR2, p_level NUMBER);
    PROCEDURE log_msg(p_msg VARCHAR2, p_level NUMBER, p_user VARCHAR2);
END overload_demo;
/