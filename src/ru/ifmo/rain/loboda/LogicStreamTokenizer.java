package ru.ifmo.rain.loboda;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class LogicStreamTokenizer {
    private String name;
    private PushbackInputStream stream;

    public LogicStreamTokenizer(InputStream stream) {
        this.stream = new PushbackInputStream(new BufferedInputStream(stream));
    }

    String get_name() {
        return name;
    }

    public Token nextToken() throws IOException {
        int ch = stream.read();
        while (ch == ' ') {
            ch = stream.read();
        }
        if (ch < 0) {
            return Token.END;
        }
        switch (ch) {
            case '?':
                return Token.EXISTENCE;
            case '@':
                return Token.UNIVERSAL;
            case '&':
                return Token.AND;
            case '!':
                return Token.NOT;
            case '\n':
                return Token.PRINT;
            case '=':
                return Token.EQUALS;
            case '*':
                return Token.MUL;
            case '+':
                return Token.PLUS;
            case '0':
                return Token.ZERO;
            case ',':
                return Token.COMMA;
            case '\'':
                return Token.INC;
            case '|':
                ch = stream.read();
                if(ch == '-'){
                    return Token.PROVABLY;
                } else {
                    stream.unread(ch);
                }
                return Token.OR;
            case '-':
                stream.read();
                return Token.IMPLICATION;
            case '(':
                return Token.LP;
            case ')':
                return Token.RP;
            case '[':
                return Token.LS;
            case ']':
                return Token.RS;
            default:
                Token token;
                if (ch >= 'a' && ch <= 'z') {
                    token = Token.TERM;
                } else {
                    token = Token.PREDICATE;
                }
                int first = ch;
                ch = stream.read();
                name = new String(new char[]{(char)first});
                while (ch >= '0' && ch <= '9') {
                    name += (char)ch;
                    ch = stream.read();
                }
                if (ch != -1) {
                    stream.unread(ch);
                }
                return token;
        }
    }
}