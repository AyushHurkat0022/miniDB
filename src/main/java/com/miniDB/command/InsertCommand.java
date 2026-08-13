package com.minidb.command;

import java.util.List;

public class InsertCommand implements Command{
    private final String tableName;
    private final List<String> values;

    public InsertCommand(String tableName, List<String> values){
        this.tableName = tableName;
        this.values = values;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getValues() {
        return values;
    }
}
