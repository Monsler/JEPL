package com.jepl.lang.libraries;

import java.util.HashMap;

public interface Library {
    HashMap<String, Function> invoke();
}
