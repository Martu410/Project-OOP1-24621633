package automata.manager;

import automata.core.Automaton;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class AutomatonManager {
    private Map<String, Automaton> automataMap;

    public AutomatonManager() {
        this.automataMap = new HashMap<>();
    }

    public void addAutomaton(Automaton a) {
        automataMap.put(a.getId(), a);
    }

    public Automaton getAutomaton(String id) {
        return automataMap.get(id);
    }

    public void clear() {
        automataMap.clear();
    }

    public void listAutomata() {
        if (automataMap.isEmpty()) {
            System.out.println("Няма заредени автомати в паметта.");
            return;
        }
        System.out.println("Налични автомати:");
        for (String id : automataMap.keySet()) System.out.println("  - " + id);
    }

    public Map<String, Automaton> getAllAutomata() {
        return Collections.unmodifiableMap(automataMap);
    }
}