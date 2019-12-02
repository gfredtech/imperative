package com.imperative;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.imperative.TokenType.*;

class Lexer {
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("array", ARRAY);
        keywords.put("boolean", BOOLEAN);
        keywords.put("else", ELSE);
        keywords.put("end", END);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("if", IF);
        keywords.put("in", IN);
        keywords.put("integer", INTEGER);
        keywords.put("is", IS);
        keywords.put("loop", LOOP);
        keywords.put("not", NOT);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("real", REAL);
        keywords.put("record", RECORD);
        keywords.put("return", RETURN);
        keywords.put("reverse", REVERSE);
        keywords.put("routine", ROUTINE);
        keywords.put("then", THEN);
        keywords.put("true", TRUE);
        keywords.put("type", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
        keywords.put("xor", XOR);
    }

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Lexer(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case '*':
                addToken(STAR);
                break;
            case '%':
                addToken(PERCENT);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '.':
                addToken(match('.') ? DOT_DOT : DOT);
                break;
            case ':':
                addToken(match('=') ? WALRUS : COLON);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                addToken(match('=') ? SLASH_EQUAL : SLASH);
                break;

            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;

            case '\n':
                // count line numbers.
                line++;
                break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Main.error(line, "Unexpected character.");
                }
                break;

        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek()))
            advance();

        String text = source.substring(start, current);
        TokenType type = keywords.getOrDefault(text, IDENTIFIER);
        addToken(type);
    }

    private void number() {
        while (isDigit(peek()))
            advance();

        if (peek() == '.' && isDigit(peekNext())) {
            // consume the "."
            advance();

            while (isDigit(peek()))
                advance();
            addToken(TYPE_REAL, Double.parseDouble(source.substring(start, current)));
            return;
        }

        addToken(TYPE_INTEGER, Integer.parseInt(source.substring(start, current)));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean match(char expected) {
        if (isAtEnd())
            return false;
        if (source.charAt(current) != expected)
            return false;

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}
