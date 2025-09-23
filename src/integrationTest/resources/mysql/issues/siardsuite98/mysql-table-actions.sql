--
-- Set character set the client will use to send SQL statements to the server
--
SET NAMES 'utf8';

--
-- Set default database
--
USE public;

--
-- Create table `actions`
--
CREATE TABLE actions
(
    aid        VARCHAR(255) NOT NULL DEFAULT '0' COMMENT 'Primary Key: Unique actions ID.',
    type       VARCHAR(32)  NOT NULL DEFAULT '' COMMENT 'The object that that action acts on (node, user, comment, system or custom types.)',
    callback   VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'The callback function that executes when the action runs.',
    parameters LONGBLOB     NOT NULL COMMENT 'Parameters to be passed to the callback function.',
    label      VARCHAR(255) NOT NULL DEFAULT '0' COMMENT 'Label of the action.',
    PRIMARY KEY (aid)
) ENGINE = INNODB,
  AVG_ROW_LENGTH = 1365,
  CHARACTER SET utf8mb3,
  COLLATE utf8mb3_general_ci,
    COMMENT = 'Stores action information.',
  ROW_FORMAT = DYNAMIC;