package ru.ifmo.rain.loboda;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Checker {
    private static List<Expression> axioms;
    private static List<Expression> arithAxioms;
    private static boolean isInit;
    private Set<Expression> hypothesis;
    private Map<Expression, List<Expression>> rightParts;
    private Set<Expression> proved;
    private String error;
    private Set<Variable> blocked;
    private Expression lastPonens;
    private Expression alpha;

    public Expression getLastPonens(){
        return lastPonens;
    }

    public String getLastError(){
        return error;
    }

    public Checker(List<Expression> hypothesis) throws IOException {
        if (!isInit) {
            init();
        }
        alpha = null;
        if(hypothesis.size() != 0){
            blocked = hypothesis.get(hypothesis.size() - 1).getFreeVariables();
            alpha = hypothesis.get(hypothesis.size() - 1);
        }
        rightParts = new HashMap<Expression, List<Expression>>();
        proved = new HashSet<Expression>();
        this.hypothesis = new HashSet<Expression>(hypothesis);
    }

    private void init() throws IOException {
        isInit = true;
        InputStream stream = Checker.class.getResourceAsStream("Resources/ClassicalAxioms");
        Parser parser = new Parser(stream);
        axioms = new ArrayList<Expression>();
        while(parser.hasNext()){
            axioms.add(parser.next());
        }
        stream.close();
        stream = Checker.class.getResourceAsStream("Resources/ArithAxioms");
        parser = new Parser(stream);
        arithAxioms = new ArrayList<Expression>();
        while(parser.hasNext()){
            arithAxioms.add(parser.next());
        }
        stream.close();
    }

    public Type check(Expression expression) {
        Type result = Type.ERROR;
        if(alpha != null && expression.equals(alpha)){
            result = Type.ALPHA;
        }
        if (result == Type.ERROR && hypothesis.contains(expression)) {
            result = Type.HYPOTHESIS;
        }
        for (Expression axiom : axioms) {
            if(result != Type.ERROR){
                break;
            }
            if (expression.isIsomorphic(axiom)) {
                result = Type.CLASSICAL_AXIOM;
            }
        }
        // Check 11, 12 axioms
        if(result == Type.ERROR && isPredicateAxiom(expression)){
            result = Type.PREDICATE_AXIOM;
        }
        //Arithmetic axioms
        if(result == Type.ERROR){
            for(Expression axiom: arithAxioms){
                if(expression.equals(axiom)){
                    result = Type.ARITH_AXIOM;
                    break;
                }
            }
        }

        // Induction scheme
        if(result == Type.ERROR){
            if(isInductionScheme(expression)){
                result = Type.ARITH_AXIOM;
            }
        }
        // MP1
        if(result == Type.ERROR && rightParts.get(expression) != null){
            for(Expression left: rightParts.get(expression)){
                if(proved.contains(left)){
                    result = Type.MODUS_PONENS_1;
                    lastPonens = left;
                    break;
                }
            }
        }
        // MP2
        if(result == Type.ERROR && expression.getClass() == Implication.class && ((Implication)expression).getRight().getClass() == Universal.class){
            Expression phi = ((Implication)expression).getLeft();
            Expression ksi = ((Universal)((Implication)expression).getRight()).getExpression();
            Variable variable = ((Universal)((Implication)expression).getRight()).getVariable();
            if(proved.contains(new Implication(phi, ksi))){
                if(!phi.replaceFree(variable, variable)){
                    result = Type.MODUS_PONENS_2;
                } else {
                    error = "переменная " + variable + " входит свободно в формулу " + phi + ".";
                }
            }
        }
        // MP3
        if(result == Type.ERROR && expression.getClass() == Implication.class && ((Implication)expression).getLeft().getClass() == Existance.class){
            Expression phi = ((Implication)expression).getRight();
            Expression ksi = ((Existance)((Implication)expression).getLeft()).getExpression();
            Variable variable = ((Existance)((Implication)expression).getLeft()).getVariable();
            if(proved.contains(new Implication(ksi, phi))){
                if(!phi.replaceFree(variable, variable)){
                    result = Type.MODUS_PONENS_3;
                } else {
                    error = "переменная " + variable + " входит свободно в формулу " + phi + ".";
                }
            }
        }

        if(expression.getClass() == Implication.class){
            Expression right = ((Implication)expression).getRight();
            Expression left = ((Implication)expression).getLeft();
            if(rightParts.get(right) == null){
                rightParts.put(right, new LinkedList<Expression>());
            }
            if(!rightParts.get(right).contains(left)){
                rightParts.get(right).add(left);
            }
        }
        proved.add(expression);
        return result;
    }

    private boolean isInductionScheme(Expression expression){
        if(expression.getClass() == Implication.class){
            Implication e = (Implication)expression;
            Expression psi = e.getRight();
            Expression left = e.getLeft();
            if(left.getClass() == OperationAnd.class){
                OperationAnd and = (OperationAnd)left;
                if(and.getRight().getClass() == Universal.class){
                    Universal universal = (Universal)and.getRight();
                    Variable variable = universal.getVariable();
                    Term replacer = psi.isReplaceResult(and.getLeft(), variable);
                    if(replacer != null && replacer.equals(Term.zero)){
                        Expression under = universal.getExpression();
                        if(under.getClass() == Implication.class){
                            e = (Implication)under;
                            replacer = psi.isReplaceResult(e.getRight(), variable);
                            return e.getLeft().equals(psi) && replacer != null && replacer.equals(new Term("inc", new Term[]{variable}));
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isPredicateAxiom(Expression expression){
        if(expression.getClass() != Implication.class){
            return false;
        }
        Implication impl = (Implication)expression;
        Expression before = null;
        Variable variable = null;
        Expression after = null;
        if(impl.getLeft().getClass() == Universal.class){
            variable = ((Universal)impl.getLeft()).getVariable();
            before = ((Universal) impl.getLeft()).getExpression();
            after = impl.getRight();
        }
        if(impl.getRight().getClass() == Existance.class){
            variable = ((Existance)impl.getRight()).getVariable();
            before = ((Existance) impl.getRight()).getExpression();
            after = impl.getLeft();
        }
        if(variable == null){
            return false;
        }
        Term replacer = before.isReplaceResult(after, variable);
        if(replacer == null){
            return false;
        }
        if(before.freeToSubstitute(variable, replacer)){
            return true;
        } else {
            error = "терм " + replacer + " не свободен для подстановки в формулу " + before + " вместо переменной " + variable + ".";
            return false;
        }
    }

    public enum Type {
        HYPOTHESIS, ERROR, CLASSICAL_AXIOM, PREDICATE_AXIOM, MODUS_PONENS_1, MODUS_PONENS_2, MODUS_PONENS_3, ALPHA, ARITH_AXIOM
    }
}
