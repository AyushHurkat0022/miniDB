package com.minidb.command;

public class CreateDatabaseCommand implements Command{
    private final String databaseName;

    public CreateDatabaseCommand(String databaseName){
        this.databaseName = databaseName;
    }

    public String getDatabaseName(){
        return databaseName;
    }
}
