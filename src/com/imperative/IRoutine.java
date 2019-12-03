package com.imperative;

import java.util.List;

class IRoutine implements RoutineCallable {
    private final Stmt.Routine declaration;

    IRoutine(Stmt.Routine declaration) {
        this.declaration = declaration;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Evaluator evaluator, List<Object> arguments) {
        Environment environment = new Environment(evaluator.globals);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i), arguments.get(i), null);
        }

        try {
            evaluator.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            return returnValue.value;
        }

        return null;
    }

    @Override
    public String toString() {
        return "<routine " + declaration.name.lexeme + ">";
    }
}
