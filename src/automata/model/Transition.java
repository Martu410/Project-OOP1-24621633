package automata.model;

/**
 * Клас за преход (стрелката) между две състояния.
 * Направен е като Immutable (неизменяем) - веднъж създаден, не се пипа!
 */
public class Transition {
    // Използваме final, за да не може никой да промени откъде тръгва преходът
    private final State from;
    // Символът на прехода (буква или 'E' за празна дума)
    private final char symbol;
    // Накъде отива преходът
    private final State to;

    // Конструктор за създаване на прехода
    public Transition(State from, char symbol, State to) {
        this.from   = from;
        this.symbol = symbol;
        this.to     = to;
    }

    // Само гетъри (без сетъри, защото полетата са final)
    public State getFrom() { return from; }
    public char getSymbol() { return symbol; }
    public State getTo() { return to; }

    // Красиво принтиране за конзолата (напр. "q0 --(a)--> q1")
    @Override
    public String toString() {
        return from.getName() + " --(" + symbol + ")--> " + to.getName();
    }
}