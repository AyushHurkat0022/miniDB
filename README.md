# MiniDB

A lightweight SQL database engine built from scratch in **Java 17** — no Spring, no Hibernate, no JDBC-backed databases, no ANTLR, no third-party SQL parsing libraries.

MiniDB is a learning-focused project that explores how database systems work internally by implementing core database components from scratch: tokenization, parsing, query execution, and persistent file-based storage.

---

## Status

**v1.0** — Core SQL engine complete: full CRUD, WHERE operators, ORDER BY, verified persistence, packaged as a runnable jar.

- ✅ Sprint 0 — Project Skeleton & CLI
- ✅ Sprint 1 — SQL Tokenizer
- ✅ Sprint 2 — Parser & Command Model
- ✅ Sprint 3 — Storage Engine & Persistence
- ✅ Sprint 4 — Full CRUD (UPDATE, DELETE with WHERE, Primary Key Enforcement)
- ✅ Sprint 5 — WHERE Operators & SELECT Filtering
- ✅ Sprint 6 — ORDER BY (ASC/DESC, type-aware sorting)
- ✅ Sprint 7 — Persistence Proof + Polish (CLI history, consistent errors, regression pass)
- ✅ Sprint 8 — Documentation + v1.0 Release (packaged jar, demo script, design notes)
- 🔜 v1.1 — Hash indexing
- 🔜 v1.2 — Transactions (BEGIN / COMMIT / ROLLBACK)
- 🔜 v1.3 — Concurrency (read/write locks, thread pool)

---

## Features

| Feature | Status |
|---|---|
| CREATE / USE / DROP / SHOW DATABASES | ✅ |
| CREATE / DROP / SHOW / DESCRIBE TABLE | ✅ |
| INSERT (with primary key uniqueness check) | ✅ |
| SELECT (`*` or named columns) | ✅ |
| WHERE (`=`, `>`, `<`, `>=`, `<=`) | ✅ |
| ORDER BY (ASC / DESC, type-aware) | ✅ |
| UPDATE / DELETE (with WHERE) | ✅ |
| Command history (`HISTORY`) | ✅ |
| Persistence across restarts (verified, incl. `kill -9` test) | ✅ |
| Compound conditions (`AND` / `OR`) | 🔜 future |
| `LIMIT` clause | 🔜 future |
| Hash indexes | 🔜 v1.1 |
| Transactions | 🔜 v1.2 |
| Concurrency | 🔜 v1.3 |

---

## Quick start

Requires Java 17+ and Maven.

```bash
git clone https://github.com/<your-username>/minidb.git
cd minidb
mvn clean package
java -jar target/minidb.jar
```

Or run the bundled demo non-interactively:

```bash
java -jar target/minidb.jar < demo/demo.sql
```

---

## Example session

```
MiniDB> CREATE DATABASE company;
Database created: company
MiniDB> USE company;
Using database: company
MiniDB> CREATE TABLE employees (id INT PRIMARY KEY, name STRING, salary DOUBLE);
Table created: employees
MiniDB> INSERT INTO employees VALUES(1, 'John', 50000);
1 row inserted.
MiniDB> INSERT INTO employees VALUES(2, 'Priya', 62000);
1 row inserted.
MiniDB> INSERT INTO employees VALUES(3, 'Amit', 45000);
1 row inserted.
MiniDB> SELECT name, salary FROM employees WHERE salary>48000 ORDER BY salary DESC;
name | salary
Priya | 62000
John | 50000
MiniDB> UPDATE employees SET salary=70000 WHERE id=3;
1 row(s) updated.
MiniDB> DELETE FROM employees WHERE id=1;
1 row(s) deleted.
MiniDB> SELECT * FROM employees;
id | name | salary
2 | Priya | 62000
3 | Amit | 70000
MiniDB> DROP DATABASE company;
Database dropped: company
MiniDB> EXIT
Goodbye.
```

The full version of this session is in [`demo/demo.sql`](demo/demo.sql) — run it directly with `java -jar target/minidb.jar < demo/demo.sql`.

---

## Architecture

```text
CLI
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

| Package | Responsibility |
|---|---|
| `cli` | REPL loop, input handling, history, error display |
| `tokenizer` | Converts SQL text into typed tokens |
| `parser` | Hand-written recursive-descent parser producing Command objects |
| `command` | One class per SQL statement type (Command Pattern) |
| `executor` | Runs commands: filter → sort → project pipeline |
| `storage` | Reads/writes table files, schema, primary key validation |
| `model` | Row, TableSchema, ColumnDefinition, WhereClause, OrderByClause |
| `exception` | SyntaxException, StorageException |

---

## Storage format

Each table is two plain-text files under `data/<database>/`:

```text
employees.meta          employees.data
id|INT|PRIMARY          1|John|50000
name|STRING|            2|Priya|62000
salary|DOUBLE|           3|Amit|45000
```

Plain text was chosen deliberately — you can `cat` the files while debugging instead of needing a binary-format reader.

---

## Persistence

MiniDB writes every INSERT, UPDATE, and DELETE directly to disk (`data/<database>/<table>.data`) using `Files.writeString`, with no in-memory buffering between a command and its file write.

Verified manually:
- Inserted rows, exited the CLI completely, restarted, and confirmed all data and schema (via `SELECT`, `SHOW TABLES`, `DESCRIBE`) were intact.
- Raw `.meta` and `.data` files were inspected directly with `cat` to confirm on-disk state independent of MiniDB's own output.
- Force-killed the process (`kill -9`) immediately after a single INSERT to simulate a crash mid-session, restarted, and confirmed the previously committed row was intact and uncorrupted, with no partial/corrupted writes.

---

## Known limitations

- **`|` in string values breaks the storage format** (no escaping yet).
- **Single quotes inside strings** (e.g. `O'Brien`) are not supported by the tokenizer.
- **UPDATE/DELETE rewrite the whole table file** — correct, but O(n) per statement, and not yet safe for concurrent access.
- **Only a single WHERE condition is supported** — no `AND` / `OR` yet.
- **Only a single ORDER BY column is supported** — no multi-column sort.
- **No `LIMIT` clause yet.**
- **No type validation on INSERT** beyond primary key uniqueness (e.g. text can currently be inserted into an INT column).
- **`SELECT` with WHERE or ORDER BY performs a full table scan** — no indexes yet (planned for v1.1).
- **Data directory is relative to the working directory** you launch from, not the jar's location.
- **Single-threaded, no transactions** — planned for v1.2 and v1.3.

---

## Supported WHERE operators

| Operator | Meaning | Comparison |
|---|---|---|
| `=` | Equal | String |
| `>` | Greater than | Numeric |
| `<` | Less than | Numeric |
| `>=` | Greater than or equal | Numeric |
| `<=` | Less than or equal | Numeric |

## Supported ORDER BY

| Syntax | Behavior |
|---|---|
| `ORDER BY column` | Ascending (default), type-aware comparison |
| `ORDER BY column ASC` | Ascending, explicit |
| `ORDER BY column DESC` | Descending |

`ORDER BY` combines freely with `WHERE` and column projection — evaluation order is always **filter → sort → project**.

## CLI commands

| Command | Behavior |
|---|---|
| `HELP` | Lists all available commands |
| `HISTORY` | Lists every command run so far this session |
| `EXIT` | Exits the CLI |

## Error message format

| Prefix | Thrown by |
|---|---|
| `[SYNTAX ERROR]` | `SyntaxException` (parser) |
| `[STORAGE ERROR]` | `StorageException` (storage engine) |
| `[EXECUTION ERROR]` | `IllegalStateException` (executor) |
| `[ERROR]` | Any other uncaught exception |

---