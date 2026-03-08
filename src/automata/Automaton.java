package automata;

import java.util.ArrayList;
import java.util.List;

public class Automaton {
    private String id;
    private State startState;
    private List<State> states;

    private List<Transition> transitions;

    public Automaton(String id) {
        this.id = id;
        this.states=new ArrayList<>();
        this.transitions=new ArrayList<>();
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
    public void addTransition(State from, char symbol, State to){
        Transition newTransition = new Transition(from, symbol, to);
        this.transitions.add(newTransition);
    }
}
