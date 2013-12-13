package ru.ifmo.rain.loboda;

import java.util.Set;

public class Term {
    private Term[] terms;
    protected String name;
    public static Term zero = new Term("zero", new Term[0]);

    public Term(String name, Term[] terms) {
        this.terms = terms;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Term[] getTerms() {
        return terms;
    }

    @Override
    public String toString() {
        if(name.equals("zero")){
            return "0";
        }
        if(name.equals("inc")){
            return "(" + terms[0].toString() + ")" + "'";
        }
        if(name.equals("plus")){
            return terms[0].toString() + "+" + terms[1].toString();
        }
        if(name.equals("mul")){
            return terms[0].toString() + "*" + terms[1].toString();
        }
        String rep = name;
        if (terms != null) {
            rep += "(";
            for (int i = 0; i < terms.length; i++) {
                rep += terms[i].toString();
                if (i != terms.length - 1) {
                    rep += ", ";
                }
            }
            rep += ")";
        }
        return rep;
    }

    public boolean replaceFree(Variable from, Variable to) {
        boolean result = false;
        for (Term term : terms) {
            if (term.replaceFree(from, to)) {
                result = true;
            }
        }
        return result;
    }

    protected boolean freeToSubstitute(Variable from, Variable to, boolean blocked){
        if(terms == null){
            return true;
        }
        for(Term term: terms){
            if(!term.freeToSubstitute(from, to, blocked)){
                return false;
            }
        }
        return true;
    }

    protected void getVariables(Set<Variable> variables){
        for(Term term: terms){
            term.getVariables(variables);
        }
    }

    protected void getFreeVariables(Set<Variable> variables, Set<Variable> blocked) {
        for(Term term: terms){
            term.getFreeVariables(variables, blocked);
        }
    }

    protected boolean freeToSubstitute(Variable from, Variable[] to, Set<Variable> blocked) {
        if(terms == null){
            return true;
        }
        for(Term term: terms){
            if(!term.freeToSubstitute(from, to, blocked)){
                return false;
            }
        }
        return true;
    }

    public int hashCode(){
        return toString().hashCode();
    }

    public boolean equals(Object object) {
        if (object.getClass() != Term.class) {
            return false;
        }
        Term term = (Term) object;
        if (!term.getName().equals(name)) {
            return false;
        }
        if (terms == null && term.getTerms() == null) {
            return true;
        }
        if (term.getTerms().length != terms.length) {
            return false;
        }
        Term[] termsToComp = term.getTerms();
        for (int i = 0; i < terms.length; i++) {
            if (!terms[i].equals(termsToComp[i])) {
                return false;
            }
        }
        return true;
    }
}
