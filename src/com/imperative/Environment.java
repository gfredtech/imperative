package com.imperative;

import java.util.HashMap;
import java.util.Map;

class Environment {
    private final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();
    private final Map<String, Type> types = new HashMap<>();

    Environment() {
        enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name, "Undefine variable '" + name.lexeme + "'.");
    }

    /** TODO:
    Type getType(Token name) {
        if (types.containsKey(name.lexeme)) {
            return types.get(name.lexeme);
        }

        throw new RuntimeError(name, "Undefine variable '" + name.lexeme + "'.");
    } */

    void define(String name, Object value, Type type) {
        values.put(name, value);
        types.put(name, type);
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable " + name.lexeme);
    }
}
