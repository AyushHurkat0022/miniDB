package com.minidb.model;

public class WhereClause {
    private final String column;
    private final String operator;
    private final String value;

    public WhereClause(String column, String operator, String value){
        this.column = column;
        this.value = value;
        this.operator = operator;
    }

    public String getColumn(){
        return column;
    }
    public String getValue(){
        return value;
    }
    public String getOperator(){ return operator;}

    public boolean matches(String rowValue){
        switch (operator){
            case "=":
                return rowValue.equals(value);
            case ">":
                return compareNumeric(rowValue) > 0;
            case "<":
                return compareNumeric(rowValue) < 0;
            case ">=":
                return compareNumeric(rowValue) >= 0;
            case "<=":
                return compareNumeric(rowValue) <= 0;
            default:
                throw new IllegalStateException("Unsupported operator: " + operator);
        }
    }

    private int compareNumeric(String rowValue){
        try {
            double rowNum = Double.parseDouble(rowValue);
            double targetNum = Double.parseDouble(value);
            return Double.compare(rowNum, targetNum);
        } catch (NumberFormatException e){
            throw new IllegalStateException(
                    "Cannot use operator '" + operator + "' on non-numeric value: " + rowValue
            );
        }
    }
}
