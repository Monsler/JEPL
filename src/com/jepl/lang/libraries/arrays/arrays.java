package com.jepl.lang.libraries.arrays;

import com.jepl.lang.Values;
import com.jepl.lang.libraries.Function;
import com.jepl.lang.libraries.Library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class arrays implements Library {

    @Override
    public HashMap<String, Function> invoke() {
        HashMap<String, Function> out = new HashMap<>();
        out.put("array_new", args -> {
            final String vname = args.get(0);
            Values.addVar(vname, new ArrayList<>());
        });

        out.put("array_put", args -> {
           ArrayList<Object> list = (ArrayList<Object>) Values.vars.get(args.get(0));
           final String value = args.get(1);
           list.add(value);
           Values.addVar(args.get(0), list);
        });

        out.put("array_get", args -> {
            ArrayList<Object> list = (ArrayList<Object>) Values.vars.get(args.get(0));
            final String var = args.get(2);
            final int index = Integer.parseInt(args.get(1));
            Values.addVar(var, list.get(index-1));
        });

        out.put("array_unset", args -> {
            ArrayList<Object> list = (ArrayList<Object>) Values.vars.get(args.get(0));
            final int index = Integer.parseInt(args.get(1));
            list.remove(index-1);
            Values.addVar(args.get(0), list);
        });

        out.put("array_putIndex", args -> {
            ArrayList<Object> list = (ArrayList<Object>) Values.vars.get(args.get(0));
            final int index = Integer.parseInt(args.get(1));
            final String of = args.get(2);
            list.add(index-1, of);
            Values.addVar(args.get(0), list);
        });
        return out;
    }
}
