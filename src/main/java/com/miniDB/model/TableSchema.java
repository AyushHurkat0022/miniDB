package com.minidb.model;

import java.util.List;

public class TableSchema {
    private final String tableName;
    private final List<ColumnDefinition> columns;

    public TableSchema(String tableName, List<ColumnDefinition> columns){
        this.tableName = tableName;
        this.columns = columns;
    }

    public String getTableName() {
        return tableName;
    }
    public List<ColumnDefinition> getColumns() {
        return columns;
    }

    public int getColumnIndex(String columnName){
        for(int i=0; i<columns.size(); i++){
            if(columns.get(i).getName().equalsIgnoreCase(columnName)){
                return i;
            }
        }
        return -1;
    }

    public String toFileContent() {
        StringBuilder sb = new StringBuilder();
        for (ColumnDefinition col : columns) {
            sb.append(col.getName()).append("|").append(col.getType());
            if (col.isPrimaryKey()) sb.append("|PRIMARY");
            sb.append("\n");
        }
        return sb.toString();
    }

    public static TableSchema fromFileContent(String tableName, String content) {
        List<ColumnDefinition> columns = new java.util.ArrayList<>();
        for (String line : content.split("\n")) {
            if (line.isBlank()) continue;
            String[] parts = line.split("\\|");
            String name = parts[0];
            String type = parts[1];
            boolean pk = parts.length > 2 && parts[2].equals("PRIMARY");
            columns.add(new ColumnDefinition(name, type, pk));
        }
        return new TableSchema(tableName, columns);
    }

}
