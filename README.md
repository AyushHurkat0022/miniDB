# MiniDB

A lightweight SQL database engine built from scratch in Java 17.

MiniDB is a learning-focused project that explores how database systems work internally by implementing core database components from scratch, including tokenization, parsing, execution, and persistent storage.

No Spring, Hibernate, JDBC-backed databases, ANTLR, or third-party SQL parsing libraries are used.

---

## Current Status

- ✅ Sprint 0 — Project Skeleton & CLI
- ✅ Sprint 1 — SQL Tokenizer
- ✅ Sprint 2 — Parser & Command Model
- ✅ Sprint 3 — Storage Engine & Persistence
- ✅ Sprint 4 — Full CRUD (UPDATE, DELETE with WHERE, Primary Key Enforcement)
- ✅ Sprint 5 — WHERE Operators & SELECT Filtering
- ✅ Sprint 6 — ORDER BY (ASC/DESC, type-aware sorting)
- ✅ Sprint 7 — Persistence Proof + Polish (CLI history, consistent errors, regression pass)
- 🚧 Sprint 8 — Compound Conditions (`AND` / `OR`) (Planned)

## Status
🚧 In development. Currently: v0.8 (WHERE, ORDER BY, full CRUD, persistence verified, CLI history).

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
  - SQL punctuation and operators (including two-character `>=` and `<=`)
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

### Sprint 4 — Full CRUD
- `UPDATE ... SET ... WHERE column = value` support
  - Single or multiple comma-separated assignments
    (`SET name='Jonathan', salary=70000`)
  - Omitting `WHERE` updates all rows
- `DELETE FROM ... WHERE column = value` support
  - Omitting `WHERE` deletes all rows
- `WhereClause` model
  - Single-condition matching
  - Matching logic encapsulated in `WhereClause.matches()` so it can be extended without changing callers
- Primary key uniqueness enforced on `INSERT`
  - Duplicate keys are rejected with a `StorageException`
- New `UpdateCommand`; `DeleteCommand` now carries an optional `WhereClause`
- Parser additions: `parseUpdate()` and a reusable `parseOptionalWhereClause()` helper
- Executor support for `UPDATE`, `DELETE` with `WHERE`, and PK validation on `INSERT`
- Parser and Executor unit tests covering the full CRUD lifecycle

### Sprint 5 — WHERE Operators & SELECT Filtering
- `WhereClause` now carries an operator and supports:
  - `=` (exact string equality)
  - `>`, `<`, `>=`, `<=` (numeric comparison)
- `SELECT ... FROM ... WHERE column <op> value` filtering
  - Works with `SELECT *` and with specific column lists
  - Omitting `WHERE` returns all rows
- `UPDATE` and `DELETE` gained numeric comparison support automatically, with no changes to their executor code, because all matching goes through `WhereClause.matches()`
- Parser additions:
  - `parseComparisonOperator()` validates the operator token
  - `parseOptionalWhereClause()` generalized to `WHERE <column> <operator> <value>`
  - `parseSelect()` now accepts an optional `WHERE`
- `SelectCommand` now carries an optional `WhereClause`
- Numeric operators on non-numeric values (e.g. `WHERE name > 50`) fail loudly with an error instead of silently returning no rows
- No tokenizer changes were needed; `>=` and `<=` were already single symbol tokens
- New isolated unit tests for `WhereClause` (no parser or storage involved) plus new parser tests for operator parsing

### Sprint 6 — ORDER BY
- `ORDER BY <column> [ASC|DESC]` support on `SELECT`
  - Defaults to `ASC` when no direction is given
  - Combines cleanly with `WHERE` and column projection in a single fixed pipeline: **filter → sort → project**
- New `OrderByClause` model — a plain data holder (column + direction), same pattern as `WhereClause`
- Type-aware sort comparator in the Executor:
  - Numeric comparison for `INT`/`DOUBLE` columns
  - Lexicographic (`String.compareTo`) comparison for `STRING` columns
  - Reads the column's declared type from `TableSchema` rather than guessing from the value
- Sorting happens **before** column projection, so `SELECT name FROM employees ORDER BY salary` correctly sorts by `salary` even though it isn't in the output
- Sorts on a defensive copy of the filtered row list rather than mutating in place
- `SelectCommand` now carries an optional `OrderByClause`
- Parser addition: `parseOptionalOrderByClause()`, following the same optional-clause chaining pattern established by `WHERE`
- New parser tests (ASC default, DESC, combined with WHERE) and an end-to-end Executor test verifying sort order against real stored rows

### Sprint 7 — Persistence Proof + Polish
- **Persistence formally verified**, not just assumed:
  - Full CLI restart (including a fresh terminal session) confirmed via `SELECT`, `SHOW TABLES`, and `DESCRIBE` that all data and schema survive
  - Raw `.meta` and `.data` files inspected directly with `cat` to confirm on-disk state independent of MiniDB's own output
  - Crash-recovery tested by `kill -9`'ing the process mid-session immediately after an `INSERT`; the previously committed row survived intact and uncorrupted, with no partial/corrupted writes
- **Command history** added to the CLI:
  - In-memory `commandHistory` list recorded on every non-empty input
  - New `HISTORY` command lists all commands run in the current session, numbered in order
- **Consistent error handling** across the CLI:
  - Dedicated `catch` blocks for `SyntaxException` (`[SYNTAX ERROR]`), `StorageException` (`[STORAGE ERROR]`), and `IllegalStateException` (`[EXECUTION ERROR]`), with a generic `[ERROR]` fallback
  - Audited all thrown exceptions across `Parser`, `StorageEngine`, and `Executor` to ensure messages name the specific offending table/column/value and never leak a raw stack trace or a `null` message
  - Verified edge cases (e.g. querying a nonexistent table) return a clean, specific error instead of an uncaught exception
- `HELP` text updated to reflect every feature through Sprint 6 (`WHERE` operators, `ORDER BY`, full CRUD, `HISTORY`), replacing the stale Sprint-0-era text
- **Regression pass**: full `mvn test` suite plus a manual end-to-end walkthrough exercising every feature together (create/use/drop DB and table, insert, filter, sort, update, delete, history) with no crashes or stack traces
- New full-pipeline integration test (`fullCrudLifecycleWorksTogether`) verifying `WHERE` + `ORDER BY` + projection + `UPDATE` + `DELETE` all cooperate correctly in combination — something isolated per-feature unit tests can't catch on their own

**Known limitations:**
- Only a single `WHERE` condition is supported (no `AND` / `OR` yet)
- Only a single `ORDER BY` column is supported (no multi-column sort, e.g. `ORDER BY dept, salary`)
- No `LIMIT` clause yet
- Every `UPDATE` and `DELETE` reads all rows and rewrites the whole table file (O(n)). This is correct but not optimized, and is not yet safe for concurrent access. A write-ahead log or in-place row updates would be a natural v2 improvement.
- `SELECT` with `WHERE` or `ORDER BY` performs a full table scan (no indexes)

---

## Persistence

MiniDB writes every INSERT, UPDATE, and DELETE directly to disk (`data/<database>/<table>.data`)
using `Files.writeString`, with no in-memory buffering between a command and its file write.

Verified manually:
- Inserted rows, exited the CLI completely, restarted, and confirmed all data and schema
  (via `SELECT`, `SHOW TABLES`, `DESCRIBE`) were intact.
- Force-killed the process (`kill -9`) immediately after a single INSERT to simulate a crash
  mid-session, restarted, and confirmed the previously committed row was intact and uncorrupted.

**Known limitation:** UPDATE and DELETE currently rewrite the entire `.data` file rather than
modifying a single row in place. This is correct but not efficient for very large tables — a
write-ahead log or in-place row updates would be a natural v2 improvement.

---

## Example Session

```sql
CREATE DATABASE company;
USE company;
CREATE TABLE employees (id INT PRIMARY KEY, name STRING, salary DOUBLE);
INSERT INTO employees VALUES(1, 'John', 50000);
INSERT INTO employees VALUES(2, 'Priya', 62000);
INSERT INTO employees VALUES(3, 'Amit', 45000);
INSERT INTO employees VALUES(1, 'Duplicate', 10000);  -- rejected: duplicate primary key

SELECT * FROM employees WHERE salary>50000;           -- Priya
SELECT * FROM employees WHERE salary<=50000;          -- John, Amit
SELECT name FROM employees WHERE id=2;                -- Priya

SELECT * FROM employees ORDER BY salary DESC;         -- Priya, John, Amit
SELECT name FROM employees WHERE salary>40000 ORDER BY salary ASC;  -- Amit, John, Priya

UPDATE employees SET salary=65000 WHERE id=1;
DELETE FROM employees WHERE salary<50000;             -- removes Amit
SELECT * FROM employees;
HISTORY
EXIT
```

---

## Supported WHERE Operators

| Operator | Meaning               | Comparison |
|----------|-----------------------|------------|
| `=`      | Equal                 | String     |
| `>`      | Greater than          | Numeric    |
| `<`      | Less than             | Numeric    |
| `>=`     | Greater than or equal | Numeric    |
| `<=`     | Less than or equal    | Numeric    |

## Supported ORDER BY

| Syntax                          | Behavior                                      |
|----------------------------------|------------------------------------------------|
| `ORDER BY column`                | Ascending (default), type-aware comparison    |
| `ORDER BY column ASC`            | Ascending, explicit                           |
| `ORDER BY column DESC`           | Descending                                    |

`ORDER BY` combines freely with `WHERE` and column projection — evaluation order is always **filter → sort → project**.

## CLI Commands

| Command    | Behavior                                   |
|------------|---------------------------------------------|
| `HELP`     | Lists all available commands                |
| `HISTORY`  | Lists every command run so far this session |
| `EXIT`     | Exits the CLI                               |

## Error Message Format

| Prefix               | Thrown by                          |
|-----------------------|-------------------------------------|
| `[SYNTAX ERROR]`      | `SyntaxException` (parser)          |
| `[STORAGE ERROR]`     | `StorageException` (storage engine) |
| `[EXECUTION ERROR]`   | `IllegalStateException` (executor)  |
| `[ERROR]`             | Any other uncaught exception        |

---

## Architecture

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
```