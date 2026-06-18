package automata.core.operations;

import automata.core.Automaton;
import automata.model.State;

import java.util.Map;

/**
 * Операция конкатенация (Concatenation) на два автомата: L(a1) · L(a2).
 *
 * <p>Свързва финалните състояния на първия автомат с началното състояние
 * на втория чрез епсилон-преходи. Началото на резултата е началото на
 * първия автомат, а финалните състояния — финалните на втория.</p>
 */
public class ConcatOperation {

    /**
     * Построява автомат, разпознаващ конкатенацията на езиците на a1 и a2.
     *
     * @param a1    първият автомат (префикс)
     * @param a2    вторият автомат (суфикс)
     * @param newId идентификатор на новия автомат
     * @return нов автомат за езика L(a1) · L(a2)
     */
    public Automaton apply(Automaton a1, Automaton a2, String newId) {
        Automaton result = new Automaton(newId);

        Map<State, State> map1 = AutomatonOperationHelper.copyAutomatonData(a1, result);
        Map<State, State> map2 = AutomatonOperationHelper.copyAutomatonData(a2, result);

        result.setStartState(map1.get(a1.getStartState()));

        for (State original : a1.getStates()) {
            State copy = map1.get(original);
            if (copy.isAccepting()) {
                result.addTransition(copy, 'E', map2.get(a2.getStartState()));
                copy.setAccepting(false);
            }
        }
        return result;
    }
}