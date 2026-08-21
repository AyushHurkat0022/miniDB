package com.minidb.model;

import java.util.List;

public class Row {
    private final List<String> values;

    public Row(List<String> values){
        this.values = values;
    }

    public List<String> getValues() {
        return values;
    }

    public String toFileLine(){
        return String.join("|", values);
    }

    public static Row fromFileLine(String line){
        return new Row(List.of(line.split("\\|",-1)));
    }
}
