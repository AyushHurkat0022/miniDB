package com.minidb.command;

import com.minidb.model.WhereClause;

public class DeleteCommand implements Command{
    private final String tableName;
    private final WhereClause whereClause;

    public DeleteCommand(String tableName, WhereClause whereClause){
        this.tableName = tableName;
        this.whereClause = whereClause;
    }

    public String getTableName(){
        return tableName;
    }

    public WhereClause getWhereClause(){
        return whereClause;
    }
}
