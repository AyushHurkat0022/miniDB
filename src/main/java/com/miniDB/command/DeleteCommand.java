package com.minidb.command;

public class DeleteCommand implements Command{
    private final String tableName;

    public DeleteCommand(String tableName){
        this.tableName = tableName;
    }

    public String getTableName(){
        return tableName;
    }
}
