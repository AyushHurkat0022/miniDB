package com.minidb.command;

import com.minidb.model.OrderByClause;
import com.minidb.model.WhereClause;

import java.util.List;

public class SelectCommand implements Command{
    private final String tableName;
    private final List<String> columns;
    private final WhereClause whereClause;
    private final OrderByClause orderByClause;

    public SelectCommand(String tableName, List<String> columns, WhereClause whereClause, OrderByClause orderByClause){
        this.tableName = tableName;
        this.columns = columns;
        this.whereClause = whereClause;
        this.orderByClause = orderByClause;
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

    public OrderByClause getOrderByClause(){
        return orderByClause;
    }
}
