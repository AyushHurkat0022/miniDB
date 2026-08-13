package com.minidb.command;

public class DropTableCommand implements Command{
    private final String tableName;

    public DropTableCommand(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}
