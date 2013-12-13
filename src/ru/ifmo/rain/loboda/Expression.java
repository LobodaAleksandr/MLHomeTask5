package ru.ifmo.rain.loboda;

import java.util.*;

public abstract class Expression {
    private int hashCache;
    private boolean isHashComputed;
    protected static Term equalsWorkAround = null;

    public abstract String toString();

    public boolean isIsomorphic(Expression expression) {
        return isIsomorphic(expression, new HashMap<Predicate, Expression>());
    }

    public Term isReplaceResult(Expression expression, Variable variable){
        replaceFree(variable, new Variable(Variable.impossibleVariable));
        boolean isReplace = equals(expression);
        Term result = null;
        if(isReplace){
            result = equalsWorkAround;
        }
        equalsWorkAround = null;
        replaceFree(new Variable(Variable.impossibleVariable), variable);
        return result;
    }

    protected boolean isIsomorphic(Expression expression, HashMap<Predicate, Expression> vars) {
        if (expression.getClass() == Predicate.class) {
            if (!vars.containsKey(expression)) {
                vars.put((Predicate) expression, this);
                return true;
            } else {
                return equals(vars.get(expression));
            }
        }
        if (this.getClass() != expression.getClass()) {
            return false;
        }
        return isomorphic(expression, vars);
    }

    protected abstract boolean isomorphic(Expression expression, HashMap<Predicate, Expression> vars);

    @Override
    public int hashCode() {
        if (isHashComputed) {
            return hashCache;
        }
        hashCache = toString().hashCode();
        isHashComputed = true;
        return hashCache;
    }

    public Set<Variable> getFreeVariables(){
        Set<Variable> variables = new HashSet<Variable>();
        getFreeVariables(variables, new HashSet<Variable>());
        return variables;
    }

    protected abstract void getFreeVariables(Set<Variable> variables, Set<Variable> blocked);

    public abstract boolean equals(Object object);

    public abstract boolean replaceFree(Variable from, Variable to);

    public boolean freeToSubstitute(Variable from, Term to){
        Set<Variable> variables = new HashSet<Variable>();
        to.getVariables(variables);
        Variable[] vars = variables.toArray(new Variable[0]);
        return freeToSubstitute(from, vars, new HashSet<Variable>());
    }

    protected abstract boolean freeToSubstitute(Variable from, Variable[] to, Set<Variable> blocked);
}

