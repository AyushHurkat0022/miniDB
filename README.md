# MiniDB

A lightweight SQL database engine built from scratch in Java 17.

MiniDB is a learning-focused project that explores how database systems work internally by implementing core database components from scratch, including tokenization, parsing, execution, and persistent storage.

No Spring, Hibernate, JDBC-backed databases, ANTLR, or third-party SQL parsing libraries are used.

---

## Current Status

✅ Sprint 0 — Project Skeleton & CLI

✅ Sprint 1 — SQL Tokenizer

✅ Sprint 2 — Parser & Command Model

✅ Sprint 3 — Storage Engine & Persistence

🚧 Sprint 4 — Indexing (Next)

**Current Version:** `v0.4`

---

## Features Implemented

### Sprint 0 — Project Setup & CLI

- Maven-based Java 17 project setup
- Production-style package structure
- Interactive command-line interface (REPL)
- `HELP` command
- `EXIT` command
- Basic utility and configuration framework

### Sprint 1 — SQL Tokenizer

- SQL lexical analyzer
- Token classification:
  - Keywords
  - Identifiers
  - Numbers
  - Strings
  - Symbols
  - EOF marker
- Case-insensitive SQL keywords
- Support for:
  - String literals (`'John'`)
  - Numeric values (`123`, `50000.75`)
  - SQL punctuation and operators
- Comprehensive JUnit 5 tests

### Sprint 2 — Parser & Command Model

- Recursive-descent SQL parser
- Custom `SyntaxException`
- Command Pattern implementation
- Type-safe command objects
- Supported statements:
  - `CREATE DATABASE`
  - `USE`
  - `DROP DATABASE`
  - `CREATE TABLE`
  - `DROP TABLE`
  - `SHOW DATABASES`
  - `SHOW TABLES`
  - `DESCRIBE`
  - `INSERT INTO ... VALUES (...)`
  - `SELECT ... FROM ...`
  - `DELETE FROM ...`
- Column definition support:
  - `INT`
  - `STRING`
  - `DOUBLE`
  - `PRIMARY KEY`
- Parser unit tests

### Sprint 3 — Storage Engine

- File-based storage engine
- Persistent databases stored on disk
- Human-readable storage format
- Custom `StorageException`
- Database management:
  - Create database
  - Use database
  - Drop database
  - Show databases
- Table management:
  - Create table
  - Drop table
  - Show tables
  - Describe table
- Data operations:
  - Insert rows
  - Select rows
  - Delete all rows from a table
- Executor layer connecting Commands to storage operations
- End-to-end SQL execution pipeline

```text
SQL
 ↓
Tokenizer
 ↓
Parser
 ↓
Command
 ↓
Executor
 ↓
StorageEngine
 ↓
Disk