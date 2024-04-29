package com.jepl.lang;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jepl.lang.libraries.Function;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class Interpreter {
    public static int block = 0;
    public static void interpret(String input){

        JsonArray code = JsonParser.parseString(input).getAsJsonArray();
        Scanner scanner = new Scanner(System.in);
        for(int i = 0; i < code.size(); i++){
            block = i;
            JsonObject o = code.get(i).getAsJsonObject();
            String name = o.get("name").getAsString();
            JsonArray args = o.get("args").getAsJsonArray();
            if(name.equals("print")){
                List<String> arg = getArgs(args);
                for(String a: arg){
                    System.out.print(a+" ");
                }
            }else if (name.equals("exit")){
                System.exit(args.get(0).getAsInt());
            }else if(name.equals("println")){
                List<String> arg = getArgs(args);
                for(String a: arg){
                    System.out.println(a);
                }
            }else if(name.equals("function")){
                String body = o.get("body").getAsJsonArray().toString();
                String funName = getArgs(args).get(0);
                Values.addFun(funName, body);
            }else if(name.equals("jump")){
                final List<String> arg = getArgs(args);
                String funName = arg.get(0);
                int argc = 0;
                if(arg.size() > 1) {
                    for (int a = 1; a < arg.size(); a++) {
                        argc = a;
                        Values.addVar("arg" + a, arg.get(a));
                    }
                }
                Values.vars.put("argc", argc);
                try {
                    Interpreter.interpret(Values.getFun(funName));
                }catch (IllegalStateException e){
                    throw new JEPLException(new NoSuchFieldException(funName + " function is not exist"));
                }
                Values.vars.remove("argc");
                if(arg.size() > 1) {
                    for (int a = 1; a < arg.size(); a++) {
                        Values.vars.remove("arg" + a);
                    }
                }
            }else if(name.equals("for")){
                String body = o.get("body").getAsJsonArray().toString();
                List<String> arg = getArgs(args);
                int from, to;
                try {
                    from = Integer.parseInt(arg.get(0));
                    to = Integer.parseInt(arg.get(1));
                }catch (NumberFormatException e){
                    throw new JEPLException(new RuntimeException(e));
                }
                for(int x = from; x <= to; x++){
                    Values.addVar("index", String.valueOf(x));
                    Interpreter.interpret(body);
                }
                Values.vars.remove("index");
            } else if (name.equals("variable")) {
                List<String> arg = getArgs(args);
                String n = arg.get(0);
                String v = arg.get(1);
                Values.addVar(n, v);
            }else if(name.equals("input")) {
                List<String> arg = getArgs(args);
                String text = arg.get(1);
                String var = arg.get(0);
                System.out.println(text);
                String inp = scanner.nextLine();
                Values.addVar(var, inp);
            }else if(name.equals("rmvar")) {
                List<String> arg = getArgs(args);
                String var = arg.get(0);
                if (Values.vars.containsKey(var)) {
                    Values.vars.remove(var);
                } else {
                    throw new JEPLException(new RuntimeException("Warning [block "+i+" - variable "+var+" not exists!"));
                }
            }else if(name.equals("include")) {
                List<String> arg = getArgs(args);
                String filename = arg.get(0);
                try {
                    Interpreter.interpret(Files.readString(Path.of(filename)));
                } catch (IOException e) {
                    throw new JEPLException(new FileNotFoundException("Error [block "+block + 1+" - file "+filename+" is not exists!"));
                }
            }else if(name.equals("while")) {
                List<String> arg = getArgs(args);
                final String body = o.get("body").getAsJsonArray().toString();
                boolean condition = Boolean.parseBoolean(arg.get(0));
                while (condition) {
                    Interpreter.interpret(body);
                    arg = getArgs(args);
                    condition = Boolean.parseBoolean(arg.get(0));
                    if (!condition) {
                        break;
                    }
                }
            }else if(name.equals("if")) {
                List<String> arg = getArgs(args);
                boolean condition = Boolean.parseBoolean(arg.get(0));
                String body = o.get("body").getAsJsonArray().toString();
                if (condition) {
                    Interpreter.interpret(body);
                }
            }else if(name.equals("import")) {
                List<String> arg = getArgs(args);
                for(String s: arg){
                    if(!Values.libs.containsKey(s)) {
                        Values.addLib(s);
                    }else {
                        throw new JEPLException(new RuntimeException("Library <"+s+"> already defined!"));
                    }
                }
            }else if(name.equals("eval")) {
                List<String> arg = getArgs(args);
                String eval = arg.get(0);
                Interpreter.interpret(eval);
            }else if(name.equals("runtime_execute")) {
                List<String> arg = getArgs(args);
                String command = arg.get(0);
                try {
                    Runtime.getRuntime().exec(command);
                } catch (IOException e) {
                    throw new JEPLException(new RuntimeException(e));
                }
            }else if(name.equals("unload")) {
                List<String> arg = getArgs(args);
                for(String s: arg){
                    Values.libs.remove(s);
                }
            }else if(name.equals("getProperty")) {
                final List<String> arg = getArgs(args);
                String prop = arg.get(0);
                String to = arg.get(1);
                Values.addVar(to, System.getProperty(prop));
            }else if(name.equals("importJar")){
                final List<String> arg = getArgs(args);
                String path = System.getProperty("user.dir")+"/"+arg.get(0);
                String addressLib = arg.get(1);
                try {
                    URLClassLoader child = new URLClassLoader(
                            new URL[] { new URL("file:" + path) },
                            Interpreter.class.getClassLoader()
                    );
                    Class<?> classToLoad = Class.forName(addressLib + ".Lib", true, child);
                    Method method = classToLoad.getDeclaredMethod("invoke");
                    Object instance = classToLoad.getDeclaredConstructor().newInstance();
                    HashMap<String, Function> lib = (HashMap<String, Function>) method.invoke(instance);
                    Values.libs.put(addressLib,  lib);

                } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                         InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }else{
                List<String> arg = getArgs(args);
                int index = 0;
                for(Map.Entry<String, HashMap<String, Function>> lib: Values.libs.entrySet()){
                    if(lib.getValue().containsKey(name)) {
                        lib.getValue().get(name).invoke(arg);
                        break;
                    }
                    index++;
                }
                if (index == Values.libs.size()){
                    throw new JEPLException(new NoSuchFieldException("Cannot find method "+name));
                }
            }
        }
    }

    public static List<String> getArgs(JsonArray a){
        List<String> out = new ArrayList<>();
        for(JsonElement n: a){
            Expression e = getExpression(n);
            try {
                out.add(e.evaluate().getStringValue());
            } catch (EvaluationException | ParseException ex) {
                throw new JEPLException(new RuntimeException(ex.getMessage()));
            }
        }
        return out;
    }

    public static Expression getExpression(JsonElement n) {
        Expression e = new Expression(n.getAsString());
        e.with("jeplversion", Runner.langVersion);
        for(int i = 0; i < Values.vars.size(); i++){
            for(Map.Entry<String, Object> map: Values.vars.entrySet()) {
                Object val;
                try {
                    val = Integer.parseInt(map.getValue().toString());
                }catch (NumberFormatException ev){
                    val = map.getValue();
                }
                e.with(map.getKey(), val);
            }
        }
        return e;
    }
}
