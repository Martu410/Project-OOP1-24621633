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
     * Пътят до текущо отворения файл (или {@code null}, ако няма отворен файл).
     * Използва се от командата {@code save} (без аргумент), за да запише
     * обратно в същия файл, от който са били прочетени данните.
     */
    private String currentFile;

    /**
     * Създава празен мениджър без заредени автомати.
     */
    public AutomatonManager() {
        this.automata = new HashMap<>();
        this.currentFile = null;
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
     * Връща пътя до текущо отворения файл.
     *
     * @return пътят до файла или {@code null}, ако няма отворен файл
     */
    public String getCurrentFile() {
        return currentFile;
    }

    /**
     * Задава пътя до текущо отворения файл.
     *
     * @param currentFile пътят до файла (или {@code null}, за да се отбележи, че няма отворен файл)
     */
    public void setCurrentFile(String currentFile) {
        this.currentFile = currentFile;
    }

    /**
     * Проверява дали в момента има отворен файл.
     *
     * @return {@code true}, ако има отворен файл
     */
    public boolean isFileOpen() {
        return currentFile != null;
    }

    /**
     * Изчиства всички автомати от паметта и затваря текущия файл.
     */
    public void clear() {
        automata.clear();
        currentFile = null;
    }
}