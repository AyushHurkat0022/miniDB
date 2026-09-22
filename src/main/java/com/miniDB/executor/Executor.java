package com.minidb.executor;

import com.minidb.command.*;
import com.minidb.model.Row;
import com.minidb.model.TableSchema;
import com.minidb.storage.StorageEngine;

import java.util.List;
import java.util.stream.Collectors;

public class Executor {
    private final StorageEngine storageEngine;

    public Executor(StorageEngine storageEngine){
        this.storageEngine = storageEngine;
    }

    public String execute(Command command){
        if(command instanceof CreateDatabaseCommand cmd){
            storageEngine.createDatabase(cmd.getDatabaseName());
            return "Database created: " + cmd.getDatabaseName();
        }
        if(command instanceof UseDatabaseCommand cmd){
            storageEngine.useDatabase(cmd.getDatabaseName());
            return "Using Database: " + cmd.getDatabaseName();
        }
        if(command instanceof DropDatabaseCommand cmd){
            storageEngine.dropDatabase(cmd.getDatabaseName());
            return "Database dropped: " + cmd.getDatabaseName();
        }
        if(command instanceof ShowDatabaseCommand cmd){
            return String.join("\n", storageEngine.showDatabases());
        }
        if(command instanceof CreateTableCommand cmd){
            TableSchema schema = new TableSchema(cmd.getTableName(), cmd.getColumns());
            storageEngine.createTable(schema);
            return "Table created: " + cmd.getTableName();
        }

        if(command instanceof DropTableCommand cmd){
            storageEngine.dropTable(cmd.getTableName());
            return "Table dropped: " + cmd.getTableName();
        }
        if(command instanceof ShowTableCommand cmd){
            return String.join("\n", storageEngine.showTable());
        }

        if(command instanceof DescribeTableCommand cmd){
            TableSchema schema = storageEngine.getTableSchema(cmd.getTableName());
            return schema.getColumns().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n"));
        }
        if(command instanceof InsertCommand cmd){
            TableSchema schema = storageEngine.getTableSchema(cmd.getTableName());
            storageEngine.validatePrimaryKeyUnique(schema, cmd.getValues());
            storageEngine.insertRow(cmd.getTableName(), new Row(cmd.getValues()));
            return "1 Row Inserted";
        }

        if(command instanceof SelectCommand cmd){
            return executeSelect(cmd);
        }

        if (command instanceof DeleteCommand cmd) {
            return executeDelete(cmd);
        }

        if (command instanceof UpdateCommand cmd) {
            return executeUpdate(cmd);
        }

        throw new IllegalStateException("Unhandled command type: "+command.getClass());
    }

    private String executeSelect(SelectCommand cmd) {
        TableSchema schema = storageEngine.getTableSchema(cmd.getTableName());
        List<Row> allRows = storageEngine.readAllRows(cmd.getTableName());

        List<Row> filteredRows = allRows.stream()
                .filter(row -> rowMatchesWhere(row,schema,cmd.getWhereClause()))
                .collect(Collectors.toList());

        boolean allColumns = cmd.getColumns().size() == 1 && cmd.getColumns().get(0).equals("*");
        List<String> columnNames = allColumns
                ? schema.getColumns().stream().map(c -> c.getName()).collect(Collectors.toList())
                : cmd.getColumns();

        StringBuilder sb = new StringBuilder();
        sb.append(String.join(" | ", columnNames)).append("\n");

        for (Row row : allRows) {
            List<String> rowValues = columnNames.stream()
                    .map(colName -> row.getValues().get(schema.getColumnIndex(colName)))
                    .collect(Collectors.toList());
            sb.append(String.join(" | ", rowValues)).append("\n");
        }

        return sb.toString().stripTrailing();
    }

    private String executeDelete(DeleteCommand cmd) {
        TableSchema schema = storageEngine.getTableSchema(cmd.getTableName());
        List<Row> allRows = storageEngine.readAllRows(cmd.getTableName());

        List<Row> remaining = new java.util.ArrayList<>();
        int deletedCount = 0;

        for (Row row : allRows) {
            if (rowMatchesWhere(row, schema, cmd.getWhereClause())) {
                deletedCount++; // matched -> not kept
            } else {
                remaining.add(row);
            }
        }

        storageEngine.overwriteAllRows(cmd.getTableName(), remaining);
        return deletedCount + " row(s) deleted.";
    }

    private String executeUpdate(UpdateCommand cmd) {
        TableSchema schema = storageEngine.getTableSchema(cmd.getTableName());
        List<Row> allRows = storageEngine.readAllRows(cmd.getTableName());

        List<Row> updated = new java.util.ArrayList<>();
        int updatedCount = 0;

        for (Row row : allRows) {
            if (rowMatchesWhere(row, schema, cmd.getWhereClause())) {
                List<String> newValues = new java.util.ArrayList<>(row.getValues());
                for (int i = 0; i < cmd.getSetColumns().size(); i++) {
                    int colIndex = schema.getColumnIndex(cmd.getSetColumns().get(i));
                    if (colIndex == -1) {
                        throw new IllegalStateException("Unknown column: " + cmd.getSetColumns().get(i));
                    }
                    newValues.set(colIndex, cmd.getSetValues().get(i));
                }
                updated.add(new Row(newValues));
                updatedCount++;
            } else {
                updated.add(row);
            }
        }

        storageEngine.overwriteAllRows(cmd.getTableName(), updated);
        return updatedCount + " row(s) updated.";
    }

    private boolean rowMatchesWhere(Row row, TableSchema schema, com.minidb.model.WhereClause whereClause) {
        if (whereClause == null) {
            return true; // no WHERE = matches every row
        }
        int colIndex = schema.getColumnIndex(whereClause.getColumn());
        if (colIndex == -1) {
            throw new IllegalStateException("Unknown column in WHERE clause: " + whereClause.getColumn());
        }
        return whereClause.matches(row.getValues().get(colIndex));
    }
}
