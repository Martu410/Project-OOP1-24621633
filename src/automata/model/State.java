package automata.model;

import java.util.Objects;


public class State {
    // Името на състоянието, например "q0" или "q1"
    private String name;
    // Флаг, който казва дали това състояние е крайно (приемащо)
    private boolean isAccepting;

    // Конструктор за създаване на ново състояние
    public State(String name, boolean isAccepting) {
        this.name = name;
        this.isAccepting = isAccepting;
    }

    // Гетъри и сетъри за достъп до private полетата
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isAccepting() { return isAccepting; }
    public void setAccepting(boolean accepting) { this.isAccepting = accepting; }

    // Предефинираме equals, за да кажем на Java кога 2 състояния са еднакви
    // Ако две състояния имат едно и също име, значи са един и същи обект
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Ако сочат към едно и също място в паметта - еднакви са
        if (o == null || getClass() != o.getClass()) return false; // Ако обектът е празен или от друг клас
        State state = (State) o; // Кастваме го към State
        return Objects.equals(name, state.name); // Сравняваме им имената
    }

    // hashCode работи в екип с equals. Трябва ни, за да слагаме състоянията в HashSet/HashMap
    @Override
    public int hashCode() {
        return Objects.hash(name); // Генерираме уникален номер на базата на името
    }

    // Метод за красиво принтиране в конзолата
    @Override
    public String toString() {
        // Ако е финално, добавяме фраза до името му
        return name + (isAccepting ? "[accepting]" : "");
    }
}