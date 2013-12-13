package ru.ifmo.rain.loboda;

public class OperationOr extends BinaryOperation {
    public OperationOr(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    protected String getSign() {
        return "|";
    }
}
