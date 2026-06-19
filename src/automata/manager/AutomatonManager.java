package automata.manager;

import automata.core.Automaton;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Управлява всички създадени автомати в рамките на текущата сесия.
 *
 * <p>Съхранява автоматите в речник, индексиран по техния уникален
 * идентификатор. Осигурява капсулация, като връща само неизменяемо
 * (read-only) копие на вътрешната структура чрез {@link #getAllAutomata()}.</p>
 */
public class AutomatonManager {

    /**
     * Речник, който съпоставя идентификатор на автомат към самия автомат.
     * Полето е {@code final}, за да не може речникът да бъде презаписан с нов обект.
     */
    private final Map<String, Automaton> automata;

    /**
     * Създава празен мениджър без заредени автомати.
     */
    public AutomatonManager() {
        this.automata = new HashMap<>();
    }

    /**
     * Добавя автомат в паметта, индексиран по неговия идентификатор.
     *
     * <p>Автоматът се добавя само ако той и неговият идентификатор не са {@code null}.</p>
     *
     * @param a автоматът за добавяне
     */
    public void addAutomaton(Automaton a) {
        if (a != null && a.getId() != null) {
            automata.put(a.getId(), a);
        }
    }

    /**
     * Връща автомат по неговия идентификатор.
     *
     * @param id идентификаторът на търсения автомат
     * @return автоматът или {@code null}, ако такъв не съществува
     */
    public Automaton getAutomaton(String id) {
        return automata.get(id);
    }

    /**
     * Връща неизменяемо (read-only) копие на всички заредени автомати.
     *
     * <p>Капсулацията предпазва вътрешния речник от външна промяна — опит за
     * модификация на върнатата колекция хвърля изключение.</p>
     *
     * @return неизменяема карта от идентификатори към автомати
     */
    public Map<String, Automaton> getAllAutomata() {
        return Collections.unmodifiableMap(automata);
    }

    /**
     * Проверява дали съществува автомат с даден идентификатор.
     *
     * @param id идентификаторът за проверка
     * @return {@code true}, ако автомат с този идентификатор е зареден
     */
    public boolean contains(String id) {
        return automata.containsKey(id);
    }

    /**
     * Изчиства всички автомати от паметта.
     */
    public void clear() {
        automata.clear();
    }
}