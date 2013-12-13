package ru.ifmo.rain.loboda;


import java.util.HashMap;
import java.util.Set;

public class OperationNot extends Expression {
    private Expression expression;

    public OperationNot(Expression expression) {
        this.expression = expression;
    }

    Expression getExpression() {
        return expression;
    }

    public boolean equals(Object obj) {
        return obj.getClass() == OperationNot.class && expression.equals(((OperationNot) obj).getExpression());
    }

    public String toString() {
        return "!(" + expression.toString() + ")";
    }

    @Override
    protected boolean isomorphic(Expression expression, HashMap<Predicate, Expression> vars) {
        return this.expression.isIsomorphic(((Existance) expression).getExpression(), vars);
    }

    @Override
    protected void getFreeVariables(Set<Variable> variables, Set<Variable> blocked) {
        expression.getFreeVariables(variables, blocked);
    }

    @Override
    public boolean replaceFree(Variable from, Variable to) {
        return expression.replaceFree(from, to);
    }

    @Override
    protected boolean freeToSubstitute(Variable from, Variable[] to, Set<Variable> blocked) {
        return expression.freeToSubstitute(from, to, blocked);
    }
}
