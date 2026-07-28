package com.minidb.util;

public class Logger {
    private static final boolean DEBUG = false;

    public static void info(String message){
        System.out.println("[INFO] "+message);
    }

    public static void error(String message){
        System.out.println("[ERROR] "+message);
    }

    public static void debug(String message){
        if(DEBUG){
            System.out.println("[DEBUG] "+message);
        }
    }
}
