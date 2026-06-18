package automata.core.operations;

import automata.core.Automaton;
import automata.model.State;
import automata.model.Transition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Операция детерминизация (НКА → ДКА).
 *
 * <p>Преобразува недетерминиран краен автомат в еквивалентен детерминиран
 * чрез алгоритъма на подмножествата (Subset Construction). Всяко състояние
 * на новия ДКА представя множество от състояния на оригиналния НКА.</p>
 */
public class DeterminizeOperation {

    /**
     * Построява ДКА, еквивалентен на подадения НКА.
     *
     * @param nfa   недетерминираният автомат
     * @param newId идентификатор на новия (детерминиран) автомат
     * @return нов детерминиран автомат, разпознаващ същия език
     */
    public Automaton apply(Automaton nfa, String newId) {
        Automaton dfa = new Automaton(newId);

        Set<Character> alphabet = new HashSet<>();
        for (Transition t : nfa.getTransitions()) {
            if (t.getSymbol() != 'E') {
                alphabet.add(t.getSymbol());
            }
        }

        Set<State> startSet = new HashSet<>();
        startSet.add(nfa.getStartState());
        startSet = nfa.getEpsilonClosure(startSet);

        Map<Set<State>, State> dfaStates = new LinkedHashMap<>();
        List<Set<State>> worklist = new ArrayList<>(); // Списък, който ползваме като опашка

        boolean startAccepting = AutomatonOperationHelper.containsAccepting(startSet);
        State dfaStart = new State(AutomatonOperationHelper.setName(startSet), startAccepting);
        dfaStates.put(startSet, dfaStart);
        dfa.addState(dfaStart);
        dfa.setStartState(dfaStart);
        worklist.add(startSet);

        while (!worklist.isEmpty()) {
            Set<State> current = worklist.remove(0); // Вадим първия елемент (началото на опашката)
            State dfaCurrent = dfaStates.get(current);

            for (char symbol : alphabet) {
                Set<State> nextSet = new HashSet<>();
                for (State s : current) {
                    for (Transition t : nfa.getTransitions()) {
                        if (t.getFrom().equals(s) && t.getSymbol() == symbol) {
                            nextSet.add(t.getTo());
                        }
                    }
                }
                if (nextSet.isEmpty()) {
                    continue;
                }

                nextSet = nfa.getEpsilonClosure(nextSet);

                if (!dfaStates.containsKey(nextSet)) {
                    boolean acc = AutomatonOperationHelper.containsAccepting(nextSet);
                    State newDfaState = new State(AutomatonOperationHelper.setName(nextSet), acc);
                    dfaStates.put(nextSet, newDfaState);
                    dfa.addState(newDfaState);
                    worklist.add(nextSet);
                }
                dfa.addTransition(dfaCurrent, symbol, dfaStates.get(nextSet));
            }
        }
        return dfa;
    }
}