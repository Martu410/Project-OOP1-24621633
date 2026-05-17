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

    // Копира цял автомат. Правим го, за да не повредим/променим оригиналния автомат,
    // когато го съединяваме с друг при union или concat.
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
}