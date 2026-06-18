package automata.core.operations;

import automata.core.Automaton;
import automata.model.State;

import java.util.Map;

/**
 * Операция звезда на Клини (Kleene Star): L(a)*.
 *
 * <p>Позволява нула или повече повторения на езика. Създава ново начално
 * и ново финално състояние, добавя епсилон-преход за празната дума и
 * епсилон-цикъл за повторението.</p>
 */
public class KleeneOperation {

    /**
     * Построява автомат за звездата на Клини на езика на a.
     *
     * @param a     автоматът, върху който се прилага операцията
     * @param newId идентификатор на новия автомат
     * @return нов автомат за езика L(a)*
     */
    public Automaton apply(Automaton a, String newId) {
        Automaton result = new Automaton(newId);

        State newStart = new State(AutomatonOperationHelper.generateStateName(), false);
        State newAccept = new State(AutomatonOperationHelper.generateStateName(), true);
        result.addState(newStart);
        result.addState(newAccept);
        result.setStartState(newStart);

        Map<State, State> map = AutomatonOperationHelper.copyAutomatonData(a, result);
        State copiedStart = map.get(a.getStartState());

        result.addTransition(newStart, 'E', copiedStart);
        result.addTransition(newStart, 'E', newAccept);

        for (State copy : result.getStates()) {
            if (copy.isAccepting() && !copy.equals(newAccept)) {
                result.addTransition(copy, 'E', copiedStart);
                result.addTransition(copy, 'E', newAccept);
                copy.setAccepting(false);
            }
        }
        return result;
    }
}