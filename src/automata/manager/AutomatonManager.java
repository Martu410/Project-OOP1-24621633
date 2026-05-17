package automata.manager;

import automata.core.Automaton;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class AutomatonManager {

    // final пази речника от презаписване с нов обект
    private final Map<String, Automaton> automata;

    public AutomatonManager() {
        this.automata = new HashMap<>(); // Инициализираме речника
    }

    // Добавя автомат в паметта
    public void addAutomaton(Automaton a) {
        if (a != null && a.getId() != null) {
            automata.put(a.getId(), a); // Слагаме го с ключ = неговото ID
        }
    }

    // Взима автомат по име
    public Automaton getAutomaton(String id) {
        return automata.get(id);
    }

    // ВАЖНО ЗА ЗАЩИТАТА: Капсулация!
    // Връщаме "unmodifiable" (заключено) копие на речника.
    // Така някой друг програмист не може да извика getAllAutomata().clear() и да ни изтрие данните!
    public Map<String, Automaton> getAllAutomata() {
        return Collections.unmodifiableMap(automata);
    }

    // Проверява дали имаме автомат с такова име
    public boolean contains(String id) {
        return automata.containsKey(id);
    }

    // Изчиства паметта
    public void clear() {
        automata.clear();
    }
}