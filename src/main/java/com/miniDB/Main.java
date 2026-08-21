package com.minidb;

import com.minidb.cli.Cli;
import com.minidb.command.CreateTableCommand;
import com.minidb.parser.Parser;
import com.minidb.tokenizer.Tokenizer;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Cli cli = new Cli();
        cli.start();
    }
}
