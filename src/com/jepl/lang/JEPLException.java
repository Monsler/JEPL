package com.jepl.lang;

import static com.jepl.lang.Interpreter.block;

public class JEPLException extends RuntimeException {
    public JEPLException(Throwable e){
        super(e);
        System.err.println("Error [block " + block+1 + "] - " + e);
        System.exit(-1);
    }
}
