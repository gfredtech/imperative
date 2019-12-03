package com.imperative;

import java.util.List;

abstract class Stmt {
    abstract <R> R accept(Visitor<R> visitor);

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
        final Token name;
        final List<Expr> members;

        Array(Token name, List<Expr> members) {
            this.name = name;
            this.members = members;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitArrayStmt(this);
        }
    }

    static class Body extends Stmt {
        final List<Stmt> statements;

        Body(List<Stmt> statements) {
            this.statements = statements;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBodyStmt(this);
        }
    }

    static class Expression extends Stmt {
        final Expr expression;

        Expression(Expr expression) {
            this.expression = expression;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

    static class Routine extends Stmt {
        final Token name;
        final List<Token> params;
        final List<Stmt> body;
        final Type returnType;
        Routine(Token name, List<Token> params, List<Stmt> body, Type returnType) {
            this.name = name;
            this.params = params;
            this.body = body;
            this.returnType = returnType;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitRoutineStmt(this);
        }
    }

    static class Range extends Stmt {
        final Expr from;
        final Expr to;

        Range(Expr from, Expr to) {
            this.from = from;
            this.to = to;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitRangeStmt(this);
        }
    }

    static class For extends Stmt {
        final Token name;
        final boolean reverse;
        final Range range;
        final Stmt body;
        For(Token name, boolean reverse, Range range, Stmt body) {
            this.name = name;
            this.reverse = reverse;
            this.range = range;
            this.body = body;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitForStmt(this);
        }
    }

    static class If extends Stmt {
        final Expr condition;
        final Stmt thenBranch;
        final Stmt elseBranch;
        If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    static class Print extends Stmt {
        final Expr expression;

        Print(Expr expression) {
            this.expression = expression;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }

    static class Record extends Stmt {
        final Token name;
        final List<Stmt.Var> fields;

        Record(Token name, List<Stmt.Var> fields) {
            this.name = name;
            this.fields = fields;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitRecordStmt(this);
        }
    }

    static class Return extends Stmt {
        final Token keyword;
        final Expr value;

        Return(Token keyword, Expr value) {
            this.keyword = keyword;
            this.value = value;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }
    }

    static class TypeDeclare extends Stmt {
        final Token name;
        final Type type;

        TypeDeclare(Token name, Type type) {
            this.name = name;
            this.type = type;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTypeDeclareStmt(this);
        }
    }

    static class Var extends Stmt {
        final Token name;
        final Expr initializer;
        final Type type;
        Var(Token name, Expr initializer, Type type) {
            this.name = name;
            this.initializer = initializer;
            this.type = type;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    static class While extends Stmt {
        final Expr condition;
        final Stmt body;

        While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }
}
