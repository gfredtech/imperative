package com.imperative;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IRecord implements RoutineCallable {
    final String name;
    private final Map<String, Object> fields = new HashMap<>();

    public IRecord(Evaluator evaluator, String name, List<Stmt.Var> varFields) {
        this.name = name;
        for (Stmt.Var field : varFields) {
            set(field.name, evaluator.evaluate(field.initializer));
        }
    }

    Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    private void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public Object call(Evaluator evaluator, List<Object> arguments) {
        return this;
    }
}
