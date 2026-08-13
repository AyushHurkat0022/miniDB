package com.minidb.parser;

import com.minidb.command.*;
import com.minidb.exception.SyntaxException;
import com.minidb.model.ColumnDefinition;
import com.minidb.tokenizer.Token;
import com.minidb.tokenizer.TokenType;

import java.util.List;
import java.util.ArrayList;

public class Parser {

    private final List<Token> tokens;
    private int position;

    public Parser(List<Token> tokens){
        this.tokens = tokens;
        this.position = 0;
    }

    private Token peek(){
        return tokens.get(position);
    }

    private Token advance(){
        Token token = tokens.get(position);
        position++;
        return token;
    }

    private boolean check(TokenType type, String value){
        Token token = peek();
        return token.getType() == type && token.getValue().equalsIgnoreCase(value);
    }

    private Token expect(TokenType type, String expectedValueOrNull){
        Token token = advance();
        if(token.getType()!= type || (expectedValueOrNull != null && !token.getValue().equalsIgnoreCase(expectedValueOrNull))){
            throw new SyntaxException(
                    "Expected " + type + (expectedValueOrNull != null ? " '" + expectedValueOrNull + "'" : "") + " but found "+ token
            );
        }
        return token;
    }

    public Command parse(){
        Token first = peek();

        if(first.getType()!=TokenType.KEYWORD){
            throw new SyntaxException("Expected a command keyword but found " + first);
        }

        switch (first.getValue()){
            case "CREATE":
                return parseCreate();
            case "USE":
                return parseUse();
            case "DROP":
                return parseDrop();
            case "SHOW":
                return parseShow();
            case "DESCRIBE":
                return parseDescribe();
            case "INSERT":
                return parseInsert();
            case "SELECT":
                return parseSelect();
            case "DELETE":
                return parseDelete();
            default:
                throw new SyntaxException("Unsupported Command: " + first);
        }
    }

    private void expectSemicolon(){
        expect(TokenType.SYMBOL, ";");
    }

    private Command parseCreate(){
        expect(TokenType.KEYWORD, "CREATE");
        Token next = peek();

        if(next.getValue().equalsIgnoreCase("DATABASE")){
            advance();
            String dbName = expect(TokenType.IDENTIFIER, null).getValue();
            expectSemicolon();
            return new CreateDatabaseCommand(dbName);
        }

        if(next.getValue().equalsIgnoreCase("TABLE")){
            advance();
            String tableName = expect(TokenType.IDENTIFIER, null).getValue();
            List<ColumnDefinition>columns = parseColumnDefinitions();
            expectSemicolon();
            return new CreateTableCommand(tableName, columns);
        }
        throw new SyntaxException("Expected DATABASE or TABLE after CREATE, found "+next);
    }

    private List<ColumnDefinition> parseColumnDefinitions(){
        expect(TokenType.SYMBOL, "(");
        List<ColumnDefinition> columns = new ArrayList<>();

        while (true){
            String name = expect(TokenType.IDENTIFIER, null).getValue();
            String type = expect(TokenType.KEYWORD, null).getValue();

            boolean primaryKey = false;
            if(check(TokenType.KEYWORD, "PRIMARY")){
                advance();
                expect(TokenType.KEYWORD, "KEY");
                primaryKey = true;
            }

            columns.add(new ColumnDefinition(name, type, primaryKey));

            if (check(TokenType.SYMBOL, ",")){
                advance();
                continue;
            }
            break;
        }

        expect(TokenType.SYMBOL, ")");
        return columns;
    }

    private Command parseUse(){
        expect(TokenType.KEYWORD, "USE");
        String dbName = expect(TokenType.IDENTIFIER, null).getValue();
        expectSemicolon();
        return new UseDatabaseCommand(dbName);
    }

    private Command parseDrop(){
        expect(TokenType.KEYWORD, "DROP");
        Token next = peek();

        if(next.getValue().equalsIgnoreCase("DATABASE")){
            advance();
            String dbName = expect(TokenType.IDENTIFIER, null).getValue();
            expectSemicolon();
            return new DropDatabaseCommand(dbName);
        }

        if(next.getValue().equalsIgnoreCase("TABLE")){
            advance();
            String tableName = expect(TokenType.IDENTIFIER, null).getValue();
            expectSemicolon();
            return new DropTableCommand(tableName);
        }

        throw new SyntaxException("Expected DATABASE or TABLE after DROP, found "+next);
    }

    private Command parseShow(){
        expect(TokenType.KEYWORD, "SHOW");
        Token next = advance();

        if(next.getValue().equalsIgnoreCase("DATABASES")){
            expectSemicolon();
            return new ShowDatabaseCommand();
        }

        if(next.getValue().equalsIgnoreCase("TABLES")){
            expectSemicolon();
            return new ShowTableCommand();
        }

        throw new SyntaxException("Expected DATABASES or TABLES after SHOW, found "+next);
    }

    private Command parseDescribe(){
        expect(TokenType.KEYWORD, "DESCRIBE");
        String tableName = expect(TokenType.IDENTIFIER, null).getValue();
        expectSemicolon();
        return new DescribeTableCommand(tableName);
    }

    private Command parseInsert(){
        expect(TokenType.KEYWORD, "INSERT");
        expect(TokenType.KEYWORD, "INTO");
        String tableName = expect(TokenType.IDENTIFIER, null).getValue();
        expect(TokenType.KEYWORD, "VALUES");
        expect(TokenType.SYMBOL, "(");

        List<String> values = new ArrayList<>();
        while(true){
            Token token = advance();
            if(token.getType() != TokenType.NUMBER && token.getType()!= TokenType.STRING){
                throw new SyntaxException("Expected a value (number or string) but found " + token);
            }
            values.add(token.getValue());

            if(check(TokenType.SYMBOL, ",")){
                advance();
                continue;
            }
            break;
        }

        expect(TokenType.SYMBOL, ")");
        expectSemicolon();
        return new InsertCommand(tableName, values);
    }

    private Command parseSelect(){
        expect(TokenType.KEYWORD, "SELECT");

        List<String> cols = new ArrayList<>();
        if(check(TokenType.SYMBOL, "*")){
            advance();
            cols.add("*");
        }
        else{
            while(true){
                String colName = expect(TokenType.IDENTIFIER, null).getValue();
                cols.add(colName);
                if(check(TokenType.SYMBOL, ",")){
                    advance();
                    continue;
                }
                break;
            }
        }
        expect(TokenType.KEYWORD, "FROM");
        String tableName = expect(TokenType.IDENTIFIER, null).getValue();
        expectSemicolon();
        return new SelectCommand(tableName,cols);
    }

    private Command parseDelete(){
        expect(TokenType.KEYWORD, "DELETE");
        expect(TokenType.KEYWORD, "FROM");
        String tableName = expect(TokenType.IDENTIFIER, null).getValue();
        expectSemicolon();
        return new DeleteCommand(tableName);
    }
}

