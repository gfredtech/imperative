package com.imperative;


import com.imperative.Expr;

import java.util.List;

class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<String>, Type.Visitor<String> {

    void print(List<Stmt> stmts) {
        for (Stmt stmt : stmts) {
            System.out.println(print(stmt));
        }
    }

    private String print(Expr expr) {
        return expr.accept(this);
    }

    String print(Stmt stmt) {
        return stmt.accept(this);
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return expr.name.lexeme + " <- " + print(expr.value);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitCallExpr(Expr.Call expr) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(expr.accept(this));

        for (Expr e: expr.arguments) {
            builder.append(e.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    @Override
    public String visitGetExpr(Expr.Get expr) {
        return expr.record.accept(this) + "." + expr.name.lexeme;
    }

    @Override
    public String visitGetIndexExpr(Expr.GetIndex expr) {
        return print(expr) + "[" + expr.index + "];";
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        return expr.value.toString();
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name.lexeme;
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    @Override
    public String visitArrayStmt(Stmt.Array stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append(stmt.name.lexeme).append(" => ").append("[");
        for (Expr expr: stmt.members) {
            builder.append(print(expr)).append(" ");
        }
        builder.append("\b]");
        return builder.toString();
    }

    @Override
    public String visitBodyStmt(Stmt.Body stmt) {
        StringBuilder builder = new StringBuilder();
        for (Stmt s: stmt.statements) {
            builder.append(print(s));
        }
        return builder.toString();
    }

    @Override
    public String visitExpressionStmt(Stmt.Expression stmt) {
        return print(stmt.expression);
    }

    @Override
    public String visitRoutineStmt(Stmt.Routine stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(stmt.name).append(" [");
        for (Token s: stmt.params) {
            builder.append(s.lexeme).append(" ");
        }

        for (Stmt s: stmt.body) {
            builder.append(print(s));
        }

        return builder.toString();
    }

    @Override
    public String visitRangeStmt(Stmt.Range stmt) {
        return "range(" + print(stmt.from) + ", " + print(stmt.to) + ")";
    }

    @Override
    public String visitForStmt(Stmt.For stmt) {
        StringBuilder builder = new StringBuilder();

        builder.append("for ")
                .append(stmt.name.lexeme)
                .append(" in ")
                .append(visitRangeStmt(stmt.range))
                .append(":\n\t");

        builder.append(print(stmt.body));


        return builder.toString();
    }

    @Override
    public String visitIfStmt(Stmt.If stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("if ").
                append(print(stmt.condition)).append(":\n\t");
        builder.append(print(stmt.thenBranch)).append("\n");
        builder.append("\relse:\n\t");
        builder.append(print(stmt.elseBranch)).append("\n");
        return builder.toString();
    }

    @Override
    public String visitPrintStmt(Stmt.Print stmt) {
        return "println(" + print(stmt.expression) + ");";
    }

    @Override
    public String visitRecordStmt(Stmt.Record stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("struct ").append(stmt.name.lexeme).append("{");
        for (Stmt s: stmt.fields) {
            builder.append(print(s)).append(" ");
        }
        builder.append("};");
        return builder.toString();
    }

    @Override
    public String visitReturnStmt(Stmt.Return stmt) {
        return "return" + print(stmt.value);
    }

    @Override
    public String visitTypeDeclareStmt(Stmt.TypeDeclare stmt) {
        return null;
    }

    @Override
    public String visitVarStmt(Stmt.Var stmt) {
        Type type = stmt.type;
        if (type == null) {
            type = new Type.PrimitiveType(Primitive.INTEGER);
        }
        return print(type) + " "
                + stmt.name.lexeme + " = "
                + print(stmt.initializer);
    }

    private String print(Type type) {
        return type.accept(this);
    }

    @Override
    public String visitWhileStmt(Stmt.While stmt) {
        return null;
    }

    @Override
    public String visitArrayTypeType(Type.ArrayType type) {
        return null;
    }

    @Override
    public String visitPrimitiveTypeType(Type.PrimitiveType type) {
        switch (type.type) {
            case BOOLEAN:
                return "bool";
            case INTEGER:
                return "int";
            case REAL:
                return "double";

        }
        return null;
    }

    @Override
    public String visitRoutineTypeType(Type.RoutineType type) {
        return null;
    }

    @Override
    public String visitRecordTypeType(Type.RecordType type) {
        return null;
    }
}
