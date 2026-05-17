package automata.core;

import automata.model.State;
import automata.model.Transition;

import java.util.*;


public class Automaton implements Recognizable {
    private String id; // Името на автомата (напр. "A1")
    private State startState; // Началното състояние (старт)

    private List<State> states; // Списък с всички състояния
    private List<Transition> transitions; // Списък с всички преходи (за да им пазим реда при запазване)

    // (Map), който пази за всяко състояние какви изходящи преходи има.
    // Правим го, за да търсим преходи веднага, вместо да въртим for цикъл през всички преходи всеки път.
    private Map<State, List<Transition>> transitionMap;

    // Конструктор
    public Automaton(String id) {
        this.id            = id;
        this.states        = new ArrayList<>();
        this.transitions   = new ArrayList<>();
        this.transitionMap = new HashMap<>(); // Инициализираме речника
    }

    // Базови гетъри и сетъри
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public State getStartState() { return startState; }
    public void setStartState(State startState) { this.startState = startState; }
    public List<State> getStates() { return states; }
    public List<Transition> getTransitions() { return transitions; }

    // Добавя състояние в списъка и му прави празно място в речника
    public void addState(State state) {
        states.add(state);
        transitionMap.putIfAbsent(state, new ArrayList<>());
    }

    // Добавя нов преход
    public void addTransition(State from, char symbol, State to) {
        Transition t = new Transition(from, symbol, to);
        transitions.add(t); // Слагаме го в общия списък
        // Слагаме го и в бързия речник точно към състоянието "from"
        transitionMap.computeIfAbsent(from, k -> new ArrayList<>()).add(t);
    }

    // Търси всички състояния, до които можем да стигнем САМО чрез епсилон преходи (без да четем буква)
    public Set<State> getEpsilonClosure(Set<State> initialStates) {
        Set<State> closure = new HashSet<>(initialStates); // Тук ще пазим резултата
        Deque<State> stack = new ArrayDeque<>(initialStates); // Модерен стек за обхождане

        // Докато има състояния в стека
        while (!stack.isEmpty()) {
            State current = stack.pop(); // Вадим най-горното
            // Взимаме бързо от речника всички преходи за това състояние
            for (Transition t : transitionMap.getOrDefault(current, new ArrayList<>())) {
                // Ако преходът е епсилон ('E') и още не сме добавили състоянието
                if (t.getSymbol() == 'E' && !closure.contains(t.getTo())) {
                    closure.add(t.getTo()); // Добавяме го в резултата
                    stack.push(t.getTo());  // Слагаме го в стека, за да го проверим и него после
                }
            }
        }
        return closure;
    }

    // Проверява дали дадена дума се приема (НКА симулация)
    @Override
    public boolean recognize(String word) {
        if (startState == null) return false; // Ако нямаме старт, няма как да четем

        Set<State> current = new HashSet<>();
        current.add(startState);
        current = getEpsilonClosure(current); // Първо намираме къде можем да отидем (с 'E')

        // Минаваме през всяка буква от думата
        for (char symbol : word.toCharArray()) {
            Set<State> next = new HashSet<>();
            // За всяко състояние, в което се намираме в момента
            for (State state : current) {
                // Проверяваме къде можем да отидем с текущата буква
                for (Transition t : transitionMap.getOrDefault(state, new ArrayList<>())) {
                    if (t.getSymbol() == symbol) next.add(t.getTo());
                }
            }
            // След като прочетем буквата, пак правим епсилон-затваряне
            current = getEpsilonClosure(next);
        }

        // Накрая проверяваме дали поне едно от състоянията, до които сме стигнали, е финално
        for (State state : current) {
            if (state.isAccepting()) return true;
        }
        return false;
    }

    // Отпечатва всички преходи на автомата
    public void printInfo() {
        System.out.println("Автомат: " + id);
        if (transitions.isEmpty()) {
            System.out.println("  (няма преходи)");
        } else {
            for (Transition t : transitions) System.out.println("  " + t);
        }
    }

    // Проверява дали е автоматът е ДКА  (Детерминиран)
    public boolean isDeterministic() {
        for (State state : states) {
            Set<Character> seen = new HashSet<>(); // Тук пазим буквите, които вече сме видели за това състояние
            for (Transition t : transitionMap.getOrDefault(state, new ArrayList<>())) {
                if (t.getSymbol() == 'E') return false; // ДКА няма епсилон преходи!
                if (!seen.add(t.getSymbol())) return false; // Ако буквата се повтаря от едно състояние -> не е ДКА
            }
        }
        return true;
    }

    // Проверява дали езикът е празен
    @Override
    public boolean isEmpty() {
        if (startState == null) return true;

        Set<State> visited = new HashSet<>(); // Пазим къде сме били
        Deque<State> stack = new ArrayDeque<>(); // Модерен стек за DFS обхождане
        stack.push(startState);

        while (!stack.isEmpty()) {
            State current = stack.pop();

            // Проверяваме дали вече сме го посетили
            if (visited.contains(current)) continue;
            visited.add(current); // Отбелязваме го като посетено

            // Ако намерим финално състояние, езикът не е празен
            if (current.isAccepting()) return false;

            // Добавяме всички съседи в стека
            for (Transition t : transitionMap.getOrDefault(current, new ArrayList<>())) {
                stack.push(t.getTo());
            }
        }
        // Ако сме обиколили всичко и не сме намерили финално състояние -> празен е
        return true;
    }
    // Проверява дали езикът е краен (търси за цикли в графа)
    // Проверява дали езикът е краен или безкраен.
    // Езикът е безкраен, ако в автомата има ЦИКЪЛ, но не какъв да е цикъл,
    // а такъв, от който можеш да стигнеш до финално състояние!
    public boolean isFinite() {
        if (startState == null) return true; // Ако няма старт, езикът е празен (следователно е краен)

        // СТЪПКА 1: Намираме всички "достижими" състояния (до които можем да стигнем от старта)
        Set<State> reachable = new HashSet<>();
        Deque<State> queue = new ArrayDeque<>();
        queue.add(startState);
        while (!queue.isEmpty()) {
            State s = queue.poll();
            if (reachable.add(s)) { // Ако успешно го добавим (т.е. не сме го виждали)
                // Добавяме всичките му съседи в опашката
                for (Transition t : transitionMap.getOrDefault(s, new ArrayList<>())) queue.add(t.getTo());
            }
        }

        // СТЪПКА 2: Намираме всички "ко-достижими" състояния (от които може да се стигне до ФИНАЛ)
        // За целта обръщаме посоката на стрелките (преходите)
        Map<State, List<State>> reverse = new HashMap<>();
        for (State s : reachable) reverse.put(s, new ArrayList<>());
        for (State s : reachable) {
            for (Transition t : transitionMap.getOrDefault(s, new ArrayList<>())) {
                if (reachable.contains(t.getTo())) reverse.get(t.getTo()).add(s); // Записваме ги наобратно
            }
        }

        Set<State> coReachable = new HashSet<>();
        // Започваме търсенето наобратно, стартирайки от всички финални състояния
        for (State s : reachable) if (s.isAccepting()) queue.add(s);
        while (!queue.isEmpty()) {
            State s = queue.poll();
            if (coReachable.add(s)) {
                for (State pred : reverse.getOrDefault(s, new ArrayList<>())) queue.add(pred);
            }
        }

        // СТЪПКА 3: Полезни състояния = хем са достижими от старта, хем стигат до финала
        Set<State> useful = new HashSet<>(reachable);
        useful.retainAll(coReachable); // Оставяме само тези, които се засичат в двете множества

        // СТЪПКА 4: Търсим цикъл само сред ПОЛЕЗНИТЕ състояния (Топологично сортиране / Kahn's Algorithm)
        Map<State, Integer> inDegree = new HashMap<>(); // Пази колко стрелки ВЛИЗАТ във всяко състояние
        for (State s : useful) inDegree.put(s, 0);
        for (State s : useful) {
            for (Transition t : transitionMap.getOrDefault(s, new ArrayList<>())) {
                if (useful.contains(t.getTo())) inDegree.merge(t.getTo(), 1, Integer::sum); // Увеличаваме брояча
            }
        }

        Deque<State> topoQueue = new ArrayDeque<>();
        // Почваме от тези, в които не влизат никакви стрелки
        for (State s : useful) if (inDegree.get(s) == 0) topoQueue.add(s);

        int processed = 0; // Броим колко състояния сме обработили
        while (!topoQueue.isEmpty()) {
            State s = topoQueue.poll();
            processed++;
            // "Изтриваме" изходящите стрелки на това състояние
            for (Transition t : transitionMap.getOrDefault(s, new ArrayList<>())) {
                if (useful.contains(t.getTo())) {
                    // Ако след изтриването в следващото състояние вече не влизат стрелки - го добавяме в опашката
                    if (inDegree.merge(t.getTo(), -1, Integer::sum) == 0) topoQueue.add(t.getTo());
                }
            }
        }

        // Ако сме успели да обработим всички полезни състояния, значи НЯМА цикли -> езикът е КРАЕН (true).
        // Ако processed е по-малко, значи сме забили в цикъл -> езикът е БЕЗКРАЕН (false).
        return processed == useful.size();
    }
}