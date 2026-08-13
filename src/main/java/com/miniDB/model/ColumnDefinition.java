package com.minidb.model;

public class ColumnDefinition {
    private final String name;
    private final String type;
    private final boolean primaryKey;

    public ColumnDefinition(String name, String type, boolean primaryKey){
        this.name = name;
        this.type = type;
        this.primaryKey = primaryKey;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    @Override
    public String toString(){
        return name + " " + type + (primaryKey ? "PRIMARY KEY" : "");
    }
}
