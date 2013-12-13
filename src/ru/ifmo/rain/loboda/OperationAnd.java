package ru.ifmo.rain.loboda;


public class OperationAnd extends BinaryOperation {
    public OperationAnd(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    protected String getSign() {
        return "&";
    }
}
