package com.jepl.lang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Runner {
    public static void runFile(String file){
        String text;
        try {
            text = Files.readString(Path.of(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Interpreter.interpret(text);
    }

    public static void runString(String input){
        Interpreter.interpret(input);
    }

    public static double langVersion = 1.1;
}
