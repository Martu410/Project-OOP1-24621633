package automata.core.operations;

import automata.core.Automaton;

/**
 * Операция позитивна обвивка (Kleene Plus): L(a)⁺.
 *
 * <p>Означава едно или повече повторения. Математически се дефинира като
 * A · A* — конкатенация на оригиналния автомат с неговата звезда на Клини.
 * Затова операцията използва {@link KleeneOperation} и
 * {@link ConcatOperation} вътрешно.</p>
 */
public class PositiveKleeneOperation {

    /** Операция звезда на Клини, използвана за построяване на A*. */
    private final KleeneOperation kleeneOperation = new KleeneOperation();

    /** Операция конкатенация, използвана за свързване на A с A*. */
    private final ConcatOperation concatOperation = new ConcatOperation();

    /**
     * Построява автомат за позитивната обвивка на езика на a (A⁺ = A · A*).
     *
     * @param a     автоматът, върху който се прилага операцията
     * @param newId идентификатор на новия автомат
     * @return нов автомат за езика L(a)⁺
     */
    public Automaton apply(Automaton a, String newId) {
        Automaton star = kleeneOperation.apply(a, newId + "_star");
        return concatOperation.apply(a, star, newId);
    }
}