package com.jepl.lang;

import com.jepl.lang.libraries.Function;
import com.jepl.lang.libraries.Library;

import java.util.HashMap;

public class Values {
    public static HashMap<String, String> funs = new HashMap<>(), vars = new HashMap<>();
    public static HashMap<String, HashMap<String, Function>> libs = new HashMap<>();

    public static void addFun(String name, String body){
        if(!funs.containsKey(name)) {
            funs.put(name, body);
        }else {
            System.err.println("Error! Function "+name+" already defined.");
        }
    }

    public static String getFun(String name){
        if(funs.containsKey(name)){
            return funs.get(name);
        }
        return "{\"line1\": {\"name\": \"println\", \"args\": [\"\"Function "+name+" is undefined!\"\"]}}";
    }

    public static void addVar(String name, String value) {
        vars.put(name, value);
    }

    public static void addLib(String name){
        Library lib;
        try {
            lib = (Library) Class.forName("com.jepl.lang.libraries."+name+"."+name).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new JEPLException(e);
        }
        libs.put(name, lib.invoke());
    }

    public static String getVar(String name){
        if(vars.containsKey(name)){
            return vars.get(name);
        }else {
            throw new JEPLException(new Throwable("Error! Variable "+name+" is not exists."));
        }
    }
}
