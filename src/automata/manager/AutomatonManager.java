package automata.manager;

import automata.core.Automaton;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// Клас, който отговаря за съхранението и управлението на всички създадени автомати
public class AutomatonManager {
    // Речник (Map), който съпоставя ID-то на автомата (ключ) със самия обект (стойност)
    private Map<String, Automaton> automataMap;

    // Конструктор, инициализиращ празен мениджър
    public AutomatonManager() {
        this.automataMap = new HashMap<>(); // Използваме HashMap за бързо търсене по ID
    }

    // Добавя нов автомат в мениджъра
    public void addAutomaton(Automaton a) {
        automataMap.put(a.getId(), a); // Поставяме ID-то като ключ, а обекта като стойност
    }

    // Извлича автомат от мениджъра по зададено ID
    public Automaton getAutomaton(String id) {
        return automataMap.get(id); // Връща автомата или null, ако не съществува
    }

    // Изтрива всички автомати от паметта
    public void clear() {
        automataMap.clear(); // Изчиства вътрешния речник
    }

    // Извежда списък с имената на всички заредени автомати в конзолата
    public void listAutomata() {
        if (automataMap.isEmpty()) { // Проверяваме дали речникът е празен
            System.out.println("Няма заредени автомати в паметта.");
            return; // Прекратяваме изпълнението на метода
        }
        System.out.println("Налични автомати:");
        // Обхождаме всички ключове (ID-та) в речника
        for (String id : automataMap.keySet()) {
            System.out.println("  - " + id); // Отпечатваме всяко ID
        }
    }

    // Връща колекция от автоматите само за четене
    public Map<String, Automaton> getAllAutomata() {
        return Collections.unmodifiableMap(automataMap); // Предпазва вътрешната структура от неоторизирана промяна
    }
}