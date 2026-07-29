package com.minidb.tokenizer;

import java.util.Set;

public class Keywords {
    public static final Set<String> Keywords = Set.of(
            "CREATE", "DATABASE", "USE", "DROP", "SHOW", "TABLE", "TABLES", "DATABASES",
            "DESCRIBE", "INSERT", "INTO", "VALUES", "SELECT", "FROM", "WHERE",
            "UPDATE", "SET", "DELETE", "ORDER", "BY", "ASC", "DESC",
            "INDEX", "ON", "PRIMARY", "KEY", "BEGIN", "COMMIT", "ROLLBACK",
            "INT", "STRING", "DOUBLE", "HELP", "EXIT", "AND", "OR"
    );

    public static boolean isKeyword(String value){
        return Keywords.contains(value.toUpperCase());
    }
}
