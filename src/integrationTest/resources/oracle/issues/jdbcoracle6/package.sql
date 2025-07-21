ALTER
    SESSION SET CONTAINER=XEPDB1;

CREATE
    USER testuser IDENTIFIED BY testpassword;

GRANT CREATE
    SESSION, CREATE TABLE, CREATE TYPE, CREATE PROCEDURE TO testuser;


ALTER
    SESSION SET CURRENT_SCHEMA = testuser;


-- at least one table is required - otherwise the archive step will fail
CREATE TABLE simple_table
(
    id    NUMBER,
    value VARCHAR2(100)
);

-- oracles packages, overloaded functions are currently not supported causing the exception with the following error:
-- java.io.IOException: Only one view with the same name allowed per schema!
-- note that the error message is misleading due to a copy paste error
CREATE
    OR REPLACE PACKAGE log_msg_overloaded AS
    PROCEDURE log_msg(p_msg VARCHAR2);
    PROCEDURE log_msg(p_msg VARCHAR2, p_level NUMBER);
    PROCEDURE log_msg(p_msg VARCHAR2, p_level NUMBER, p_user VARCHAR2);
END log_msg_overloaded;
/