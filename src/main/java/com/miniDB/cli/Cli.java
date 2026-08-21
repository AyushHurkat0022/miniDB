package com.minidb.cli;

import com.minidb.tokenizer.Tokenizer;
import com.minidb.parser.Parser;
import com.minidb.command.Command;
import com.minidb.executor.Executor;
import com.minidb.storage.StorageEngine;
import com.minidb.exception.SyntaxException;
import com.minidb.exception.StorageException;

import java.util.Scanner;

public class Cli {

    private final Scanner scanner;
    private final Executor executor;
    private boolean running;

    public Cli() {
        this.scanner = new Scanner(System.in);
        this.executor = new Executor(new StorageEngine());
        this.running = true;
    }

    public void start() {
        printWelcome();

        while (running) {
            System.out.print("MiniDB> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            handleInput(input);
        }

        scanner.close();
        System.out.println("Goodbye.");
    }

    private void handleInput(String input) {
        String upperNoSemicolon = input.replace(";", "").trim().toUpperCase();

        if (upperNoSemicolon.equals("EXIT")) {
            running = false;
            return;
        }
        if (upperNoSemicolon.equals("HELP")) {
            printHelp();
            return;
        }

        try {
            Tokenizer tokenizer = new Tokenizer(input);
            Parser parser = new Parser(tokenizer.tokenize());
            Command command = parser.parse();
            String result = executor.execute(command);
            if (!result.isBlank()) {
                System.out.println(result);
            }
        } catch (SyntaxException e) {
            System.out.println("[SYNTAX ERROR] " + e.getMessage());
        } catch (StorageException e) {
            System.out.println("[STORAGE ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void printWelcome() {
        System.out.println("=================================");
        System.out.println(" MiniDB v0.4 - Lightweight SQL Engine");
        System.out.println(" Type HELP for commands, EXIT to quit.");
        System.out.println("=================================");
    }

    private void printHelp() {
        System.out.println("Available commands:");
        System.out.println("  CREATE DATABASE <name>;");
        System.out.println("  USE <name>;");
        System.out.println("  DROP DATABASE <name>;");
        System.out.println("  SHOW DATABASES;");
        System.out.println("  CREATE TABLE <name> (col TYPE [PRIMARY KEY], ...);");
        System.out.println("  DROP TABLE <name>;");
        System.out.println("  SHOW TABLES;");
        System.out.println("  DESCRIBE <table>;");
        System.out.println("  INSERT INTO <table> VALUES (...);");
        System.out.println("  SELECT * FROM <table>;");
        System.out.println("  DELETE FROM <table>;");
        System.out.println("  HELP");
        System.out.println("  EXIT");
    }
}