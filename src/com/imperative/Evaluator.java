package com.imperative;

import java.util.ArrayList;
import java.util.List;

class Evaluator implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    final Environment globals = new Environment();
    private Environment environment = globals;

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Main.runtimeError(error);
        }
    }

    private void execute(Stmt statement) {
        statement.accept(this);
    }

    Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);

        environment.assign(expr.name, value);
        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case GREATER:
                if (left instanceof Double && right instanceof Integer) {
                    right = ((Integer) right).doubleValue();
                    return (double) left > (double) right;
                }

                if (left instanceof Integer && right instanceof Double) {
                    left = ((Integer) left).doubleValue();
                    return (double) left > (double) right;
                }

                if (left instanceof Double && right instanceof Double) {
                    return (double) left > (double) right;
                }
                return (int) left > (int) right;

            case GREATER_EQUAL:
                if (left instanceof Double && right instanceof Integer) {
                    right = ((Integer) right).doubleValue();
                    return (double) left >= (double) right;
                }

                if (left instanceof Integer && right instanceof Double) {
                    left = ((Integer) left).doubleValue();
                    return (double) left >= (double) right;
                }

                if (left instanceof Double && right instanceof Double) {
                    return (double) left >= (double) right;
                }
                return (int) left >= (int) right;
            case LESS:
                if (left instanceof Double && right instanceof Integer) {
                    right = ((Integer) right).doubleValue();
                    return (double) left < (double) right;
                }

                if (left instanceof Integer && right instanceof Double) {
                    left = ((Integer) left).doubleValue();
                    return (double) left < (double) right;
                }

                if (left instanceof Double && right instanceof Double) {
                    return (double) left < (double) right;
                }
                return (int) left < (int) right;
            case LESS_EQUAL:
                if (left instanceof Double && right instanceof Integer) {
                    right = ((Integer) right).doubleValue();
                    return (double) left <= (double) right;
                }

                if (left instanceof Integer && right instanceof Double) {
                    left = ((Integer) left).doubleValue();
                    return (double) left <= (double) right;
                }

                if (left instanceof Double && right instanceof Double) {
                    return (double) left <= (double) right;
                }

                return (int) left <= (int) right;
            case SLASH_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case PLUS:
                if (left instanceof Double && right instanceof Integer) {
                    right = ((Integer) right).doubleValue();
                    return (double) left + (double) right;
                }

                if (left instanceof Integer && right instanceof Double) {
                    left = ((Integer) left).doubleValue();
                    return (double) left + (double) right;
                }

                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }

                return (int) left + (int) right;
            case MINUS:
                if (left instanceof Double && right instanceof Integer) {
                    right = ((Integer) right).doubleValue();
                    return (double) left - (double) right;
                }

                if (left instanceof Integer && right instanceof Double) {
                    left = ((Integer) left).doubleValue();
                    return (double) left - (double) right;
                }

                if (left instanceof Double && right instanceof Double) {
                    return (double) left - (double) right;
                }

                return (int) left - (int) right;
            case SLASH:
                if (left instanceof Double && right instanceof Integer) {
                    right = ((Integer) right).doubleValue();
                    return (double) left / (double) right;
                }

                if (left instanceof Integer && right instanceof Double) {
                    left = ((Integer) left).doubleValue();
                    return (double) left / (double) right;
                }

                if (left instanceof Double && right instanceof Double) {
                    return (double) left / (double) right;
                }

                return (int) left / (int) right;
            case STAR:
                if (left instanceof Double && right instanceof Integer) {
                    right = ((Integer) right).doubleValue();
                    return (double) left * (double) right;
                }

                if (left instanceof Integer && right instanceof Double) {
                    left = ((Integer) left).doubleValue();
                    return (double) left * (double) right;
                }

                if (left instanceof Double && right instanceof Double) {
                    return (double) left * (double) right;
                }

                return (int) left * (int) right;
        }

        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof RoutineCallable)) {
            throw new RuntimeError(expr.paren, "Can only call functions.");
        }

        RoutineCallable function = (RoutineCallable) callee;
        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Expected " +
                    function.arity() + " arguments but got " +
                    arguments.size() + ".");
        }

        return function.call(this, arguments);
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object record = evaluate(expr.record);

        return ((IRecord) record).get(expr.name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitGetIndexExpr(Expr.GetIndex expr) {
        Object value = evaluate(expr.array);
        List<Expr> array = (ArrayList<Expr>) value;

        return evaluate(array.get(expr.index - 1));
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);

        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else if (expr.operator.type == TokenType.AND) {
            if (!isTruthy(left)) return left;

        } else if (expr.operator.type == TokenType.XOR) {

            return isTruthy(left) ^ isTruthy(evaluate(expr.right));

        }
        return evaluate(expr.right);
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case MINUS:
                checkOperand(expr.operator, right);
                return -(double) right;
            case NOT:
                return !isTruthy(right);
        }

        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name);
    }

    private void checkOperand(Token operator, Object operand) {
        if (operand instanceof Double || operand instanceof Integer) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private boolean isTruthy(Object object) {
        if (object == null)
            return false;
        if (object instanceof Boolean)
            return (boolean) object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) return "<null>";

        return object.toString();

    }

    @Override
    public Void visitArrayStmt(Stmt.Array stmt) {
        environment.define(stmt.name, stmt.members,
                new Type.ArrayType(stmt.name.lexeme));
        return null;
    }

    @Override
    public Void visitBodyStmt(Stmt.Body stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitRoutineStmt(Stmt.Routine stmt) {
        IRoutine routine = new IRoutine(stmt);
        environment.define(stmt.name, routine,
                new Type.RoutineType(stmt.name.lexeme));
        return null;
    }

    @Override
    public Void visitRangeStmt(Stmt.Range stmt) {
        return null;
    }

    @Override
    public Void visitForStmt(Stmt.For stmt) {
        int from = (int) evaluate(stmt.range.from);
        int to = (int) evaluate(stmt.range.to);

        environment.define(stmt.name, from, new Type.PrimitiveType(Primitive.INTEGER));

        if (stmt.reverse) {
            while (from > to) {
                execute(stmt.body);
                environment.assign(stmt.name, --from);
            }
        } else {
            while (from < to) {
                execute(stmt.body);
                environment.assign(stmt.name, ++from);
            }
        }
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitRecordStmt(Stmt.Record stmt) {
        environment.define(stmt.name, null,
                new Type.RecordType(stmt.name.lexeme));
        IRecord record = new IRecord(this, stmt.name.lexeme, stmt.fields);
        environment.assign(stmt.name, record);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) value = evaluate(stmt.value);

        throw new Return(value);
    }

    @Override
    public Void visitTypeDeclareStmt(Stmt.TypeDeclare stmt) {
        environment.defineType(stmt.name, stmt.type);
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var statement) {
        Object value = null;
        if (statement.initializer != null) {
            value = evaluate(statement.initializer);
        }

        // assertTypes(value, statement.type);

        environment.define(statement.name, value, statement.type);
        return null;
    }

   /* private Object assertTypeAnd(Object value, Type type) {
        if (value == null) return;
        if ()


        if (((Type.PrimitiveType) type).type == Primitive.INTEGER) {
            if (value instanceof Integer) {
                return type;
            }

        } else if (((Type.PrimitiveType) type).type == Primitive.REAL) {
            if (value instanceof Double ) {
                return type;
            }
            if (value instanceof Integer) {
                return new Type.PrimitiveType(Primitive.REAL);
            }
        } else if (((Type.PrimitiveType) type).type == Primitive.BOOLEAN) {
            if (value instanceof Boolean) {
                return type;
            }
        }
    } */

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body);
        }
        return null;
    }
}
