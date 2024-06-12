package com.jepl.lang.libraries.string

import com.ezylang.evalex.EvaluationException
import com.ezylang.evalex.Expression
import com.ezylang.evalex.parser.ParseException
import com.jepl.lang.JEPLException
import com.jepl.lang.Runner
import com.jepl.lang.Values
import com.jepl.lang.libraries.Function
import com.jepl.lang.libraries.Library

class string : Library {
    override fun invoke(): HashMap<String, Function> {
        val out = HashMap<String, Function>()
        out["string_lowerCase"] = object : Function {
            override fun invoke(args: List<String>) {
                val vname = args[0]
                val to = args[1]
                Values.addVar(to, vname.lowercase())
            }
        }
        out["string_upperCase"] = object : Function {
            override fun invoke(args: List<String>) {
                val vname = args[0]
                val to = args[1]
                Values.addVar(to, vname.uppercase())
            }
        }
        out["string_reverse"] = object : Function {
            override fun invoke(args: List<String>) {
                val vname = args[0]
                val to = args[1]
                Values.addVar(to!!, StringBuilder(vname).reverse().toString())
            }
        }
        out["string_repeat"] = object : Function {
            override fun invoke(args: List<String>) {
                val vname = args[0]
                val to = args[2]
                val c = args[1].toInt()
                Values.addVar(to, vname.repeat(c))
            }
        }
        out["string_size"] = object : Function {
            override fun invoke(args: List<String>) {
                val vname = args[0]
                val to = args[1]
                Values.addVar(to, vname.length)
            }
        }
        out["string_parse"] = object : Function {
            override fun invoke(args: List<String>) {
                val e = args[0]
                val vname = args[0]
                val expr = Expression(e)
                expr.with("jeplversion", Runner.langVersion)
                for ((key, value) in Values.vars) {
                    var `val` = try {
                        value.toString().toInt()
                    } catch (ev: NumberFormatException) {
                        value
                    }
                    expr.with(key, `val`)
                }
                try {
                    Values.addVar(vname!!, expr.evaluate().stringValue)
                } catch (ex: Exception) {
                    throw JEPLException(RuntimeException(ex))
                } catch (ex: ParseException) {
                    throw JEPLException(RuntimeException(ex))
                }
            }
        }
        out["string_concat"] = object : Function {
            override fun invoke(args: List<String>) {
                val str1 = args[0]
                val str2 = args[1]
                val name = args[2]
                Values.addVar(name, str1 + str2)
            }
        }
        out["string_indexOf"] = object : Function {
            override fun invoke(args: List<String>) {
                val str = args[0]
                val index = args[1]
                val `var` = args[2]
                Values.addVar(`var`, str.indexOf(index))
            }
        }
        return out
    }
}