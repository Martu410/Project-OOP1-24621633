package automata.model;

/**
 * Представя преход (насочено ребро) между две състояния в краен автомат.
 *
 * <p>Преходът е напълно непроменим (immutable) — и трите му полета са
 * {@code final} и нямат сетъри. Веднъж създаден, преходът не може да бъде
 * модифициран, което гарантира сигурност по време на симулацията.</p>
 */
public class Transition {

    /** Състоянието, от което започва преходът. */
    private final State from;

    /** Символът на прехода — буква от азбуката или 'E' за епсилон-преход. */
    private final char symbol;

    /** Състоянието, към което води преходът. */
    private final State to;

    /**
     * Създава нов преход.
     *
     * @param from   изходното състояние
     * @param symbol символът на прехода ('E' за епсилон)
     * @param to     целевото състояние
     */
    public Transition(State from, char symbol, State to) {
        this.from = from;
        this.symbol = symbol;
        this.to = to;
    }

    /**
     * Връща изходното състояние на прехода.
     *
     * @return състоянието, от което започва преходът
     */
    public State getFrom() {
        return from;
    }

    /**
     * Връща символа на прехода.
     *
     * @return символът ('E' означава епсилон-преход)
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Връща целевото състояние на прехода.
     *
     * @return състоянието, към което води преходът
     */
    public State getTo() {
        return to;
    }

    /**
     * Връща текстово представяне на прехода.
     *
     * @return низ във формат "from --(symbol)--&gt; to"
     */
    @Override
    public String toString() {
        return from.getName() + " --(" + symbol + ")--> " + to.getName();
    }
}