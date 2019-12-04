package com.imperative;

import java.util.HashMap;
import java.util.Map;

class Environment {
    private final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();
    private final Map<String, Type> types = new HashMap<>();
    private static final Map<String, Type> aliases = new HashMap<>();

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

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme);
    }

    /**
     * TODO:
     * Type getType(Token name) {
     * if (types.containsKey(name.lexeme)) {
     * return types.get(name.lexeme);
     * }
     *
     * throw new RuntimeError(name, "Undefine variable '" + name.lexeme + "'.");
     * }
     */

    void define(Token name, Object value, Type type) {
        if (!values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            types.put(name.lexeme, type);
            return;
        }

        throw new RuntimeError(name, "Variable already defined in scope");
    }

    void defineType(Token name, Type type) {
        if (types.containsKey(name.lexeme)) {
            types.put(name.lexeme, type);
            return;
        }

        if (enclosing != null) {
            enclosing.defineType(name, type);
            return;
        }

        throw new RuntimeError(name, "Undefined variable " + name.lexeme);
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

    void defineTypeAlias(Token name, Type type) {
        aliases.put(name.lexeme, type);
    }

    Type getTypeAlias(Token name) {
        if (aliases.containsKey(name.lexeme)) {
            return aliases.get(name.lexeme);
        }
        throw new RuntimeError(name, "Cannot find type in this scope.");
    }
}
