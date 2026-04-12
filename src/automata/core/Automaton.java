package automata.core;

import automata.model.State;
import automata.model.Transition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Automaton implements Recognizable {
    private String id;
    private State startState;
    private List<State> states;

    private List<Transition> transitions;

    public Automaton(String id) {
        this.id = id;
        this.states = new ArrayList<>();
        this.transitions = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public State getStartState() {
        return startState;
    }

    public void setStartState(State startState) {
        this.startState = startState;
    }

    public List<State> getStates() {
        return states;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void addState(State state) {
        states.add(state);
    }

    public void addTransition(State from, char symbol, State to) {
        Transition newTransition = new Transition(from, symbol, to);
        this.transitions.add(newTransition);
    }


    public Set<State> getEpsilonClosure(Set<State> initialStates) {
        Set<State> closure = new HashSet<>(initialStates);
        for (State s : initialStates) {
            epsilonDFS(s, closure);
        }
        return closure;
    }

    private void epsilonDFS(State current, Set<State> closure) {
        for (Transition t : transitions) {
            if (t.getFrom().equals(current) && t.getSymbol() == 'E') {
                if (closure.add(t.getTo())) {
                    epsilonDFS(t.getTo(), closure);
                }
            }
        }
    }

    @Override
    public boolean recognize(String word) {
        if (startState == null) return false;
        Set<State> current = new HashSet<>();
        current.add(startState);
        current = getEpsilonClosure(current);

        for (char symbol : word.toCharArray()) {
            Set<State> next = new HashSet<>();
            for (State state : current) {
                // Бавно сканиране на списъка
                for (Transition t : transitions) {
                    if (t.getFrom().equals(state) && t.getSymbol() == symbol) {
                        next.add(t.getTo());
                    }
                }
            }
            current = getEpsilonClosure(next);
        }
        for (State state : current) {
            if (state.isAccepting()) return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return !recognize("");
    }
}