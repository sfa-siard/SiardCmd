ALTER SESSION SET CONTAINER=XEPDB1;

-- 1. Create User A
CREATE USER user_a IDENTIFIED BY password_a;
GRANT CREATE SESSION, CREATE TABLE TO user_a;

-- 2. Create User B
CREATE USER user_b IDENTIFIED BY password_b;
GRANT CREATE SESSION, CREATE TABLE TO user_b;

-- 3. Create table in USER_A schema
BEGIN
EXECUTE IMMEDIATE 'CREATE TABLE user_a.table_a (
                                                   id NUMBER PRIMARY KEY,
                                                   name VARCHAR2(100)
                   )';
END;
/

-- 4. Create table in USER_B schema
BEGIN
EXECUTE IMMEDIATE 'CREATE TABLE user_b.table_b (
                                                   id NUMBER PRIMARY KEY,
                                                   description VARCHAR2(200)
                   )';
END;
/

-- 5. Grant USER_A access to USER_B's table
GRANT SELECT ON user_b.table_b TO user_a;


-- 6. Create table in PUBLIC schema
BEGIN
EXECUTE IMMEDIATE 'CREATE TABLE public.persons (
                                                   id NUMBER PRIMARY KEY,
                                                   name VARCHAR2(200)
                   )';
END;
/

-- 7. Grant USER_A access to PUBLIC's table
GRANT SELECT ON public.persons TO user_a;