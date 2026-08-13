package com.minidb.command;

import com.minidb.model.ColumnDefinition;

import java.util.List;

public class CreateTableCommand implements Command{
    private final String tableName;
    private final List<ColumnDefinition> columns;

    public CreateTableCommand(String tableName, List<ColumnDefinition> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    public String getTableName(){
        return tableName;
    }
    public List<ColumnDefinition> getColumns(){
        return columns;
    }
}
