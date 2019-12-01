package com.imperative;

abstract class Type {
    abstract <R> R accept(Visitor<R> visitor);

    interface Visitor<R> {
        R visitPrimitiveTypeType(PrimitiveType type);

        R visitRoutineTypeType(RoutineType type);
    }

    static class PrimitiveType extends Type {
        final Primitive type;

        PrimitiveType(Primitive type) {
            this.type = type;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrimitiveTypeType(this);
        }
    }

    static class RoutineType extends Type {
        final String name;

        RoutineType(String name) {
            this.name = name;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitRoutineTypeType(this);
        }
    }
}
