package automata.core.operations;

import automata.core.Automaton;
import automata.model.State;

import java.util.Map;

/**
 * Операция обединение (Union) на два автомата: L(a1) ∪ L(a2).
 *
 * <p>Реализира конструкцията на Томпсън — създава ново общо начално
 * и ново общо финално състояние, свързва ги с копията на оригиналните
 * автомати чрез епсилон-преходи. Оригиналните автомати не се променят.</p>
 */
public class UnionOperation {

    /**
     * Построява автомат, разпознаващ обединението на езиците на a1 и a2.
     *
     * @param a1    първият автомат
     * @param a2    вторият автомат
     * @param newId идентификатор на новия автомат
     * @return нов автомат за езика L(a1) ∪ L(a2)
     */
    public Automaton apply(Automaton a1, Automaton a2, String newId) {
        Automaton result = new Automaton(newId);

        State newStart = new State(AutomatonOperationHelper.generateStateName(), false);
        State newAccept = new State(AutomatonOperationHelper.generateStateName(), true);
        result.addState(newStart);
        result.addState(newAccept);
        result.setStartState(newStart);

        Map<State, State> map1 = AutomatonOperationHelper.copyAutomatonData(a1, result);
        Map<State, State> map2 = AutomatonOperationHelper.copyAutomatonData(a2, result);

        result.addTransition(newStart, 'E', map1.get(a1.getStartState()));
        result.addTransition(newStart, 'E', map2.get(a2.getStartState()));

        for (State copy : result.getStates()) {
            if (copy.isAccepting() && !copy.equals(newAccept)) {
                result.addTransition(copy, 'E', newAccept);
                copy.setAccepting(false);
            }
        }
        return result;
    }
}