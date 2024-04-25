package com.jepl.lang.libraries.math;

import com.jepl.lang.Values;
import com.jepl.lang.libraries.Function;
import com.jepl.lang.libraries.Library;

import java.util.HashMap;
import java.util.List;

public class math implements Library {
    @Override
    public HashMap<String, Function> invoke() {
        HashMap<String, Function> out = new HashMap<>();
        out.put("math_random", new Function() {
            @Override
            public void invoke(List<String> args) {
                int range = Integer.parseInt(args.get(1));
                int min = Integer.parseInt(args.get(0));
                String var = args.get(2);
                int rand = (int)(Math.random() * range) + min;
                Values.addVar(var, String.valueOf(rand));
            }
        });
        out.put("math_round", new Function() {
            @Override
            public void invoke(List<String> args) {
                float num = Float.parseFloat(args.get(0));
                String var = args.get(1);
                Values.addVar(var, String.valueOf(num));
            }
        });
        out.put("math_sqr", new Function() {
            @Override
            public void invoke(List<String> args) {
                int a = Integer.parseInt(args.get(0));
                String var = args.get(1);
                Values.addVar(var, String.valueOf(a*a));
            }
        });
        return out;
    }
}
