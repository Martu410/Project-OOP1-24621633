package automata.core.analysis;

import automata.core.Automaton;
import automata.model.State;
import automata.model.Transition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Проверява дали езикът на даден автомат е краен.
 *
 * <p>Езикът е безкраен тогава и само тогава, когато в "полезния" подграф на
 * автомата има цикъл. Полезни са състоянията, които са едновременно достижими
 * от старта и от които е достижимо финално състояние.</p>
 *
 * <p>Алгоритъмът протича в четири стъпки: (1) намиране на достижимите състояния
 * чрез обхождане напред; (2) намиране на ко-достижимите чрез обхождане по
 * обърнатия граф; (3) сечение на двете множества; (4) търсене на цикъл сред
 * полезните състояния чрез алгоритъма на Kahn за топологично сортиране.</p>
 */
public class FinitenessCheck {

    /**
     * Извършва проверката за крайност на езика върху подадения автомат.
     *
     * @param automaton автоматът за проверка
     * @return {@code true}, ако езикът е краен; {@code false}, ако е безкраен
     */
    public boolean check(Automaton automaton) {
        State startState = automaton.getStartState();
        if (startState == null) return true; // Няма старт -> езикът е празен (следователно краен)

        // СТЪПКА 1: Намираме всички "достижими" състояния (общо обхождане в Automaton)
        Set<State> reachable = automaton.getReachableStates();
        List<State> queue = new ArrayList<>(); // Списък, който ползваме като опашка по-долу

        // СТЪПКА 2: Намираме "ко-достижимите" (от които може да се стигне до ФИНАЛ)
        // За целта обръщаме посоката на преходите
        Map<State, List<State>> reverse = new HashMap<>();
        for (State s : reachable) reverse.put(s, new ArrayList<>());
        for (State s : reachable) {
            for (Transition t : automaton.getTransitionsFrom(s)) {
                if (reachable.contains(t.getTo())) reverse.get(t.getTo()).add(s);
            }
        }

        Set<State> coReachable = new HashSet<>();
        // Започваме търсенето наобратно от всички финални състояния
        for (State s : reachable) if (s.isAccepting()) queue.add(s);
        while (!queue.isEmpty()) {
            State s = queue.remove(0);
            if (coReachable.add(s)) {
                List<State> preds = reverse.get(s);
                if (preds != null) {
                    for (State pred : preds) queue.add(pred);
                }
            }
        }

        // СТЪПКА 3: Полезни състояния = достижими от старта И водещи до финал
        Set<State> useful = new HashSet<>(reachable);
        useful.retainAll(coReachable);

        // СТЪПКА 4: Търсим цикъл само сред ПОЛЕЗНИТЕ състояния (алгоритъм на Kahn)
        Map<State, Integer> inDegree = new HashMap<>(); // Колко стрелки ВЛИЗАТ във всяко състояние
        for (State s : useful) inDegree.put(s, 0);
        for (State s : useful) {
            for (Transition t : automaton.getTransitionsFrom(s)) {
                if (useful.contains(t.getTo())) {
                    inDegree.put(t.getTo(), inDegree.get(t.getTo()) + 1);
                }
            }
        }

        List<State> topoQueue = new ArrayList<>(); // Списък като опашка за топологичното сортиране
        for (State s : useful) if (inDegree.get(s) == 0) topoQueue.add(s);

        int processed = 0; // Броим колко състояния сме обработили
        while (!topoQueue.isEmpty()) {
            State s = topoQueue.remove(0);
            processed++;
            for (Transition t : automaton.getTransitionsFrom(s)) {
                if (useful.contains(t.getTo())) {
                    int newDegree = inDegree.get(t.getTo()) - 1;
                    inDegree.put(t.getTo(), newDegree);
                    if (newDegree == 0) topoQueue.add(t.getTo());
                }
            }
        }

        // Ако всички полезни състояния са обработени -> няма цикъл -> езикът е КРАЕН.
        // Ако processed е по-малко -> има цикъл -> езикът е БЕЗКРАЕН.
        return processed == useful.size();
    }
}