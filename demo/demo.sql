CREATE DATABASE company;
USE company;
CREATE TABLE employees (id INT PRIMARY KEY, name STRING, salary DOUBLE);
INSERT INTO employees VALUES(1, 'John', 50000);
INSERT INTO employees VALUES(2, 'Priya', 62000);
INSERT INTO employees VALUES(3, 'Amit', 45000);
SELECT * FROM employees;
SELECT name, salary FROM employees WHERE salary>48000 ORDER BY salary DESC;
UPDATE employees SET salary=70000 WHERE id=3;
SELECT * FROM employees ORDER BY salary DESC;
DELETE FROM employees WHERE id=1;
SELECT * FROM employees;
DESCRIBE employees;
SHOW TABLES;
DROP DATABASE company;
EXIT