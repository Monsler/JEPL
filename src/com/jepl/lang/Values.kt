package com.jepl.lang

import com.google.gson.JsonParser
import com.jepl.lang.libraries.Function
import com.jepl.lang.libraries.Library

class Values {
    companion object {
        var funs: HashMap<String, String> = HashMap()
        var vars: HashMap<String, Any> = HashMap()
        var libs: HashMap<String, HashMap<String, Function>> = HashMap()

        fun addFun(name: String, body: String){
            if (!funs.containsKey(name)) {
                funs[name] = body
            } else {
                throw JEPLException(RuntimeException("Error [block ${Interpreter.block+1}] - function $name redefine"))
            }
        }

        fun getFun(name: String) : String? {
            return funs[name]
        }

        fun addVar(name: String, value: Any) {
            vars[name] = value
        }

        fun addLib(name: String) {
            val lib: Library
            try {
                lib = Class.forName("com.jepl.lang.libraries.$name.$name").newInstance() as Library
            }catch (e: NullPointerException) {
                throw JEPLException(e, JsonParser.parseString("{\"name\": \"?\", \"args\": []}").asJsonObject)
            }catch (e: ClassNotFoundException) {
                throw JEPLException(e, JsonParser.parseString("{\"name\": \"?\", \"args\": []}").asJsonObject)
            }
            libs[name] = lib.invoke()
        }
    }
}