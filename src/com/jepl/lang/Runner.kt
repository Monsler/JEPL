package com.jepl.lang

import java.io.File
import java.io.IOException

class Runner {
    companion object {
        const val langVersion = "1.2"

        fun runFile(file: String) {
            val codeOf: String
            try {
                codeOf = File(file).readText()
            }catch (e: IOException) {
                throw JEPLException(e)
            }
            Interpreter.interpret(codeOf)

            if (Values.funs.containsKey("main")) {
                Interpreter.interpret(Values.getFun("main").toString())
            }
        }

        fun runString(code: String) {
            Interpreter.interpret(code)
            if (Values.funs.containsKey("main")) {
                Interpreter.interpret(Values.getFun("main").toString())
            }
        }
    }
}