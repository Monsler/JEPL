package com.jepl.lang.libraries.string;

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
        out.put("lowerCase", new Function() {
            @Override
            public void invoke(List<String> args) {
                String vname = args.getFirst();
                Values.addVar(vname, Values.getVar(vname).toLowerCase(Locale.ROOT));
            }
        });
        out.put("upperCase", new Function() {
            @Override
            public void invoke(List<String> args) {
                String vname = args.getFirst();
                Values.addVar(vname, Values.getVar(vname).toUpperCase(Locale.ROOT));
            }
        });
        return out;
    }
}
