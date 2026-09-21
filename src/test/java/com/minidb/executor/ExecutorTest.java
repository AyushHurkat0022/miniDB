package com.minidb.executor;

import com.minidb.command.*;
import com.minidb.model.ColumnDefinition;
import com.minidb.model.WhereClause;
import com.minidb.storage.StorageEngine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutorTest {

    private static final String TEST_DB = "test_db_executor";
    private Executor executor;

    @BeforeEach
    public void setup() {
        StorageEngine storageEngine = new StorageEngine();
        executor = new Executor(storageEngine);
        executor.execute(new CreateDatabaseCommand(TEST_DB));
        executor.execute(new UseDatabaseCommand(TEST_DB));
        executor.execute(new CreateTableCommand("employees", List.of(
                new ColumnDefinition("id", "INT", true),
                new ColumnDefinition("name", "STRING", false),
                new ColumnDefinition("salary", "DOUBLE", false)
        )));
    }

    @AfterEach
    public void cleanup() throws Exception {
        Path dbPath = Path.of("data", TEST_DB);
        if (Files.exists(dbPath)) {
            Files.walk(dbPath)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(p -> { try { Files.deleteIfExists(p); } catch (Exception ignored) {} });
        }
    }

    @Test
    public void rejectsDuplicatePrimaryKey() {
        executor.execute(new InsertCommand("employees", List.of("1", "John", "50000")));
        assertThrows(RuntimeException.class, () ->
                executor.execute(new InsertCommand("employees", List.of("1", "Someone Else", "1000"))));
    }

    @Test
    public void updatesMatchingRowOnly() {
        executor.execute(new InsertCommand("employees", List.of("1", "John", "50000")));
        executor.execute(new InsertCommand("employees", List.of("2", "Priya", "62000")));

        executor.execute(new UpdateCommand("employees", List.of("salary"), List.of("65000"),
                new WhereClause("id", "1")));

        String result = executor.execute(new SelectCommand("employees", List.of("*")));
        assertTrue(result.contains("65000"));
        assertTrue(result.contains("62000")); // Priya untouched
    }

    @Test
    public void deletesMatchingRowOnly() {
        executor.execute(new InsertCommand("employees", List.of("1", "John", "50000")));
        executor.execute(new InsertCommand("employees", List.of("2", "Priya", "62000")));

        executor.execute(new DeleteCommand("employees", new WhereClause("id", "2")));

        String result = executor.execute(new SelectCommand("employees", List.of("*")));
        assertFalse(result.contains("Priya"));
        assertTrue(result.contains("John"));
    }

    @Test
    public void deleteWithoutWhereRemovesAllRows() {
        executor.execute(new InsertCommand("employees", List.of("1", "John", "50000")));
        executor.execute(new InsertCommand("employees", List.of("2", "Priya", "62000")));

        executor.execute(new DeleteCommand("employees", null));

        String result = executor.execute(new SelectCommand("employees", List.of("*")));
        // header line only, no data rows
        assertEquals(1, result.split("\n").length);
    }
}