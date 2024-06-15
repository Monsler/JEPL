package com.jepl.lang

import com.google.gson.JsonObject

class JEPLException(e: Throwable, at: JsonObject? = null): Exception(e) {
    private var block = Interpreter.block+1
    private var throwable = e

    init {
        System.err.println("Error [block $block] - ${this.throwable.message}\nInterpreter says that something is wrong here:   ${at!!}\n\nJVM stacktrace:")
    }
}