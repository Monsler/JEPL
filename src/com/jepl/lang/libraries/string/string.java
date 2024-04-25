package com.jepl.lang.libraries.string;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import com.jepl.lang.JEPLException;
import com.jepl.lang.Values;
import com.jepl.lang.libraries.Function;
import com.jepl.lang.libraries.Library;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class string implements Library {
    @Override
    public HashMap<String, Function> invoke() {
        HashMap<String, Function> out = new HashMap<>();
        out.put("string_lowerCase", new Function() {
            @Override
            public void invoke(List<String> args) {
                String vname = args.get(0);
                Values.addVar(vname, Values.getVar(vname).toLowerCase(Locale.ROOT));
            }
        });
        out.put("string_upperCase", new Function() {
            @Override
            public void invoke(List<String> args) {
                String vname = args.get(0);
                Values.addVar(vname, Values.getVar(vname).toUpperCase(Locale.ROOT));
            }
        });
        out.put("string_reverse", new Function() {
            @Override
            public void invoke(List<String> args) {
                String vname = args.get(0);
                Values.addVar(vname, new StringBuilder(Values.getVar(vname)).reverse().toString());
            }
        });
        out.put("string_repeat", new Function() {
            @Override
            public void invoke(List<String> args) {
                String vname = args.get(0);
                int c = Integer.parseInt(args.get(1));
                Values.addVar(vname, Values.getVar(vname).repeat(c));
            }
        });
        out.put("string_size", new Function() {
            @Override
            public void invoke(List<String> args) {
                String vname = args.get(0);
                Values.addVar(vname, String.valueOf(Values.getVar(vname).length()));
            }
        });
        out.put("string_parse", new Function() {
            @Override
            public void invoke(List<String> args) {
                String e = Values.vars.get(args.get(0));
                String vname = args.get(0);
                Expression expr = new Expression(e);
                try {
                    Values.addVar(vname, expr.evaluate().getStringValue());
                } catch (EvaluationException | ParseException ex) {
                    throw new JEPLException(new RuntimeException(ex));
                }

            }
        });
        return out;
    }
}
