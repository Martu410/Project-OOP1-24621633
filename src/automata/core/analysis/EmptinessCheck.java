package automata.core.analysis;

import automata.core.Automaton;
import automata.model.State;

import java.util.Set;

/**
 * Проверява дали езикът на даден автомат е празен.
 *
 * <p>Езикът е празен, ако от началното състояние не е достижимо нито едно
 * финално състояние. Класът използва {@link Automaton#getReachableStates()}
 * за обхождането и след това проверява дали сред достижимите има финално
 * състояние.</p>
 */
public class EmptinessCheck {

    /**
     * Извършва проверката за празнота върху подадения автомат.
     *
     * @param automaton автоматът за проверка
     * @return {@code true}, ако автоматът не приема нито една дума
     */
    public boolean check(Automaton automaton) {
        // Взимаме всички достижими от старта състояния (общо обхождане в Automaton)
        Set<State> reachable = automaton.getReachableStates();

        // Ако някое достижимо състояние е финално -> езикът НЕ е празен
        for (State s : reachable) {
            if (s.isAccepting()) {
                return false;
            }
        }
        // Няма достижимо финално състояние -> езикът е празен
        return true;
    }
}