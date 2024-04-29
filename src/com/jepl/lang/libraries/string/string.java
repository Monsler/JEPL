package com.jepl.lang.libraries.string;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import com.jepl.lang.JEPLException;
import com.jepl.lang.Runner;
import com.jepl.lang.Values;
import com.jepl.lang.libraries.Function;
import com.jepl.lang.libraries.Library;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class string implements Library {
    @Override
    public HashMap<String, Function> invoke() {
        HashMap<String, Function> out = new HashMap<>();
        out.put("string_lowerCase", args -> {
            String vname = args.get(0);
            String to = args.get(1);
            Values.addVar(to, vname.toLowerCase(Locale.ROOT));
        });
        out.put("string_upperCase", args -> {
            String vname = args.get(0);
            String to = args.get(1);
            Values.addVar(to, vname.toUpperCase(Locale.ROOT));
        });
        out.put("string_reverse", args -> {
            String vname = args.get(0);
            String to = args.get(1);
            Values.addVar(to, new StringBuilder(vname).reverse().toString());
        });
        out.put("string_repeat", args -> {
            String vname = args.get(0);
            String to = args.get(2);
            final int c = Integer.parseInt(args.get(1));
            Values.addVar(to, vname.repeat(c));
        });
        out.put("string_size", args -> {
            String vname = args.get(0);
            String to = args.get(1);
            Values.addVar(to, vname.length());
        });
        out.put("string_parse", args -> {
            String e = args.get(0);
            String vname = args.get(0);
            Expression expr = new Expression(e);
            expr.with("jeplversion", Runner.langVersion);
            for(Map.Entry<String, Object> map: Values.vars.entrySet()) {
                Object val;
                try {
                    val = Integer.parseInt(map.getValue().toString());
                }catch (NumberFormatException ev){
                    val = map.getValue();
                }
                expr.with(map.getKey(), val);
            }
            try {
                Values.addVar(vname, expr.evaluate().getStringValue());
            } catch (EvaluationException | ParseException ex) {
                throw new JEPLException(new RuntimeException(ex));
            }
        });
        out.put("string_concat", args -> {
           final String str1 = args.get(0);
           final String str2 = args.get(1);
           final String name = args.get(2);
           Values.addVar(name, str1+str2);
        });
        out.put("string_indexOf", args -> {
            final String str = args.get(0);
            final String index = args.get(1);
            final String var = args.get(2);
            Values.addVar(var, str.indexOf(index));
        });
        return out;
    }
}
