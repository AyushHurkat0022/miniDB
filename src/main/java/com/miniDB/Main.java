package com.minidb;

import com.minidb.cli.Cli;
import com.minidb.command.CreateTableCommand;
import com.minidb.parser.Parser;
import com.minidb.tokenizer.Tokenizer;

public class Main {
    public static void main(String[] args) throws InterruptedException {
//        Cli cli = new Cli();
//        cli.start();
        Tokenizer t = new Tokenizer("CREATE TABLE employees (id INT PRIMARY KEY, name STRING, salary DOUBLE);");
        Parser p = new Parser(t.tokenize());
        CreateTableCommand cmd = (CreateTableCommand) p.parse();
        System.out.println(cmd.getTableName());
        cmd.getColumns().forEach(System.out::println);
    }
}
