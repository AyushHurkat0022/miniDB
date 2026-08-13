package com.minidb.command;

public class UseDatabaseCommand implements Command{
    private final String databaseName;

    public UseDatabaseCommand(String databaseName){
        this.databaseName = databaseName;
    }

    public String getDatabaseName(){
        return databaseName;
    }
}
