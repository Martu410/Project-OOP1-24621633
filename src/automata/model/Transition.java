package automata.model;

// Клас, който описва преход между две състояния при прочитане на даден символ
public class Transition {
    // Състоянието, от което тръгва преходът
    private State from;
    // Символът, който се прочита ('E' означава Епсилон/празна дума)
    private char symbol;
    // Състоянието, в което се отива след прочитане на символа
    private State to;

    // Конструктор за инициализиране на прехода
    public Transition(State from, char symbol, State to) {
        this.from = from;     // Задаваме началното състояние
        this.symbol = symbol; // Задаваме символа на прехода
        this.to = to;         // Задаваме крайното състояние
    }

    // Връща началното състояние на прехода
    public State getFrom() {
        return from;
    }

    // Връща символа, с който се извършва преходът
    public char getSymbol() {
        return symbol;
    }

    // Връща състоянието, до което води преходът
    public State getTo() {
        return to;
    }
}