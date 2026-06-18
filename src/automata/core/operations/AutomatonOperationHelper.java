package automata.core.operations;

import automata.core.Automaton;
import automata.model.State;
import automata.model.Transition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Споделени помощни методи за всички класове-операции върху автомати.
 *
 * <p>Този клас централизира логиката, която всички операции
 * (обединение, конкатенация, звезда на Клини, детерминизация) използват:
 * генериране на уникални имена, дълбоко копиране на автомати и
 * помощни функции за множества от състояния.</p>
 *
 * <p>Методите са package-private (видими само в пакета operations),
 * за да не се излагат вътрешни детайли извън него и същевременно да
 * не се дублира кодът в отделните класове-операции.</p>
 */
final class AutomatonOperationHelper {

    /** Брояч за генериране на уникални имена на нови състояния (q0, q1, q2...). */
    private static int stateCounter = 0;

    /** Помощният клас не се инстанцира. */
    private AutomatonOperationHelper() {
    }

    /**
     * Генерира уникално име за ново състояние.
     *
     * @return име от вида "q0", "q1", "q2"...
     */
    static String generateStateName() {
        return "q" + (stateCounter++);
    }

    /**
     * Създава изцяло нов обект State (дълбоко копие).
     *
     * @param s оригиналното състояние
     * @return ново състояние със същото име и статус
     */
    static State copyState(State s) {
        return new State(s.getName(), s.isAccepting());
    }

    /**
     * Копира всички състояния и преходи от source в destination,
     * без да променя оригиналния автомат.
     *
     * @param source      автоматът, който копираме
     * @param destination автоматът, в който поставяме копията
     * @return речник, съпоставящ оригиналните състояния с техните копия
     */
    static Map<State, State> copyAutomatonData(Automaton source, Automaton destination) {
        Map<State, State> stateMap = new HashMap<>();
        for (State s : source.getStates()) {
            State copy = copyState(s);
            destination.addState(copy);
            stateMap.put(s, copy);
        }
        for (Transition t : source.getTransitions()) {
            State fromCopy = stateMap.get(t.getFrom());
            State toCopy = stateMap.get(t.getTo());
            destination.addTransition(fromCopy, t.getSymbol(), toCopy);
        }
        return stateMap;
    }

    /**
     * Проверява дали дадено множество съдържа поне едно финално състояние.
     *
     * @param set множеството от състояния
     * @return true ако поне едно състояние е финално
     */
    static boolean containsAccepting(Set<State> set) {
        for (State s : set) {
            if (s.isAccepting()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Генерира четимо име за ДКА състояние от множество НКА състояния.
     * Пример: множество {q1, q0} дава името "{q0,q1}".
     *
     * @param set множеството от състояния
     * @return име в скоби, сортирано по азбучен ред
     */
    static String setName(Set<State> set) {
        List<String> names = new ArrayList<>();
        for (State s : set) {
            names.add(s.getName());
        }
        Collections.sort(names);
        return "{" + String.join(",", names) + "}";
    }
}