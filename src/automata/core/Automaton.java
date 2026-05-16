package automata.core;

import automata.model.State;
import automata.model.Transition;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Основен клас, който представя крайния автомат и реализира логиката за разпознаване
public class Automaton implements Recognizable {
    // Уникален идентификатор (име) на автомата
    private String id;
    // Референция към началното състояние на автомата
    private State startState;
    // Списък, съхраняващ всички състояния в автомата
    private List<State> states;
    // Списък, съхраняващ всички преходи в автомата
    private List<Transition> transitions;

    // Конструктор, който създава празен автомат с дадено име
    public Automaton(String id) {
        this.id = id; // Записваме ID-то
        this.states = new ArrayList<>(); // Инициализираме празен списък със състояния
        this.transitions = new ArrayList<>(); // Инициализираме празен списък с преходи
    }

    // Връща идентификатора на автомата
    public String getId() {
        return id;
    }

    // Променя идентификатора на автомата
    public void setId(String id) {
        this.id = id;
    }

    // Връща началното състояние
    public State getStartState() {
        return startState;
    }

    // Задава кое да бъде началното състояние
    public void setStartState(State startState) {
        this.startState = startState;
    }

    // Връща всички състояния
    public List<State> getStates() {
        return states;
    }

    // Връща всички преходи
    public List<Transition> getTransitions() {
        return transitions;
    }

    // Добавя ново състояние към списъка на автомата
    public void addState(State state) {
        states.add(state); // Добавя обекта в ArrayList
    }

    // Създава и добавя нов преход към автомата
    public void addTransition(State from, char symbol, State to) {
        Transition newTransition = new Transition(from, symbol, to); // Инстанцираме нов преход
        this.transitions.add(newTransition); // Добавяме го в колекцията
    }

    // Намира Епсилон-затварянето за дадено множество от начални състояния
    public Set<State> getEpsilonClosure(Set<State> initialStates) {
        Set<State> closure = new HashSet<>(initialStates); // Създаваме множество и копираме началните състояния в него
        for (State s : initialStates) { // Обхождаме всяко начално състояние
            epsilonDFS(s, closure); // Извикваме рекурсивно търсене в дълбочина (DFS) за намиране на свързаните чрез Епсилон състояния
        }
        return closure; // Връщаме пълното множество от достижими състояния
    }

    // Помощен рекурсивен метод (DFS) за намиране на състояния, достижими чрез Епсилон преходи
    private void epsilonDFS(State current, Set<State> closure) {
        for (Transition t : transitions) { // Обхождаме всички налични преходи
            // Ако преходът тръгва от текущото състояние и е с празна дума ('E')
            if (t.getFrom().equals(current) && t.getSymbol() == 'E') {
                // Опитваме се да добавим целевото състояние; add() връща true, ако състоянието е ново
                if (closure.add(t.getTo())) {
                    epsilonDFS(t.getTo(), closure); // Ако е ново, продължаваме рекурсията от него
                }
            }
        }
    }

    // Проверява дали подадената дума се приема от автомата
    @Override
    public boolean recognize(String word) {
        if (startState == null) return false; // Ако няма начално състояние, думата не може да бъде разпозната

        Set<State> current = new HashSet<>(); // Създаваме множество за текущите активни състояния
        current.add(startState); // Винаги започваме от началното състояние
        current = getEpsilonClosure(current); // Правим Епсилон-затваряне преди да започнем четенето на букви

        // Обхождаме думата буква по буква
        for (char symbol : word.toCharArray()) {
            Set<State> next = new HashSet<>(); // Създаваме множество за състоянията, в които ще попаднем
            for (State state : current) { // За всяко едно от текущите състояния
                for (Transition t : transitions) { // Проверяваме всички възможни преходи
                    // Ако преходът тръгва от текущото състояние и е с текущата буква от думата
                    if (t.getFrom().equals(state) && t.getSymbol() == symbol) {
                        next.add(t.getTo()); // Добавяме целевото състояние в множеството за следващата стъпка
                    }
                }
            }
            current = getEpsilonClosure(next); // Правим Епсилон-затваряне на новите състояния след прочитане на буквата
        }

        // След като прочетем цялата дума, обхождаме финалния списък с достигнати състояния
        for (State state : current) {
            if (state.isAccepting()) return true; // Ако поне едно от тях е финално, думата е приета
        }
        return false; // Думата не води до финално състояние
    }

    // Проверява дали езикът на автомата е празен (т.е. дали съществува път от началното до някое финално състояние)
    @Override
    public boolean isEmpty() {
        // Ако няма начално състояние, няма как да стигнем до финално -> езикът със сигурност е празен
        if (startState == null) return true;

        // Множество за съхранение на вече посетените състояния, за да предотвратим безкрайни цикли при наличие на затворени пътища
        Set<State> visited = new HashSet<>();
        // Използваме структурата Стек (Stack) за реализация на алгоритъма "Търсене в дълбочина" (DFS)
        java.util.Stack<State> stack = new java.util.Stack<>();
        stack.push(startState); // Започваме обхождането от началното състояние

        // Въртим цикъла, докато има непосетени достижими състояния
        while (!stack.isEmpty()) {
            State current = stack.pop(); // Взимаме поредното състояние от стека

            // Ако намерим поне едно финално състояние, значи има поне една валидна дума -> езикът НЕ Е празен
            if (current.isAccepting()) return false;

            // Ако все още не сме проверявали това състояние
            if (!visited.contains(current)) {
                visited.add(current); // Маркираме го като проверено

                // Обхождаме всички налични преходи в автомата
                for (Transition t : transitions) {
                    // Ако преходът тръгва от нашето текущо състояние
                    if (t.getFrom().equals(current)) {
                        stack.push(t.getTo()); // Добавяме целевото състояние в стека, за да го проверим на следващите стъпки
                    }
                }
            }
        }
        // Ако сме обходили целия граф от достижими състояния и никое от тях не е финално -> езикът е празен
        return true;
    }
}