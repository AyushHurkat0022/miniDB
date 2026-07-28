package com.minidb.cli;

import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Cli {
    private final Scanner scanner;
    private boolean running;

    public Cli(){
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    public void start() throws InterruptedException {
        printWelcome();

        while (running){
            System.out.print("MiniDB> ");
            String input = scanner.nextLine().trim();

            if(input.isEmpty()){
                continue;
            }
            handleInput(input);
        }

        System.out.println("Closing MiniDB....");
        sleep(1000);
        System.out.println("Thank You for using MiniDB");
    }

    public void handleInput(String input){
        String command = input.endsWith(";")?input.substring(0, input.length()-1) : input;
        String upper = command.toUpperCase();

        switch (upper){
            case "EXIT":
                running = false;
                break;
            case "HELP":
                printHelp();
                break;
            default:
                System.out.println("[Error] Unknown command: " + input);
                System.out.println("Type HELP to see available commands.");
        }
    }

    public void printWelcome(){
        System.out.println("~~ Welcome to the MiniDB ~~");
        System.out.println("Type HELP Or EXIT for actions.");
    }

    public void printHelp(){
        System.out.println("Available commands: ");
        System.out.println("    HELP - show this message");
        System.out.println("    EXIT - quit MiniDB");
        System.out.println("    (more commands coming in future sprints)");
    }
}
