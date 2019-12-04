package com.imperative;

import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    R visitArrayStmt(Array stmt);
    R visitBodyStmt(Body stmt);
    R visitExpressionStmt(Expression stmt);
    R visitRoutineStmt(Routine stmt);
    R visitRangeStmt(Range stmt);
    R visitForStmt(For stmt);
    R visitIfStmt(If stmt);
    R visitPrintStmt(Print stmt);
    R visitRecordStmt(Record stmt);
    R visitReturnStmt(Return stmt);
    R visitTypeDeclareStmt(TypeDeclare stmt);
    R visitVarStmt(Var stmt);
    R visitWhileStmt(While stmt);
  }
  static class Array extends Stmt {
    Array(Token name, List<Expr> members) {
      this.name = name;
      this.members = members;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitArrayStmt(this);
    }

    final Token name;
    final List<Expr> members;
  }
  static class Body extends Stmt {
    Body(List<Stmt> statements) {
      this.statements = statements;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBodyStmt(this);
    }

    final List<Stmt> statements;
  }
  static class Expression extends Stmt {
    Expression(Expr expression) {
      this.expression = expression;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }

    final Expr expression;
  }
  static class Routine extends Stmt {
    Routine(Token name, List<Token> params, List<Token> types, List<Stmt> body, Type returnType) {
      this.name = name;
      this.params = params;
      this.types = types;
      this.body = body;
      this.returnType = returnType;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitRoutineStmt(this);
    }

    final Token name;
    final List<Token> params;
    final List<Token> types;
    final List<Stmt> body;
    final Type returnType;
  }
  static class Range extends Stmt {
    Range(Expr from, Expr to) {
      this.from = from;
      this.to = to;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitRangeStmt(this);
    }

    final Expr from;
    final Expr to;
  }
  static class For extends Stmt {
    For(Token name, boolean reverse, Range range, Stmt body) {
      this.name = name;
      this.reverse = reverse;
      this.range = range;
      this.body = body;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitForStmt(this);
    }

    final Token name;
    final boolean reverse;
    final Range range;
    final Stmt body;
  }
  static class If extends Stmt {
    If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStmt(this);
    }

    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;
  }
  static class Print extends Stmt {
    Print(Expr expression) {
      this.expression = expression;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStmt(this);
    }

    final Expr expression;
  }
  static class Record extends Stmt {
    Record(Token name, List<Stmt.Var> fields) {
      this.name = name;
      this.fields = fields;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitRecordStmt(this);
    }

    final Token name;
    final List<Stmt.Var> fields;
  }
  static class Return extends Stmt {
    Return(Token keyword, Expr value) {
      this.keyword = keyword;
      this.value = value;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitReturnStmt(this);
    }

    final Token keyword;
    final Expr value;
  }
  static class TypeDeclare extends Stmt {
    TypeDeclare(Token name, Type type) {
      this.name = name;
      this.type = type;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitTypeDeclareStmt(this);
    }

    final Token name;
    final Type type;
  }
  static class Var extends Stmt {
    Var(Token name, Expr initializer, Type type) {
      this.name = name;
      this.initializer = initializer;
      this.type = type;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStmt(this);
    }

    final Token name;
    final Expr initializer;
    final Type type;
  }
  static class While extends Stmt {
    While(Expr condition, Stmt body) {
      this.condition = condition;
      this.body = body;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitWhileStmt(this);
    }

    final Expr condition;
    final Stmt body;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
