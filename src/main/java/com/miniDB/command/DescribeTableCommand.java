package com.minidb.command;

public class DescribeTableCommand implements Command{
    private final String tableName;

    public DescribeTableCommand(String tableName){
        this.tableName = tableName;
    }

    public String getTableName(){
        return tableName;
    }
}
