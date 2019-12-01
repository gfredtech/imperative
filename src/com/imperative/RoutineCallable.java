package com.imperative;

import java.util.List;

interface RoutineCallable {
    int arity();

    Object call(Evaluator evaluator, List<Object> arguments);
}
