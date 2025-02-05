create database notes;
use notes;

CREATE TABLE users
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(32) NOT NULL,
    last_name VARCHAR(32) NOT NULL,
    password VARCHAR(256) NOT NULL,
    email VARCHAR(64) UNIQUE NOT NULL
);

CREATE TABLE notes
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(64) NOT NULL,
    content VARCHAR(8192) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);