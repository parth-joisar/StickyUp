CREATE DATABASE IF NOT EXISTS stickyup
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'stickyup_user'@'localhost' IDENTIFIED BY '1234';
ALTER USER 'stickyup_user'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON stickyup.* TO 'stickyup_user'@'localhost';
FLUSH PRIVILEGES;