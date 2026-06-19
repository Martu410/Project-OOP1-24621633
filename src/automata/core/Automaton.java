package automata.core;

import automata.model.State;
import automata.model.Transition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Представя краен автомат (детерминиран или недетерминиран с епсилон-преходи).
 *
 * <p>Класът реализира {@link Recognizable} и осигурява пълната логика за
 * симулация и анализ на автомата: разпознаване на думи, проверка за празен
 * и краен език, проверка за детерминираност и изчисляване на епсилон-затваряния.</p>
 *
 * <p>За производителност преходите се пазят едновременно в списък (за
 * запазване на реда при сериализация) и в речник {@code transitionMap},
 * който осигурява достъп за константно време O(1) до изходящите преходи на
 * всяко състояние.</p>
 */
public class Automaton implements Recognizable {

    /** Уникалното име (идентификатор) на автомата, например "A1". */
    private String id;

    /** Началното (стартово) състояние на автомата. */
    private State startState;

    /** Списък с всички състояния на автомата. */
    private List<State> states;

    /** Списък с всички преходи — пази реда им за нуждите на сериализацията. */
    private List<Transition> transitions;

    /**
     * Речник, който за всяко състояние пази неговите изходящи преходи.
     * Осигурява търсене за константно време O(1) вместо линейно обхождане.
     */
    private Map<State, List<Transition>> transitionMap;

    /**
     * Създава нов празен автомат със зададено име.
     *
     * @param id уникалният идентификатор на автомата
     */
    public Automaton(String id) {
        this.id            = id;
        this.states        = new ArrayList<>();
        this.transitions   = new ArrayList<>();
        this.transitionMap = new HashMap<>();
    }

    /**
     * Връща идентификатора на автомата.
     *
     * @return името на автомата
     */
    public String getId() { return id; }

    /**
     * Задава нов идентификатор на автомата.
     *
     * @param id новият идентификатор
     */
    public void setId(String id) { this.id = id; }

    /**
     * Връща началното състояние на автомата.
     *
     * @return стартовото състояние или {@code null}, ако не е зададено
     */
    public State getStartState() { return startState; }

    /**
     * Задава началното състояние на автомата.
     *
     * @param startState новото стартово състояние
     */
    public void setStartState(State startState) { this.startState = startState; }

    /**
     * Връща списъка с всички състояния на автомата.
     *
     * @return списък със състоянията
     */
    public List<State> getStates() { return states; }

    /**
     * Връща списъка с всички преходи на автомата.
     *
     * @return списък с преходите
     */
    public List<Transition> getTransitions() { return transitions; }

    /**
     * Връща списъка с изходящите преходи на дадено състояние.
     * Ако състоянието няма записани преходи, връща празен списък.
     *
     * <p>Методът е публичен, за да могат класовете за анализ от пакета
     * {@code automata.core.analysis} да ползват бързия достъп O(1) до
     * преходите, без да им се разкрива вътрешният речник.</p>
     *
     * @param state състоянието, чиито преходи търсим
     * @return списък с изходящите преходи
     */
    public List<Transition> getTransitionsFrom(State state) {
        List<Transition> list = transitionMap.get(state);
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    /**
     * Добавя ново състояние към автомата и създава празен запис за него в
     * речника с преходите.
     *
     * @param state състоянието за добавяне
     */
    public void addState(State state) {
        states.add(state);
        if (!transitionMap.containsKey(state)) {
            transitionMap.put(state, new ArrayList<>());
        }
    }

    /**
     * Създава и добавя нов преход към автомата.
     *
     * <p>Преходът се добавя както в общия списък (за запазване на реда), така
     * и в речника {@code transitionMap} (за бърз достъп).</p>
     *
     * @param from   изходното състояние
     * @param symbol символът на прехода ('E' за епсилон)
     * @param to     целевото състояние
     */
    public void addTransition(State from, char symbol, State to) {
        Transition t = new Transition(from, symbol, to);
        transitions.add(t);
        List<Transition> list = transitionMap.get(from);
        if (list == null) {
            list = new ArrayList<>();
            transitionMap.put(from, list);
        }
        list.add(t);
    }

    /**
     * Изчислява епсилон-затварянето на дадено множество от състояния.
     *
     * <p>Епсилон-затварянето включва всички състояния, достижими от началното
     * множество единствено чрез епсилон-преходи (без четене на символ).
     * Реализацията е итеративно обхождане в дълбочина (DFS), което използва
     * списък като стек и така избягва риска от препълване на стека при дълги
     * вериги от епсилон-преходи.</p>
     *
     * @param initialStates началното множество от състояния
     * @return множеството от всички епсилон-достижими състояния
     */
    public Set<State> getEpsilonClosure(Set<State> initialStates) {
        Set<State> closure = new HashSet<>(initialStates); // Тук пазим резултата
        List<State> stack = new ArrayList<>(initialStates); // Списък, който ползваме като стек

        // Докато има състояния в стека
        while (!stack.isEmpty()) {
            State current = stack.remove(stack.size() - 1); // Вадим последния елемент (върха на стека)
            // Обхождаме всички преходи за това състояние
            for (Transition t : getTransitionsFrom(current)) {
                // Ако преходът е епсилон ('E') и още не сме добавили състоянието
                if (t.getSymbol() == 'E' && !closure.contains(t.getTo())) {
                    closure.add(t.getTo()); // Добавяме го в резултата
                    stack.add(t.getTo());   // Слагаме го в стека, за да го проверим и него после
                }
            }
        }
        return closure;
    }

    /**
     * Проверява дали дадена дума се разпознава (приема) от автомата.
     *
     * <p>Реализира симулация на НКА чрез проследяване на множество от текущи
     * активни състояния. След всеки прочетен символ се изчислява новото
     * епсилон-затваряне. Думата се приема, ако поне едно от крайните активни
     * състояния е финално.</p>
     *
     * @param word думата за проверка
     * @return {@code true}, ако думата принадлежи на езика на автомата
     */
    @Override
    public boolean recognize(String word) {
        if (startState == null) return false; // Ако нямаме старт, няма как да четем

        Set<State> current = new HashSet<>();
        current.add(startState);
        current = getEpsilonClosure(current); // Първо намираме къде можем да отидем (с 'E')

        // Минаваме през всяка буква от думата
        for (char symbol : word.toCharArray()) {
            Set<State> next = new HashSet<>();
            // За всяко състояние, в което се намираме в момента
            for (State state : current) {
                // Проверяваме къде можем да отидем с текущата буква
                for (Transition t : getTransitionsFrom(state)) {
                    if (t.getSymbol() == symbol) next.add(t.getTo());
                }
            }
            // След като прочетем буквата, пак правим епсилон-затваряне
            current = getEpsilonClosure(next);
        }

        // Накрая проверяваме дали поне едно от състоянията, до които сме стигнали, е финално
        for (State state : current) {
            if (state.isAccepting()) return true;
        }
        return false;
    }

    /**
     * Връща множеството от всички състояния, достижими от началното състояние.
     *
     * <p>Обхожда графа на автомата в дълбочина (DFS), започвайки от стартовото
     * състояние и следвайки преходите. Методът е общ за класовете за анализ
     * (например проверките за празнота и крайност), които иначе биха повтаряли
     * една и съща логика за обхождане.</p>
     *
     * @return множеството от достижими състояния (празно, ако няма старт)
     */
    public Set<State> getReachableStates() {
        Set<State> reachable = new HashSet<>();
        if (startState == null) {
            return reachable;
        }
        List<State> stack = new ArrayList<>(); // Списък, който ползваме като стек за DFS
        stack.add(startState);
        while (!stack.isEmpty()) {
            State current = stack.remove(stack.size() - 1); // Вадим върха на стека
            if (reachable.add(current)) { // Ако е ново (не сме го виждали), добавяме съседите
                for (Transition t : getTransitionsFrom(current)) {
                    stack.add(t.getTo());
                }
            }
        }
        return reachable;
    }

    /**
     * Отпечатва на конзолата подробна информация за всички преходи на автомата.
     */
    public void printInfo() {
        System.out.println("Автомат: " + id);
        if (transitions.isEmpty()) {
            System.out.println("  (няма преходи)");
        } else {
            for (Transition t : transitions) System.out.println("  " + t);
        }
    }

    /**
     * Проверява дали автоматът е детерминиран (ДКА).
     *
     * <p>Делегира към класа {@link automata.core.analysis.DeterminismCheck}.</p>
     *
     * @return {@code true}, ако автоматът е детерминиран
     */
    public boolean isDeterministic() {
        return new automata.core.analysis.DeterminismCheck().check(this);
    }

    /**
     * Проверява дали езикът на автомата е празен.
     *
     * <p>Делегира към класа {@link automata.core.analysis.EmptinessCheck}.</p>
     *
     * @return {@code true}, ако автоматът не приема нито една дума
     */
    @Override
    public boolean isEmpty() {
        return new automata.core.analysis.EmptinessCheck().check(this);
    }

    /**
     * Проверява дали езикът на автомата е краен.
     *
     * <p>Делегира към класа {@link automata.core.analysis.FinitenessCheck}.</p>
     *
     * @return {@code true}, ако езикът е краен; {@code false}, ако е безкраен
     */
    public boolean isFinite() {
        return new automata.core.analysis.FinitenessCheck().check(this);
    }
}