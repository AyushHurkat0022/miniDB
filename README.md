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

🚧 Sprint 2 — Parser (Next)

Current Version: **v0.2**

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