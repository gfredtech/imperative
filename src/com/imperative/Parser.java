package com.imperative;

import java.util.ArrayList;
import java.util.List;

import static com.imperative.TokenType.*;

class Parser {
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private Stmt declaration() {
        try {
            if (match(VAR)) return varDeclaration();
            if (match(RECORD)) return recordDeclaration();
            if (match(ROUTINE)) return routineDeclaration("routine");
            if (match(ARRAY)) return arrayDeclaration();

            return statement();
        } catch (ParserError error) {
            synchronize();
            return null;
        }
    }

    private Stmt arrayDeclaration() {
        Token name = consume("Expected array name", IDENTIFIER);
        consume("Expect '[' after array name.", LEFT_SQUARE_BRACE);
        List<Expr> members = new ArrayList<>();
        if (!check(RIGHT_SQUARE_BRACE)) {
            do {
                members.add(expression());
            } while(match(COMMA));
        }

        consume("Expected ']' after parameters.", RIGHT_SQUARE_BRACE);
        consume("Expected ';' after array declaration", SEMICOLON);
        return new Stmt.Array(name, members);
    }

    private Stmt.Routine routineDeclaration(String kind) {
        Token name = consume("Expect " + kind + " name.", IDENTIFIER);
        consume("Expect '(' after " + kind + " name.", LEFT_PAREN);
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 69) {
                    error(peek(), "Cannot have more than 69 parameters.");
                }

                parameters.add(consume("Expected parameter name.", IDENTIFIER));
            } while (match(COMMA));
        }
        consume("Expected ')' after parameters.", RIGHT_PAREN);

        Type returnType = null;
        if (match(COLON)) {
            returnType = getType();
        }

        consume("Expected 'is' before " + kind + " body.", IS);
        List<Stmt> body = block();
        return new Stmt.Routine(name, parameters, body, returnType);
    }

    private Stmt recordDeclaration() {
        Token name = consume("Expected record name.", IDENTIFIER);
        consume("Expected '{' before record body.", LEFT_BRACE);

        List<Stmt.Var> fields = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            consume("Expected 'var' keyword", VAR);
            fields.add((Stmt.Var) varDeclaration());
        }

        consume("Expected '}' after record body.", RIGHT_BRACE);
        consume("Expected 'end' after '}'.", END);
        return new Stmt.Record(name, fields);
    }

    private Stmt varDeclaration() {
        Token name = consume("Expected identifier.", IDENTIFIER);

        Type type = null;
        if (match(COLON)) {
            type = getType();
        }

        Expr initializer = null;
        if (match(IS)) {
            initializer = expression();
        }

        if (type == null && initializer == null) {
            throw error(peek(), "Type or initializer not specified for variable.");
        }

        consume("Expected ';' after variable declaration.", SEMICOLON);
        return new Stmt.Var(name, initializer, type);
    }

    private Type getType() {
        if (match(INTEGER)) {
            return new Type.PrimitiveType(Primitive.INTEGER);
        } else if (match(REAL)) {
            return new Type.PrimitiveType(Primitive.REAL);
        } else if (match(BOOLEAN)) {
            return new Type.PrimitiveType(Primitive.BOOLEAN);
        }
        throw error(peek(), "Cannot find Type in scope.");
    }

    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {

        Expr expr = or();
        if (match(WALRUS)) {
            Token assign = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            }

            error(assign, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr or() {
        Expr expr = and();

        while (match(OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr and() {
        Expr expr = xor();

        while (match(AND)) {
            Token operator = previous();
            Expr right = xor();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr xor() {
        Expr expr = equality();

        while (match(XOR)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Stmt statement() {

        if (match(FOR)) return forStatement();
        if (match(IF)) return ifStatement();
        if (match(RETURN)) return returnStatement();
        if (match(WHILE)) return whileStatement();
        if (match(PRINT)) return printStatement();
        if (match(LOOP)) return new Stmt.Body(block());
        return expressionStatement();
    }

    private Stmt returnStatement() {
        Token keyword = previous();
        Expr value = null;
        if (!check(SEMICOLON)) {
            value = expression();
        }

        consume("Expected ';' after return value.", SEMICOLON);
        return new Stmt.Return(keyword, value);
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume("Expected ';' after value.", SEMICOLON);
        return new Stmt.Print(value);
    }

    private Stmt forStatement() {
        Token name = consume("Expected identifier after 'for'.", IDENTIFIER);

        consume("Expected 'in' after identifier.", IN);

        boolean reverse = false;
        if (match(REVERSE)) {
            reverse = true;
        }
        Stmt.Range range = rangeDeclaration();
        Stmt body = statement();

        return new Stmt.For(name, reverse, range, body);
    }

    private Stmt.Range rangeDeclaration() {
        Expr left = expression();
        consume("Expected .. after initial value of range.", DOT_DOT);
        Expr right = expression();
        return new Stmt.Range(left, right);
    }

    private Stmt whileStatement() {
        Expr condition = expression();

        Stmt body = statement();

        return new Stmt.While(condition, body);

    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while (!check(END) && !isAtEnd()) statements.add(declaration());
        consume("Expected 'end' after body.", END);
        return statements;
    }

    private Stmt ifStatement() {
        Expr condition = expression();
        consume("Expected 'then' after if condition. ", THEN);

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) {
            elseBranch = statement();
        }

        consume("Expected 'end' after body.", END);
        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume("Expect ';' after expression.", SEMICOLON);
        return new Stmt.Expression(expr);
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(SLASH_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = addition();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = addition();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr addition() {
        Expr expr = multiplication();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = multiplication();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr multiplication() {
        Expr expr = unary();

        while (match(SLASH, STAR, PERCENT)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(NOT, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return call();
    }

    private Expr call() {
        Expr expr = primary();

        while (true) {
            if (match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if(match(DOT)) {
                Token name = consume("Expected property name after '.'.", IDENTIFIER);
                expr = new Expr.Get(expr, name);
            } else if (match(LEFT_SQUARE_BRACE)) {
               Token index = consume("Expected integer index", TYPE_INTEGER);
               consume("Expected enclosing ']' after index", RIGHT_SQUARE_BRACE);
               expr = new Expr.GetIndex(expr, Integer.parseInt(index.lexeme));
            }
            else {
                break;
            }
        }

        return expr;
    }

    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 69) {
                    error(peek(), "Cannot have more than 69 arguments.");
                }
                arguments.add(expression());
            } while (match(COMMA));
        }

        Token paren = consume("Expected ')' after arguments.", RIGHT_PAREN);
        return new Expr.Call(callee, paren, arguments);
    }

    private Expr primary() {
        if (match(FALSE))
            return new Expr.Literal(false);

        if (match(TRUE))
            return new Expr.Literal(true);
        if (match(TYPE_INTEGER, TYPE_REAL)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume("Expected ')' after expression.", RIGHT_PAREN);
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expected expression.");
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(String message, TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                return advance();
            }
        }
        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd())
            return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd())
            current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParserError error(Token token, String message) {
        Main.error(token, message);
        return new ParserError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            switch (peek().type) {
                case ARRAY:
                case RECORD:
                case ROUTINE:
                case VAR:
                case FOR:
                case IF:
                case TYPE:
                case WHILE:
                    return;
                default:
            }

            advance();
        }
    }

    private static class ParserError extends RuntimeException {
    }

}
