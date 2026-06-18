package automata.core.analysis;

import automata.core.Automaton;
import automata.model.State;
import automata.model.Transition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Проверява дали езикът на даден автомат е празен.
 *
 * <p>Езикът е празен, ако от началното състояние не е достижимо нито едно
 * финално състояние. Реализацията използва обхождане в дълбочина (DFS) със
 * списък като стек, като проверката за вече посетено състояние се прави в
 * началото на всяка итерация — това гарантира коректна работа при цикли.</p>
 */
public class EmptinessCheck {

    /**
     * Извършва проверката за празнота върху подадения автомат.
     *
     * @param automaton автоматът за проверка
     * @return {@code true}, ако автоматът не приема нито една дума
     */
    public boolean check(Automaton automaton) {
        State startState = automaton.getStartState();
        if (startState == null) return true;

        Set<State> visited = new HashSet<>(); // Пазим къде сме били
        List<State> stack = new ArrayList<>(); // Списък, който ползваме като стек за DFS
        stack.add(startState);

        while (!stack.isEmpty()) {
            State current = stack.remove(stack.size() - 1); // Вадим върха на стека

            // Проверяваме дали вече сме го посетили
            if (visited.contains(current)) continue;
            visited.add(current); // Отбелязваме го като посетено

            // Ако намерим финално състояние, езикът не е празен
            if (current.isAccepting()) return false;

            // Добавяме всички съседи в стека
            for (Transition t : automaton.getTransitionsFrom(current)) {
                stack.add(t.getTo());
            }
        }
        // Обиколили сме всичко без финално състояние -> езикът е празен
        return true;
    }
}