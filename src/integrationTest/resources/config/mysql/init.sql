CREATE USER 'it_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON *.* TO 'it_user'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;