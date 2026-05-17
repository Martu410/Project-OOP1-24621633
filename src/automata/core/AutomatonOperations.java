package automata.core;

import automata.model.State;
import automata.model.Transition;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class AutomatonOperations {
    // Брояч, за да генерираме уникални имена на новите състояния (q0, q1, q2...)
    private static final AtomicInteger stateCounter = new AtomicInteger(0);

    private static String generateStateName() {
        return "q" + stateCounter.getAndIncrement();
    }

    //  Метод за дълбоко копиране. Създава изцяло нов обект State.
    private static State copyState(State s) {
        return new State(s.getName(), s.isAccepting());
    }

    // Копира цял автомат. Правим го, за да не повредим/променим оригиналния автомат,когато го съединяваме с друг при union или concat.
    private static Map<State, State> copyAutomatonData(Automaton source, Automaton destination) {
        Map<State, State> stateMap = new HashMap<>();
        // Първо копираме състоянията
        for (State s : source.getStates()) {
            State copy = copyState(s);
            destination.addState(copy);
            stateMap.put(s, copy);
        }
        // После копираме и преходите
        for (Transition t : source.getTransitions()) {
            State fromCopy = stateMap.get(t.getFrom());
            State toCopy   = stateMap.get(t.getTo());
            destination.addTransition(fromCopy, t.getSymbol(), toCopy);
        }
        return stateMap;
    }

    // Обединение (A ∪ B)
    public static Automaton union(Automaton a1, Automaton a2, String newId) {
        Automaton result = new Automaton(newId);
        // Създаваме нов общ старт и нов общ финал (по Томпсън)
        State newStart  = new State(generateStateName(), false);
        State newAccept = new State(generateStateName(), true);
        result.addState(newStart);
        result.addState(newAccept);
        result.setStartState(newStart);

        // Копираме двата автомата вътре в новия
        Map<State, State> map1 = copyAutomatonData(a1, result);
        Map<State, State> map2 = copyAutomatonData(a2, result);

        // Пускаме епсилон преходи от новия старт към старите стартове
        result.addTransition(newStart, 'E', map1.get(a1.getStartState()));
        result.addTransition(newStart, 'E', map2.get(a2.getStartState()));

        // Пускаме епсилон преходи от старите финали към новия финал и ги правим нефинални
        for (State copy : result.getStates()) {
            if (copy.isAccepting() && !copy.equals(newAccept)) {
                result.addTransition(copy, 'E', newAccept);
                copy.setAccepting(false);
            }
        }
        return result;
    }

    // Конкатенация (A . B)
    public static Automaton concat(Automaton a1, Automaton a2, String newId) {
        Automaton result = new Automaton(newId);
        Map<State, State> map1 = copyAutomatonData(a1, result);
        Map<State, State> map2 = copyAutomatonData(a2, result);

        // Стартът на новия е стартът на първия
        result.setStartState(map1.get(a1.getStartState()));

        // Финалите на първия отиват със 'E' към старта на втория
        for (State original : a1.getStates()) {
            State copy = map1.get(original);
            if (copy.isAccepting()) {
                result.addTransition(copy, 'E', map2.get(a2.getStartState()));
                copy.setAccepting(false);
            }
        }
        return result;
    }

    // Звезда на Клини (A*)
    public static Automaton kleene(Automaton a, String newId) {
        Automaton result = new Automaton(newId);
        State newStart  = new State(generateStateName(), false);
        State newAccept = new State(generateStateName(), true);
        result.addState(newStart);
        result.addState(newAccept);
        result.setStartState(newStart);

        Map<State, State> map = copyAutomatonData(a, result);
        State copiedStart = map.get(a.getStartState());

        // 'E' от новия старт към стария старт и директно към новия финал (за празната дума)
        result.addTransition(newStart, 'E', copiedStart);
        result.addTransition(newStart, 'E', newAccept);

        // Старите финали се връщат в стария старт (за повторението) и отиват в новия финал
        for (State copy : result.getStates()) {
            if (copy.isAccepting() && !copy.equals(newAccept)) {
                result.addTransition(copy, 'E', copiedStart);
                result.addTransition(copy, 'E', newAccept);
                copy.setAccepting(false);
            }
        }
        return result;
    }
    // Позитивна обвивка (A+), което означава 1 или повече повторения.
    // Математически това е A съединено с A* (A . A*).
    public static Automaton positiveKleene(Automaton a, String newId) {
        String tempId = newId + "_star"; // Временно име за звездата
        Automaton star = kleene(a, tempId); // Първо правим A*
        return concat(a, star, newId); // После ги залепяме: A . A*
    }

    // Превръща НКА (Недетерминиран) в ДКА (Детерминиран автомат).
    // Използва "алгоритъм на подмножествата" (Subset construction).
    public static Automaton determinize(Automaton nfa, String newId) {
        Automaton dfa = new Automaton(newId);

        // Намираме азбуката (всички букви, които се ползват, без епсилон 'E')
        Set<Character> alphabet = new HashSet<>();
        for (Transition t : nfa.getTransitions()) {
            if (t.getSymbol() != 'E') alphabet.add(t.getSymbol());
        }

        // Началното състояние на новия ДКА е епсилон-затварянето на стария старт
        Set<State> startSet = new HashSet<>();
        startSet.add(nfa.getStartState());
        startSet = nfa.getEpsilonClosure(startSet);

        // Речник, който помни кои множества от НКА състояния на кое ДКА състояние отговарят
        Map<Set<State>, State> dfaStates = new LinkedHashMap<>();
        Queue<Set<State>> worklist = new ArrayDeque<>(); // Опашка за нещата, които трябва да обработим

        // Правим стартовото ДКА състояние
        boolean startAccepting = containsAccepting(startSet); // Ако вътре има финално -> и новото е финално
        State dfaStart = new State(setName(startSet), startAccepting);
        dfaStates.put(startSet, dfaStart);
        dfa.addState(dfaStart);
        dfa.setStartState(dfaStart);
        worklist.add(startSet);

        // Въртим цикъла, докато имаме необработени множества
        while (!worklist.isEmpty()) {
            Set<State> current = worklist.poll(); // Взимаме поредното множество
            State dfaCurrent   = dfaStates.get(current); // Взимаме му ДКА състоянието

            // За всяка буква от азбуката проверяваме къде можем да отидем
            for (char symbol : alphabet) {
                Set<State> nextSet = new HashSet<>();
                for (State s : current) {
                    for (Transition t : nfa.getTransitions()) {
                        // Ако преходът е с тази буква, добавяме го
                        if (t.getFrom().equals(s) && t.getSymbol() == symbol) {
                            nextSet.add(t.getTo());
                        }
                    }
                }
                if (nextSet.isEmpty()) continue; // Ако не отива никъде с тази буква - пропускаме

                // Правим епсилон-затваряне на резултата (защото пак може да се пътува безплатно с 'E')
                nextSet = nfa.getEpsilonClosure(nextSet);

                // Ако сме открили напълно ново множество, което досега не сме виждали
                if (!dfaStates.containsKey(nextSet)) {
                    boolean acc = containsAccepting(nextSet); // Проверяваме дали е финално
                    State newDfaState = new State(setName(nextSet), acc); // Създаваме го
                    dfaStates.put(nextSet, newDfaState); // Записваме го в речника
                    dfa.addState(newDfaState); // Добавяме го в автомата
                    worklist.add(nextSet); // Слагаме го в опашката за обработка
                }
                // Накрая просто пускаме прехода между двете ДКА състояния
                dfa.addTransition(dfaCurrent, symbol, dfaStates.get(nextSet));
            }
        }
        return dfa;
    }

    // Помощен метод: проверява дали в дадено множество има поне едно финално състояние
    private static boolean containsAccepting(Set<State> set) {
        for (State s : set) {
            if (s.isAccepting()) return true;
        }
        return false;
    }

    // Помощен метод: кръщава новото ДКА състояние.
    // Примерно ако обединява q0 и q1, ще го кръсти "{q0,q1}"
    private static String setName(Set<State> set) {
        StringBuilder sb = new StringBuilder("{");
        List<String> names = new ArrayList<>();
        for (State s : set) names.add(s.getName());
        Collections.sort(names); // Сортираме ги по азбучен ред
        sb.append(String.join(",", names));
        sb.append("}");
        return sb.toString();
    }
}