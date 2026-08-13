# MiniDB

A lightweight SQL database engine built from scratch in Java 17.

MiniDB is a learning-focused project that explores how database systems work internally by implementing core components from scratch, including:

- Command-line interface (CLI)
- SQL tokenization
- Parsing
- Storage engine
- Indexing
- Transactions
- Concurrency control

No Spring, Hibernate, JDBC databases, or third-party parsing libraries are used.

---
## Current Status

✅ Sprint 0 — Project Skeleton & CLI

✅ Sprint 1 — SQL Tokenizer

✅ Sprint 2 — Parser & Command Model

✅ Sprint 3 — Storage Engine

🚧 Sprint 4 — Indexing (Next)

**Current Version:** `v0.4`

Current Version: **v0.4**

---

## Features Implemented

### Sprint 0
- Maven-based Java 17 project setup
- Production-style package structure
- Interactive REPL shell
- `HELP` command
- `EXIT` command
- Basic logging utility

### Sprint 1
- SQL tokenizer (lexical analyzer)
- Token classification:
  - Keywords
  - Identifiers
  - Numbers
  - Strings
  - Symbols
  - EOF marker
- Keyword lookup system
- Support for:
  - String literals (`'John'`)
  - Decimal numbers (`65000.50`)
  - Comparison operators (`>=`, `<=`)
- JUnit 5 test suite

### Sprint 2
- Recursive-descent SQL parser
- Custom `SyntaxException` for clean error handling
- Command Pattern: one type-safe class per statement
- Supported statements:
  - `CREATE DATABASE`
  - `USE`
  - `DROP DATABASE`
  - `DROP TABLE`
  - `SHOW DATABASES`
  - `SHOW TABLES`
  - `DESCRIBE`
  - `CREATE TABLE` (with column definitions and `PRIMARY KEY`)
  - `INSERT INTO ... VALUES (...)`
  - `SELECT` (`*` or specific columns) `... FROM ...`
  - `DELETE FROM` (whole-table only — `WHERE` support arrives in Sprint 5)
- Expanded JUnit 5 test suite covering all statement types and error cases

### Sprint 3
- File-based storage engine — no embedded database libraries
- Custom `StorageException` for clean error handling
- Databases stored as directories under `data/`
- Tables stored as a `.schema` file (column definitions) and a `.tbl` file (rows)
- `DatabaseStore` — create, drop, list, and check existence of databases
- `SchemaStore` — persist and read back column definitions
- `TableStore` — append, read, clear, and delete rows
- `Executor` — wires parsed `Command` objects to the storage layer:
  - `CREATE DATABASE` / `DROP DATABASE` / `SHOW DATABASES` / `USE`
  - `CREATE TABLE` / `DROP TABLE` / `DESCRIBE`
  - `INSERT` with column-count validation against the schema
  - `SELECT` with column projection (`*` or named columns)
  - `DELETE` (clears all rows in a table)
- CLI now executes commands end-to-end and persists data across restarts
- Integration test suite verifying data survives across storage operations

---

## Project Structure

```text
src/
├── main/java/com/minidb
│   ├── cli
│   ├── tokenizer
│   ├── parser
│   ├── command
│   ├── executor
│   ├── storage
│   ├── transaction
│   ├── index
│   ├── concurrency
│   ├── model
│   ├── exception
│   ├── util
│   └── config
│
└── test/java/com/minidb

data/                     # generated at runtime — one directory per database
├── company/
│   ├── employees.tbl
│   ├── employees.schema
│   └── departments.tbl
└── school/
    └── students.tbl
```

---

## How It Works So Far

A raw SQL string is fed to the `Tokenizer`, which produces a flat list of tokens (keywords, identifiers, symbols, numbers, strings). The `Parser` then walks that list left to right using recursive descent, dispatching to a dedicated method per statement type, and builds a typed `Command` object (e.g. `SelectCommand`, `InsertCommand`, `CreateTableCommand`).

The `Executor` takes that `Command` and carries it out against the storage layer — creating database directories, writing schema and row files, and reading/updating them on subsequent statements. Data written in one CLI session is still there the next time you launch it.

Indexing, transactions, and concurrency control are not implemented yet — every read currently does a full table scan, and there's no rollback or isolation between operations. Those arrive in later sprints.

---

## Example Session

```
> CREATE DATABASE company;
Database created: company
> USE company;
Using database: company
> CREATE TABLE employees (id INT PRIMARY KEY, name STRING, salary DOUBLE);
Table created: employees
> INSERT INTO employees VALUES(1, 'John', 50000);
1 row inserted into employees
> SELECT * FROM employees;
1 | John | 50000
```

---

## Running Tests

```bash
mvn test
```