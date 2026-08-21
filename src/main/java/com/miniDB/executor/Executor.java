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
            storageEngine.insertRow(cmd.getTableName(), new Row(cmd.getValues()));
            return "1 Row Inserted";
        }

        if(command instanceof SelectCommand cmd){
            return executeSelect(cmd);
        }

        if(command instanceof DeleteCommand cmd){
            int count = storageEngine.readAllRows(cmd.getTableName()).size();
            storageEngine.overwriteAllRows(cmd.getTableName(), List.of());
            return count + " row(s) deleted.";
        }

        throw new IllegalStateException("Unhandled command type: "+command.getClass());
    }

    private String executeSelect(SelectCommand cmd) {
        TableSchema schema = storageEngine.getTableSchema(cmd.getTableName());
        List<Row> rows = storageEngine.readAllRows(cmd.getTableName());

        boolean allColumns = cmd.getColumns().size() == 1 && cmd.getColumns().get(0).equals("*");
        List<String> columnNames = allColumns
                ? schema.getColumns().stream().map(c -> c.getName()).collect(Collectors.toList())
                : cmd.getColumns();

        StringBuilder sb = new StringBuilder();
        sb.append(String.join(" | ", columnNames)).append("\n");

        for (Row row : rows) {
            List<String> rowValues = columnNames.stream()
                    .map(colName -> row.getValues().get(schema.getColumnIndex(colName)))
                    .collect(Collectors.toList());
            sb.append(String.join(" | ", rowValues)).append("\n");
        }

        return sb.toString().stripTrailing();
    }
}
