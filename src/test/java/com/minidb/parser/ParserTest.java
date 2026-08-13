package com.minidb.parser;

import com.minidb.tokenizer.Tokenizer;
import com.minidb.command.*;
import com.minidb.exception.SyntaxException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    private Command parse(String sql) {
        Tokenizer tokenizer = new Tokenizer(sql);
        Parser parser = new Parser(tokenizer.tokenize());
        return parser.parse();
    }

    @Test
    public void parsesCreateDatabase() {
        Command cmd = parse("CREATE DATABASE company;");
        assertTrue(cmd instanceof CreateDatabaseCommand);
        assertEquals("company", ((CreateDatabaseCommand) cmd).getDatabaseName());
    }

    @Test
    public void parsesUseDatabase() {
        Command cmd = parse("USE company;");
        assertTrue(cmd instanceof UseDatabaseCommand);
        assertEquals("company", ((UseDatabaseCommand) cmd).getDatabaseName());
    }

    @Test
    public void parsesCreateTable() {
        Command cmd = parse("CREATE TABLE employees (id INT PRIMARY KEY, name STRING, salary DOUBLE);");
        assertTrue(cmd instanceof CreateTableCommand);
        CreateTableCommand createCmd = (CreateTableCommand) cmd;
        assertEquals("employees", createCmd.getTableName());
        assertEquals(3, createCmd.getColumns().size());
        assertTrue(createCmd.getColumns().get(0).isPrimaryKey());
        assertEquals("salary", createCmd.getColumns().get(2).getName());
        assertEquals("DOUBLE", createCmd.getColumns().get(2).getType());
    }

    @Test
    public void parsesInsert() {
        Command cmd = parse("INSERT INTO employees VALUES(1, 'John', 50000);");
        assertTrue(cmd instanceof InsertCommand);
        InsertCommand insertCmd = (InsertCommand) cmd;
        assertEquals("employees", insertCmd.getTableName());
        assertEquals(3, insertCmd.getValues().size());
        assertEquals("John", insertCmd.getValues().get(1));
    }

    @Test
    public void parsesSelectStar() {
        Command cmd = parse("SELECT * FROM employees;");
        assertTrue(cmd instanceof SelectCommand);
        SelectCommand selectCmd = (SelectCommand) cmd;
        assertEquals("employees", selectCmd.getTableName());
        assertEquals(1, selectCmd.getColumns().size());
        assertEquals("*", selectCmd.getColumns().get(0));
    }

    @Test
    public void parsesSelectSpecificColumns() {
        Command cmd = parse("SELECT name, salary FROM employees;");
        SelectCommand selectCmd = (SelectCommand) cmd;
        assertEquals(2, selectCmd.getColumns().size());
        assertEquals("name", selectCmd.getColumns().get(0));
        assertEquals("salary", selectCmd.getColumns().get(1));
    }

    @Test
    public void parsesDelete() {
        Command cmd = parse("DELETE FROM employees;");
        assertTrue(cmd instanceof DeleteCommand);
        assertEquals("employees", ((DeleteCommand) cmd).getTableName());
    }

    @Test
    public void throwsSyntaxExceptionOnMissingSemicolon() {
        assertThrows(SyntaxException.class, () -> parse("SELECT * FROM employees"));
    }

    @Test
    public void throwsSyntaxExceptionOnGarbage() {
        assertThrows(SyntaxException.class, () -> parse("BANANA employees;"));
    }
}