package automata.model;

// Клас, който представя едно състояние в крайния автомат
public class State {
    // Име на състоянието (напр. "q0", "q1")
    private String name;
    // Флаг, указващ дали състоянието е финално (приемащо)
    private boolean isAccepting;

    // Конструктор за създаване на ново състояние с дадено име и тип
    public State(String name, boolean isAccepting) {
        this.name = name; // Инициализираме името
        this.isAccepting = isAccepting; // Инициализираме типа на състоянието
    }

    // Връща името на състоянието
    public String getName() {
        return name;
    }

    // Променя името на състоянието
    public void setName(String name) {
        this.name = name;
    }

    // Проверява дали състоянието е финално
    public boolean isAccepting() {
        return isAccepting;
    }

    // Задава дали състоянието да бъде финално или не
    public void setAccepting(boolean accepting) {
        this.isAccepting = accepting;
    }

    // Сравнява две състояния по тяхното име, за да не се дублират в Set колекции
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Ако са на един и същ адрес в паметта, са еднакви
        if (o == null || getClass() != o.getClass()) return false; // Ако обектът е null или от друг клас, не са еднакви
        State state = (State) o; // Кастваме обекта към класа State
        return java.util.Objects.equals(name, state.name); // Сравняваме имената им за равенство
    }

    // Генерира уникален хеш код базиран на името на състоянието
    @Override
    public int hashCode() {
        return java.util.Objects.hash(name);
    }
}