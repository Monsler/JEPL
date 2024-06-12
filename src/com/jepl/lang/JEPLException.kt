package com.jepl.lang

class JEPLException(e: Throwable): Exception(e) {
    private var block = Interpreter.block+1
    private var throwable = e

    override fun printStackTrace() {
        System.err.println("Error [block $block] - ${this.throwable.message}")
    }
}