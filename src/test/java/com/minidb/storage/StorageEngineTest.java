package com.minidb.storage;

import com.minidb.model.ColumnDefinition;
import com.minidb.model.Row;
import com.minidb.model.TableSchema;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StorageEngineTest {

    private static final String TEST_DB = "test_db_storage_engine";

    @AfterEach
    public void cleanup() throws Exception {
        Path dbPath = Path.of("data", TEST_DB);
        if (Files.exists(dbPath)) {
            Files.walk(dbPath)
                    .sorted((a, b) -> b.compareTo(a)) // delete files before their parent dir
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); } catch (Exception ignored) {}
                    });
        }
    }

    @Test
    public void createsAndUsesDatabase() {
        StorageEngine engine = new StorageEngine();
        engine.createDatabase(TEST_DB);
        engine.useDatabase(TEST_DB);
        assertEquals(TEST_DB, engine.getCurrentDatabase());
    }

    @Test
    public void createsTableAndInsertsRow() {
        StorageEngine engine = new StorageEngine();
        engine.createDatabase(TEST_DB);
        engine.useDatabase(TEST_DB);

        TableSchema schema = new TableSchema("employees", List.of(
                new ColumnDefinition("id", "INT", true),
                new ColumnDefinition("name", "STRING", false)
        ));
        engine.createTable(schema);
        engine.insertRow("employees", new Row(List.of("1", "John")));

        List<Row> rows = engine.readAllRows("employees");
        assertEquals(1, rows.size());
        assertEquals("John", rows.get(0).getValues().get(1));
    }

    @Test
    public void throwsWhenTableDoesNotExist() {
        StorageEngine engine = new StorageEngine();
        engine.createDatabase(TEST_DB);
        engine.useDatabase(TEST_DB);

        assertThrows(RuntimeException.class, () -> engine.readAllRows("ghost_table"));
    }
}