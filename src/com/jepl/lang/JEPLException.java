package com.jepl.lang;
import static com.jepl.lang.Interpreter.block;

public class JEPLException extends RuntimeException {
    public JEPLException(Throwable e){
        super(e);
        int b = block+1;

        System.err.println("Error [block " + b + "] - " + e);
        System.exit(-1);
    }
}
