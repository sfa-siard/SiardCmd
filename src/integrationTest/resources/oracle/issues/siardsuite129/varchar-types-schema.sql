ALTER SESSION SET CONTAINER=XEPDB1;

CREATE TABLE IT_USER.varchartest (
    id NUMBER PRIMARY KEY,
    text2 VARCHAR2(1),
    text3 VARCHAR2(255),
    text4 VARCHAR2(4000)
);

INSERT INTO IT_USER.varchartest (id, text2, text3, text4)
VALUES
    (1, 'b', 'Regular text', 'Another longer text example');

INSERT INTO IT_USER.varchartest (id, text2, text3, text4)
VALUES
    (2, 'd', 'Another text', 'Another longer text example');