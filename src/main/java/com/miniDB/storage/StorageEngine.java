package com.minidb.storage;

import com.minidb.exception.StorageException;
import com.minidb.model.Row;
import com.minidb.model.TableSchema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StorageEngine {
    private static final String DATA_ROOT = "data";
    private String currentDatabase;

    public void createDatabase(String name){
        Path dbPath = Path.of(DATA_ROOT, name);
        if(Files.exists(dbPath)){
            throw new StorageException("Database already exists: " + name);
        }
        try{
            Files.createDirectories(dbPath);
        } catch (IOException e) {
            throw new StorageException("Failed to create database: " + name, e);
        }
    }

    public void useDatabase(String name){
        Path dbPath = Path.of(DATA_ROOT, name);
        if(!Files.exists(dbPath)){
            throw new StorageException("Database does not exist: "+name);
        }
        this.currentDatabase = name;
    }

    public void dropDatabase(String name){
        Path dbPath = Path.of(DATA_ROOT, name);
        if(!Files.exists(dbPath)){
            throw new StorageException("Database does not exist: "+name);
        }
        deleteDirectoryRecursively(dbPath);
        if(name.equals(currentDatabase)){
            currentDatabase = null;
        }
    }

    public List<String> showDatabases(){
        Path root = Path.of(DATA_ROOT);
        try{
            if(!Files.exists(root)) return List.of();
            return Files.list(root)
                    .filter(Files::isDirectory)
                    .map(p->p.getFileName().toString())
                    .collect(Collectors.toList());
        }
        catch (IOException e){
            throw new StorageException("Failed to list databases", e);
        }
    }

    public String getCurrentDatabase() {
        return currentDatabase;
    }

    private void requireDatabaseSelected(){
        if(currentDatabase == null){
            throw new StorageException("No database selected. Use 'USE <database>;' first.");
        }
    }

    private void deleteDirectoryRecursively(Path path) {
        try {
            if (Files.isDirectory(path)) {
                try (var entries = Files.list(path)) {
                    for (Path entry : entries.toList()) {
                        deleteDirectoryRecursively(entry);
                    }
                }
            }
            Files.delete(path);
        } catch (IOException e) {
            throw new StorageException("Failed to delete: " + path, e);
        }
    }

    public void createTable(TableSchema schema){
        requireDatabaseSelected();
        Path metaPath = tableMetaPath(schema.getTableName());
        if(Files.exists(metaPath)){
            throw new StorageException("Table already exists with name " + schema.getTableName());
        }
        try{
            Files.writeString(metaPath, schema.toFileContent());
            Files.writeString(tableDataPath(schema.getTableName()), "");
        } catch (IOException e) {
            throw new StorageException("Failed to create table " + schema.getTableName());
        }
    }

    public void dropTable(String tableName){
        requireDatabaseSelected();
        try {
            Files.deleteIfExists(tableMetaPath(tableName));
            Files.deleteIfExists(tableDataPath(tableName));
        }
        catch (IOException e){
            throw new StorageException("Failed to drop table " + tableName);
        }
    }

    public List<String> showTable(){
        requireDatabaseSelected();
        Path dbPath = Path.of(DATA_ROOT, currentDatabase);
        try{
            return Files.list(dbPath)
                    .filter(p->p.toString().endsWith(".meta"))
                    .map(p->p.getFileName().toString().replace(".meta", ""))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new StorageException("Failed to list tables ", e);
        }
    }

    public TableSchema getTableSchema(String tableName){
        requireDatabaseSelected();
        Path metaPath = tableMetaPath(tableName);
        if(!Files.exists(metaPath)){
            throw new StorageException("Table doesn't exist");
        }
        try{
            String content = Files.readString(metaPath);
            return TableSchema.fromFileContent(tableName, content);
        } catch (IOException e) {
            throw new StorageException("Failed to read schema: " + tableName, e);
        }
    }

    public void insertRow(String tableName, Row row){
        requireDatabaseSelected();
        Path dataPath = tableDataPath(tableName);
        if(!Files.exists(dataPath)){
            throw new StorageException("Table does not exist: "+tableName);
        }
        try {
            Files.writeString(dataPath, row.toFileLine() + System.lineSeparator(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new StorageException("Failed to insert row into " + tableName,e);
        }
    }

    public List<Row> readAllRows(String tableName){
        requireDatabaseSelected();
        Path dataPath = tableDataPath(tableName);
        if(!Files.exists(dataPath)){
            throw new StorageException("Table does not exist: " + tableName);
        }
        try{
            List<String> lines = Files.readAllLines(dataPath);
            List<Row> rows = new ArrayList<>();

            for(String line: lines){
                if(!line.isBlank()) rows.add(Row.fromFileLine(line));
            }
            return rows;
        } catch (IOException e) {
            throw new StorageException("Failed to read rows from: " + tableName, e);
        }
    }

    public void overwriteAllRows(String tableName, List<Row> rows){
        requireDatabaseSelected();
        Path dataPath = tableDataPath(tableName);
        if(!Files.exists(dataPath)){
            throw new StorageException("Table does not exist: " + tableName);
        }
        try{
            String content = rows.stream()
                    .map(Row::toFileLine)
                    .collect(Collectors.joining(System.lineSeparator()));
            Files.writeString(dataPath, content.isEmpty()?"":content+System.lineSeparator());
        }
        catch (IOException e){
            throw new StorageException("Failed to overwrite rows in: "+ tableName, e);
        }
    }

    private Path tableMetaPath(String tableName) {
        return Path.of(DATA_ROOT, currentDatabase, tableName + ".meta");
    }

    private Path tableDataPath(String tableName) {
        return Path.of(DATA_ROOT, currentDatabase, tableName + ".data");
    }

    public void validatePrimaryKeyUnique(TableSchema schema, List<String> newValues){
        int pkIndex = -1;
        for(int i=0; i<schema.getColumns().size(); i++){
            if(schema.getColumns().get(i).isPrimaryKey()){
                pkIndex = i;
                break;
            }
        }

        if(pkIndex==-1){
            return;
        }

        String newPkValue = newValues.get(pkIndex);
        List<Row> existingRows = readAllRows(schema.getTableName());
        for(Row row: existingRows){
            if(row.getValues().get(pkIndex).equals(newPkValue)){
                throw new StorageException(
                        "Duplicate primary key value '" + newPkValue + "' for table " + schema.getTableName()
                );
            }
        }
    }
}
