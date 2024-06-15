package com.jepl.lang

import com.ezylang.evalex.EvaluationException
import com.ezylang.evalex.Expression
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

class Expressions {
    companion object {
        fun getArgs(arr: JsonArray) : List<String> {
            val out = mutableListOf<String>()
            for (elem in arr){
                val e = getExpression(elem)
                try {
                    out += e.evaluate().stringValue
                }catch (e: EvaluationException){
                    throw JEPLException(RuntimeException(e.message))
                }
            }

            return out
        }

        fun checkArguments(expected: Int, args: List<String>, `object`: JsonObject?= null) {
            if(expected != args.size){
                throw JEPLException(IllegalArgumentException("Not enough arguments to this function"), `object`)
            }
        }

        private fun getExpression(n: JsonElement) : Expression {
            val expr = Expression(n.asString)

            expr.with("JEPL_VERSION", Runner.langVersion)
            for (i in 0..Values.vars.size){
                for (map in Values.vars.iterator()){
                    var obj: Any
                    try {
                        obj = Integer.parseInt(map.value.toString())
                    }catch (e: NumberFormatException){
                        obj = map.value
                    }
                    expr.with(map.key, obj)
                }
            }
            return expr
        }
    }

}