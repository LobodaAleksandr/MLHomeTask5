package ru.ifmo.rain.loboda;

import java.util.HashMap;
import java.util.Set;

public class Universal extends Expression {
    private Expression expression;
    private Variable variable;

    public Universal(Variable variable, Expression expression) {
        this.expression = expression;
        this.variable = variable;
    }

    @Override
    public String toString() {
        return "@" + variable.toString() + "(" + expression.toString() + ")";
    }

    public Variable getVariable() {
        return variable;
    }

    @Override
    protected boolean isomorphic(Expression expression, HashMap<Predicate, Expression> vars) {
        return this.expression.isIsomorphic(((Universal) expression).getExpression(), vars);
    }


    public Expression getExpression() {
        return expression;
    }

    @Override
    public boolean replaceFree(Variable from, Variable to) {
        if (variable.equals(from)) {
            return false;
        } else {
            return expression.replaceFree(from, to);
        }
    }

    @Override
    protected boolean freeToSubstitute(Variable from, Variable[] to, Set<Variable> blocked){
        boolean blockedHere = true;
        if(blocked.contains(variable)){
            blockedHere = false;
        } else {
            blocked.add(variable);
        }
        boolean result = expression.freeToSubstitute(from, to, blocked);
        if(blockedHere){
            blocked.remove(variable);
        }
        return result;
    }

    @Override
    protected void getFreeVariables(Set<Variable> variables, Set<Variable> blocked) {
        boolean blockedHere = true;
        if(blocked.contains(variable)){
            blockedHere = false;
        } else {
            blocked.add(variable);
        }
        expression.getFreeVariables(variables, blocked);
        if(blockedHere){
            blocked.remove(variable);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object.getClass() != Universal.class) {
            return false;
        }
        Universal universal = (Universal) object;
        if (!universal.getVariable().equals(variable)) {
            return false;
        }
        return expression.equals(universal.getExpression());
    }
}
