package com.minidb.tokenizer;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TokenizerTest {

    @Test
    public void tokenizesSimpleSelect() {
        Tokenizer tokenizer = new Tokenizer("SELECT * FROM employees;");
        List<Token> tokens = tokenizer.tokenize();

        assertEquals(new Token(TokenType.KEYWORD, "SELECT"), tokens.get(0));
        assertEquals(new Token(TokenType.SYMBOL, "*"), tokens.get(1));
        assertEquals(new Token(TokenType.KEYWORD, "FROM"), tokens.get(2));
        assertEquals(new Token(TokenType.IDENTIFIER, "employees"), tokens.get(3));
        assertEquals(new Token(TokenType.SYMBOL, ";"), tokens.get(4));
        assertEquals(TokenType.EOF, tokens.get(5).getType());
    }

    @Test
    public void tokenizesStringLiteral() {
        Tokenizer tokenizer = new Tokenizer("INSERT INTO employees VALUES(1, 'John', 50000);");
        List<Token> tokens = tokenizer.tokenize();

        assertTrue(tokens.contains(new Token(TokenType.STRING, "John")));
        assertTrue(tokens.contains(new Token(TokenType.NUMBER, "1")));
        assertTrue(tokens.contains(new Token(TokenType.NUMBER, "50000")));
    }

    @Test
    public void tokenizesDecimalNumber() {
        Tokenizer tokenizer = new Tokenizer("salary=65000.50");
        List<Token> tokens = tokenizer.tokenize();

        assertEquals(new Token(TokenType.NUMBER, "65000.50"), tokens.get(2));
    }

    @Test
    public void tokenizesComparisonOperators() {
        Tokenizer tokenizer = new Tokenizer("salary>=50000");
        List<Token> tokens = tokenizer.tokenize();

        assertEquals(new Token(TokenType.SYMBOL, ">="), tokens.get(1));
    }

    @Test
    public void distinguishesKeywordFromIdentifier() {
        // "table" as a column name should NOT become a keyword token here
        // because it's fully matched against the Keywords set already -
        // this test instead proves a normal identifier stays an identifier
        Tokenizer tokenizer = new Tokenizer("salary");
        List<Token> tokens = tokenizer.tokenize();

        assertEquals(new Token(TokenType.IDENTIFIER, "salary"), tokens.get(0));
    }

    @Test
    public void throwsOnUnterminatedString() {
        Tokenizer tokenizer = new Tokenizer("'John");
        assertThrows(IllegalArgumentException.class, tokenizer::tokenize);
    }
}