-- Create the referenced table
CREATE TABLE referenced_table
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create your table with a foreign key and CASCADE actions
CREATE TABLE your_table
(
    id            SERIAL PRIMARY KEY,
    referenced_id INT REFERENCES referenced_table (id) ON DELETE CASCADE ON UPDATE CASCADE,
    name          VARCHAR(255) NOT NULL
);

-- Create another table with a foreign key and SET NULL actions
CREATE TABLE another_table
(
    id            SERIAL PRIMARY KEY,
    referenced_id INT          REFERENCES referenced_table (id) ON DELETE SET NULL ON UPDATE SET NULL,
    name          VARCHAR(255) NOT NULL
);

-- Create yet another table with a foreign key and SET DEFAULT actions
CREATE TABLE additional_table
(
    id            SERIAL PRIMARY KEY,
    referenced_id INT REFERENCES referenced_table (id) ON DELETE SET DEFAULT ON UPDATE SET DEFAULT,
    name          VARCHAR(255) NOT NULL
);

-- Create one more table with a foreign key and RESTRICT/NO ACTION actions
CREATE TABLE last_table
(
    id            SERIAL PRIMARY KEY,
    referenced_id INT REFERENCES referenced_table (id) ON DELETE RESTRICT ON UPDATE NO ACTION,
    name          VARCHAR(255) NOT NULL
);

-- Example data for the referenced table
INSERT INTO referenced_table (name)
VALUES ('Referenced Element 1');
INSERT INTO referenced_table (name)
VALUES ('Referenced Element 2');
INSERT INTO referenced_table (name)
VALUES ('Referenced Element 3');

-- Example data for your table with CASCADE actions
INSERT INTO your_table (referenced_id, name)
VALUES (1, 'Your Element 1');
INSERT INTO your_table (referenced_id, name)
VALUES (2, 'Your Element 2');
INSERT INTO your_table (referenced_id, name)
VALUES (3, 'Your Element 3');

-- Example data for another table with SET NULL actions
INSERT INTO another_table (referenced_id, name)
VALUES (1, 'Another Element 1');
INSERT INTO another_table (referenced_id, name)
VALUES (2, 'Another Element 2');
-- Here, referenced_id is set to NULL as the referenced element was deleted

-- Example data for additional table with SET DEFAULT actions
INSERT INTO additional_table (referenced_id, name)
VALUES (1, 'Additional Element 1');
INSERT INTO additional_table (referenced_id, name)
VALUES (2, 'Additional Element 2');
-- Here, referenced_id is set to its default value as the referenced element was updated

-- Example data for last table with RESTRICT/NO ACTION actions
INSERT INTO last_table (referenced_id, name)
VALUES (1, 'Last Element 1');
INSERT INTO last_table (referenced_id, name)
VALUES (2, 'Last Element 2');
-- Since there are linked records, deleting or updating in the referenced table is blocked
