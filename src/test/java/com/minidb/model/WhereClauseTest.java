package com.minidb.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WhereClauseTest {

    @Test
    public void equalityMatchesExactString() {
        WhereClause clause = new WhereClause("id", "=", "1");
        assertTrue(clause.matches("1"));
        assertFalse(clause.matches("2"));
    }

    @Test
    public void greaterThanComparesNumerically() {
        WhereClause clause = new WhereClause("salary", ">", "50000");
        assertTrue(clause.matches("60000"));
        assertFalse(clause.matches("40000"));
        assertFalse(clause.matches("50000")); // strictly greater
    }

    @Test
    public void greaterThanOrEqualIncludesBoundary() {
        WhereClause clause = new WhereClause("salary", ">=", "50000");
        assertTrue(clause.matches("50000"));
    }

    @Test
    public void lessThanComparesNumerically() {
        WhereClause clause = new WhereClause("salary", "<", "50000");
        assertTrue(clause.matches("40000"));
        assertFalse(clause.matches("60000"));
    }

    @Test
    public void throwsOnNonNumericComparison() {
        WhereClause clause = new WhereClause("name", ">", "50");
        assertThrows(IllegalStateException.class, () -> clause.matches("John"));
    }
}