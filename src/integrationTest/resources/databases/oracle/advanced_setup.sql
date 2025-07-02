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

-- Base and derived object types
CREATE OR REPLACE TYPE base_object AS OBJECT (
  id NUMBER,
  label VARCHAR2(100),
  MEMBER FUNCTION to_string RETURN VARCHAR2
) NOT FINAL;
/

CREATE OR REPLACE TYPE sub_object UNDER base_object (
  extra VARCHAR2(100),
  OVERRIDING MEMBER FUNCTION to_string RETURN VARCHAR2
);
/

-- Implement methods
CREATE OR REPLACE TYPE BODY base_object AS
  MEMBER FUNCTION to_string RETURN VARCHAR2 IS
BEGIN
RETURN 'ID=' || id || ', Label=' || label;
END;
END;
/

CREATE OR REPLACE TYPE BODY sub_object AS
  OVERRIDING MEMBER FUNCTION to_string RETURN VARCHAR2 IS
BEGIN
RETURN 'ID=' || id || ', Label=' || label || ', Extra=' || extra;
END;
END;
/

-- Collection type
CREATE OR REPLACE TYPE object_list AS TABLE OF base_object;
/

-- Table using object collection
CREATE TABLE complex_table (
                               collection_id NUMBER,
                               objects object_list
)
    NESTED TABLE objects STORE AS objects_nested;
/

-- Procedure that returns all object info
CREATE OR REPLACE PROCEDURE list_all_objects IS
  objs object_list;
BEGIN
SELECT objects INTO objs FROM complex_table WHERE collection_id = 1;

FOR i IN 1 .. objs.COUNT LOOP
    DBMS_OUTPUT.PUT_LINE(objs(i).to_string);
END LOOP;
END;
/

-- Simple table
CREATE TABLE simple_table (
                              id NUMBER,
                              value VARCHAR2(100)
);

INSERT INTO simple_table VALUES (1, 'Data A');
INSERT INTO simple_table VALUES (2, 'Data B');
COMMIT;

-- Insert into complex_table
DECLARE
o1 sub_object := sub_object(1, 'Main 1', 'Extra A');
  o2 base_object := base_object(2, 'Main 2');
BEGIN
INSERT INTO complex_table VALUES (1, object_list(o1, o2));
END;
/



-- ====================================
-- 2. Create Objects in TESTUSER schema
-- ====================================

-- Connect to otheruser (or use CURRENT_SCHEMA if running from sys)
ALTER SESSION SET CURRENT_SCHEMA = testuser;


  CREATE OR REPLACE PACKAGE overload_demo AS
  PROCEDURE log_msg(p_msg VARCHAR2);
  PROCEDURE log_msg(p_msg VARCHAR2, p_level NUMBER);
  PROCEDURE log_msg(p_msg VARCHAR2, p_level NUMBER, p_user VARCHAR2);
END overload_demo;
/

CREATE OR REPLACE PACKAGE BODY overload_demo AS
  PROCEDURE log_msg(p_msg VARCHAR2) IS
BEGIN
    DBMS_OUTPUT.PUT_LINE('Default: ' || p_msg);
END;

  PROCEDURE log_msg(p_msg VARCHAR2, p_level NUMBER) IS
BEGIN
    DBMS_OUTPUT.PUT_LINE('Level ' || p_level || ': ' || p_msg);
END;

  PROCEDURE log_msg(p_msg VARCHAR2, p_level NUMBER, p_user VARCHAR2) IS
BEGIN
    DBMS_OUTPUT.PUT_LINE('User ' || p_user || ' - Level ' || p_level || ': ' || p_msg);
END;
END overload_demo;
/
-- Base and derived object types
CREATE OR REPLACE TYPE base_object AS OBJECT (
  id NUMBER,
  label VARCHAR2(100),
  MEMBER FUNCTION to_string RETURN VARCHAR2
) NOT FINAL;
/

CREATE OR REPLACE TYPE sub_object UNDER base_object (
  extra VARCHAR2(100),
  OVERRIDING MEMBER FUNCTION to_string RETURN VARCHAR2
);
/

-- Implement methods
CREATE OR REPLACE TYPE BODY base_object AS
  MEMBER FUNCTION to_string RETURN VARCHAR2 IS
BEGIN
RETURN 'ID=' || id || ', Label=' || label;
END;
END;
/

CREATE OR REPLACE TYPE BODY sub_object AS
  OVERRIDING MEMBER FUNCTION to_string RETURN VARCHAR2 IS
BEGIN
RETURN 'ID=' || id || ', Label=' || label || ', Extra=' || extra;
END;
END;
/

-- Collection type
CREATE OR REPLACE TYPE object_list AS TABLE OF base_object;
/

-- Table using object collection
CREATE TABLE complex_table (
                               collection_id NUMBER,
                               objects object_list
)
    NESTED TABLE objects STORE AS objects_nested;
/

-- Procedure that returns all object info
CREATE OR REPLACE PROCEDURE list_all_objects IS
  objs object_list;
BEGIN
SELECT objects INTO objs FROM complex_table WHERE collection_id = 1;

FOR i IN 1 .. objs.COUNT LOOP
    DBMS_OUTPUT.PUT_LINE(objs(i).to_string);
END LOOP;
END;
/

-- Simple table
CREATE TABLE simple_table (
                              id NUMBER,
                              value VARCHAR2(100)
);

INSERT INTO simple_table VALUES (1, 'Data A');
INSERT INTO simple_table VALUES (2, 'Data B');
COMMIT;

-- Insert into complex_table
DECLARE
o1 sub_object := sub_object(1, 'Main 1', 'Extra A');
  o2 base_object := base_object(2, 'Main 2');
BEGIN
INSERT INTO complex_table VALUES (1, object_list(o1, o2));
END;
/

-- ====================================
-- 3. PUBLIC Grants (simulate risky config)
-- ====================================
GRANT EXECUTE ON base_object TO PUBLIC;
GRANT EXECUTE ON sub_object TO PUBLIC;
GRANT EXECUTE ON object_list TO PUBLIC;
GRANT EXECUTE ON list_all_objects TO PUBLIC;
GRANT SELECT ON simple_table TO PUBLIC;

-- ====================================
-- 4. Grant TESTUSER access to OTHERUSER objects
-- ====================================
GRANT SELECT ON otheruser.simple_table TO testuser;
GRANT EXECUTE ON otheruser.base_object TO testuser;
GRANT EXECUTE ON otheruser.sub_object TO testuser;
GRANT EXECUTE ON otheruser.object_list TO testuser;
GRANT EXECUTE ON otheruser.list_all_objects TO testuser;
--GRANT SELECT ON otheruser.complex_table TO testuser;

-- Optional: allow testuser to insert
GRANT INSERT ON otheruser.complex_table TO testuser;

COMMENT ON COLUMN testuser.simple_table.value IS 'The textual value associated with the ID.';
COMMENT ON TABLE testuser.simple_table IS 'This table stores simple key-value pairs.';

