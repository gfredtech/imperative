package com.imperative;

import java.util.List;

abstract class Type {
  interface Visitor<R> {
    R visitArrayTypeType(ArrayType type);
    R visitPrimitiveTypeType(PrimitiveType type);
    R visitRoutineTypeType(RoutineType type);
    R visitRecordTypeType(RecordType type);
  }
  static class ArrayType extends Type {
    ArrayType(String name) {
      this.name = name;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitArrayTypeType(this);
    }

    final String name;
  }
  static class PrimitiveType extends Type {
    PrimitiveType(Primitive type) {
      this.type = type;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrimitiveTypeType(this);
    }

    final Primitive type;
  }
  static class RoutineType extends Type {
    RoutineType(String name) {
      this.name = name;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitRoutineTypeType(this);
    }

    final String name;
  }
  static class RecordType extends Type {
    RecordType(String name) {
      this.name = name;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitRecordTypeType(this);
    }

    final String name;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
