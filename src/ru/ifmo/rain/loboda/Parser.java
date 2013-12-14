package ru.ifmo.rain.loboda;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Parser {
    private Token token;
    private LogicStreamTokenizer tokenizer;
    Expression nextExpression;
    List<Expression> hypothesis;
    private void skip() {
        token = null;
    }

    Parser(InputStream inputStream) throws IOException {
        this.tokenizer = new LogicStreamTokenizer(new MyInputStream(inputStream));
        nextExpression = get();
        if(token == Token.COMMA || token == Token.PROVABLY){
            hypothesis = new ArrayList<Expression>();
            hypothesis.add(nextExpression);
            while(token == Token.COMMA){
                hypothesis.add(get());
            }
            nextExpression = get();
        }
    }

    public List<Expression> getHypothesis(){
        return hypothesis;
    }

    public boolean hasNext() {
        return nextExpression != null;
    }

    private Expression get() throws IOException {
        token = Token.PRINT;
        while(token == Token.PRINT){
            token = tokenizer.nextToken();
        }
        if(token == Token.END){
            return null;
        }
        return expression();
    }

    public Expression next() throws IOException {
        Expression expression = nextExpression;
        nextExpression = get();
        return expression;
    }

    private Term multiplier() throws IOException {
        if (token == null) {
            token = tokenizer.nextToken();
        }
        Term result;
        switch (token){
            case ZERO:
                result = new Term("zero", new Term[0]);
                token = tokenizer.nextToken();
                break;
            case LP:
            case LS:
                skip();
                result = term();
                token = tokenizer.nextToken();
                break;
            default:
                String name = tokenizer.get_name();
                token = tokenizer.nextToken();
                if (token == Token.LP) {
                    List<Term> terms = new LinkedList<Term>();
                    token = Token.COMMA;
                    while (token == Token.COMMA) {
                        skip();
                        terms.add(term());
                    }
                    token = tokenizer.nextToken();
                    result = new Term(name, terms.toArray(new Term[0]));
                } else {
                    result = new Variable(name);
                }
                break;
        }
        while(token == Token.INC){
            result = new Term("inc", new Term[]{result});
            token = tokenizer.nextToken();
        }
        return result;
    }

    private Term summand() throws IOException {
        if (token == null) {
            token = tokenizer.nextToken();
        }
        Term left = multiplier();
        while(token == Token.MUL){
            skip();
            left = new Term("mul", new Term[]{left, multiplier()});
        }
        return left;
    }

    private Term term() throws IOException {
        if (token == null) {
            token = tokenizer.nextToken();
        }
        Term left = summand();
        while(token == Token.PLUS){
            skip();
            left = new Term("plus", new Term[]{left, summand()});
        }
        return left;
    }

    private Expression predicate() throws IOException {
        if (token == null) {
            token = tokenizer.nextToken();
        }
        if(token == Token.PREDICATE){
            String name = tokenizer.get_name();
            token = tokenizer.nextToken();
            if (token == Token.LP) {
                List<Term> terms = new LinkedList<Term>();
                token = Token.COMMA;
                while (token == Token.COMMA) {
                    skip();
                    terms.add(term());
                }
                token = tokenizer.nextToken();
                return new Predicate(name, terms.toArray(new Term[0]));
            } else {
                return new Predicate(name, null);
            }
        } else {
            Term term1 = term();
            skip();
            Term term2 = term();
            return new Predicate("EQUALS", new Term[]{term1, term2});
        }
    }

    private Expression unary() throws IOException {
        if (token == null) {
            token = tokenizer.nextToken();
        }
        switch (token) {
            case NOT:
                skip();
                return new OperationNot(unary());
            case UNIVERSAL:
                skip();
                tokenizer.nextToken();
                Variable var = new Variable(tokenizer.get_name());
                skip();
                return new Universal(var, unary());
            case EXISTENCE:
                skip();
                tokenizer.nextToken();
                var = new Variable(tokenizer.get_name());
                skip();
                return new Existance(var, unary());
            case LP:
                skip();
                Expression expression = expression();
                token = tokenizer.nextToken();
                return expression;
            default:
                return predicate();
        }
    }

    private Expression conjunction() throws IOException {
        Expression left = unary();
        while (token == Token.AND) {
            skip();
            left = new OperationAnd(left, unary());
        }
        return left;
    }

    private Expression disjunction() throws IOException {
        Expression left = conjunction();
        while (token == Token.OR) {
            skip();
            left = new OperationOr(left, conjunction());
        }
        return left;
    }

    private Expression expression() throws IOException {
        Expression left = disjunction();
        if (token == Token.IMPLICATION) {
            skip();
            return new Implication(left, expression());
        }
        return left;
    }
}
