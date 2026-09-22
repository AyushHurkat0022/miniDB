package com.minidb.command;

import com.minidb.model.WhereClause;

import java.util.List;

public class SelectCommand implements Command{
    private final String tableName;
    private final List<String> columns;
    private final WhereClause whereClause;

    public SelectCommand(String tableName, List<String> columns, WhereClause whereClause){
        this.tableName = tableName;
        this.columns = columns;
        this.whereClause = whereClause;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumns() {
        return columns;
    }

    public WhereClause getWhereClause(){
        return whereClause;
    }
}
