package com.minidb.command;

public class DropDatabaseCommand implements Command{
    private final String databaseName;

    public DropDatabaseCommand(String databaseName){
        this.databaseName = databaseName;
    }

    public String getDatabaseName(){
        return databaseName;
    }
}
