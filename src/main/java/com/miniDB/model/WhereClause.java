package com.minidb.model;

public class WhereClause {
    private final String column;
    private final String value;

    public WhereClause(String column, String value){
        this.column = column;
        this.value = value;
    }

    public String getColumn(){
        return column;
    }
    public String getValue(){
        return value;
    }

    public boolean matches(String rowValue){
        return rowValue.equals(value);
    }
}
