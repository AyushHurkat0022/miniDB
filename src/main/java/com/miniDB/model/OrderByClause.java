package com.minidb.model;

public class OrderByClause {
    private final String column;
    private final boolean descending;

    public OrderByClause(String column, boolean descending) {
        this.column = column;
        this.descending = descending;
    }

    public String getColumn() {
        return column;
    }

    public boolean isDescending() {
        return descending;
    }
}
