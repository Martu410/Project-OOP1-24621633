package automata;

public class Transition {
    private State from;
    private char symbol;
    private State to;

    public Transition(State from, char symbol, State to) {
        this.from = from;
        this.symbol = symbol;
        this.to = to;
    }
    public State getFrom() {
        return from;
    }
    public char getSymbol() {
        return symbol;
    }
    public State getTo() {
        return to;
    }
}

