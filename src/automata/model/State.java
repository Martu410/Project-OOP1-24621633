package automata.model;

import java.util.Objects;

/**
 * Представя едно състояние (върх) в краен автомат.
 *
 * <p>Всяко състояние се идентифицира по уникалното си име. Класът
 * предефинира {@link #equals(Object)} и {@link #hashCode()}, базирани
 * единствено на името, за да функционира коректно като елемент в
 * {@link java.util.HashSet} и като ключ в {@link java.util.HashMap}.</p>
 */
public class State {

    /** Уникалното име на състоянието, например "q0" или "q1". */
    private String name;

    /** Флаг, който указва дали състоянието е крайно (приемащо). */
    private boolean isAccepting;

    /**
     * Създава ново състояние.
     *
     * @param name        уникалното име на състоянието
     * @param isAccepting {@code true}, ако състоянието е крайно (приемащо)
     */
    public State(String name, boolean isAccepting) {
        this.name = name;
        this.isAccepting = isAccepting;
    }

    /**
     * Връща името на състоянието.
     *
     * @return името на състоянието
     */
    public String getName() {
        return name;
    }

    /**
     * Задава ново име на състоянието.
     *
     * @param name новото име
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Проверява дали състоянието е крайно (приемащо).
     *
     * @return {@code true}, ако състоянието е крайно
     */
    public boolean isAccepting() {
        return isAccepting;
    }

    /**
     * Задава дали състоянието да бъде крайно (приемащо).
     *
     * @param accepting {@code true}, за да стане състоянието крайно
     */
    public void setAccepting(boolean accepting) {
        this.isAccepting = accepting;
    }

    /**
     * Сравнява две състояния по тяхното име.
     *
     * <p>Две състояния се считат за логически еднакви тогава и само тогава,
     * когато имената им съвпадат. Това е необходимо, за да не се дублират
     * състоянията в {@link java.util.HashSet}.</p>
     *
     * @param o обектът за сравнение
     * @return {@code true}, ако двете състояния имат еднакво име
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Objects.equals(name, state.name);
    }

    /**
     * Генерира хеш код въз основа на името на състоянието.
     *
     * <p>Методът работи в съгласие с {@link #equals(Object)} — две равни
     * състояния винаги имат еднакъв хеш код.</p>
     *
     * @return хеш код на състоянието
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Връща текстово представяне на състоянието.
     *
     * @return името, последвано от "[accepting]" ако състоянието е крайно
     */
    @Override
    public String toString() {
        return name + (isAccepting ? "[accepting]" : "");
    }
}