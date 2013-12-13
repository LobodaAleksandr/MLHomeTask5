package ru.ifmo.rain.loboda;

import java.util.HashMap;
import java.util.Set;

public abstract class BinaryOperation extends Expression {
    private Expression left, right;
    private String stringCache;

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public BinaryOperation(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    public String toString() {
        if (stringCache != null) {
            return stringCache;
        }
        String leftS;
        String rightS;
        leftS = "(" + left.toString() + ")";
        rightS = "(" + right.toString() + ")";
        stringCache = leftS + getSign() + rightS;
        return stringCache;
    }

    @Override
    public boolean replaceFree(Variable from, Variable to) {
        return left.replaceFree(from, to) | right.replaceFree(from, to);
    }

    @Override
    public boolean equals(Object object) {
        if (this.getClass() != object.getClass()) {
            return false;
        } else {
            return left.equals(((BinaryOperation) object).getLeft()) && right.equals(((BinaryOperation) object).getRight());
        }
    }

    @Override
    protected boolean isomorphic(Expression expression, HashMap<Predicate, Expression> vars) {
        return left.isIsomorphic(((BinaryOperation) expression).getLeft(), vars) && right.isIsomorphic(((BinaryOperation) expression).getRight(), vars);
    }

    protected abstract String getSign();


    @Override
    protected boolean freeToSubstitute(Variable from, Variable[] to, Set<Variable> blocked){
        return left.freeToSubstitute(from, to, blocked) && right.freeToSubstitute(from, to, blocked);
    }

    @Override
    protected void getFreeVariables(Set<Variable> variables, Set<Variable> blocked){
        left.getFreeVariables(variables, blocked);
        right.getFreeVariables(variables, blocked);
    }

}
