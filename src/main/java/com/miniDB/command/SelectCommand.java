package com.minidb.command;

import java.util.List;

public class SelectCommand implements Command{
    private final String tableName;
    private final List<String> columns;

    public SelectCommand(String tableName, List<String> columns){
        this.tableName = tableName;
        this.columns = columns;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumns() {
        return columns;
    }
}
