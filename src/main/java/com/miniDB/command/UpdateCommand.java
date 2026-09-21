package com.minidb.command;

import com.minidb.model.WhereClause;

import java.util.List;

public class UpdateCommand implements Command{
    private final String tableName;
    private final List<String> setColumns;
    private final List<String> setValues;
    private final WhereClause whereClause;

    public UpdateCommand(String tableName, List<String> setColumns, List<String> setValues, WhereClause whereClause) {
        this.tableName = tableName;
        this.setColumns = setColumns;
        this.setValues = setValues;
        this.whereClause = whereClause;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getSetColumns() {
        return setColumns;
    }

    public List<String> getSetValues() {
        return setValues;
    }

    public WhereClause getWhereClause() {
        return whereClause;
    }
}
