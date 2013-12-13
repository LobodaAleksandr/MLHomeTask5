package ru.ifmo.rain.loboda;

public class Implication extends BinaryOperation {


    public Implication(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    protected String getSign() {
        return "->";
    }
}
