package automata.core.analysis;

import automata.core.Automaton;
import automata.model.State;
import automata.model.Transition;

import java.util.HashSet;
import java.util.Set;

/**
 * Проверява дали даден автомат е детерминиран (ДКА).
 *
 * <p>Автоматът е детерминиран, ако няма епсилон-преходи и от всяко състояние
 * има най-много един изходящ преход за всеки символ. Класът е изнесен отделно,
 * за да отговаря всеки вид анализ на автомат на собствен клас.</p>
 */
public class DeterminismCheck {

    /**
     * Извършва проверката за детерминираност върху подадения автомат.
     *
     * @param automaton автоматът за проверка
     * @return {@code true}, ако автоматът е детерминиран (ДКА)
     */
    public boolean check(Automaton automaton) {
        for (State state : automaton.getStates()) {
            Set<Character> seen = new HashSet<>(); // Буквите, които вече сме видели за това състояние
            for (Transition t : automaton.getTransitionsFrom(state)) {
                if (t.getSymbol() == 'E') return false; // ДКА няма епсилон преходи!
                if (!seen.add(t.getSymbol())) return false; // Повтаряща се буква -> не е ДКА
            }
        }
        return true;
    }
}