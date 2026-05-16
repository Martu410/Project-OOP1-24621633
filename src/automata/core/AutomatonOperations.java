package automata.core;

import automata.model.State;
import automata.model.Transition;

// Клас, съдържащ статични методи за извършване на математически операции над крайни автомати (Алгоритъм на Томпсън)
public class AutomatonOperations {
    // Статичен брояч за генериране на уникални имена на новите състояния
    private static int stateCounter = 0;

    // Помощен метод за генериране на уникално име за състояние (напр. "q0", "q1")
    private static String generateStateName() {
        return "q" + (stateCounter++); // Връща името с текущото число и след това увеличава брояча
    }

    // Помощен метод за копиране на всички състояния и преходи от един автомат (източник) в друг (цел)
    private static void copyAutomatonData(Automaton source, Automaton destination) {
        // Обхождаме състоянията на автомата-източник
        for (State s : source.getStates()) {
            destination.addState(s); // Добавяме ги като референции в целевия автомат
        }
        // Обхождаме преходите на автомата-източник
        for (Transition t : source.getTransitions()) {
            destination.addTransition(t.getFrom(), t.getSymbol(), t.getTo()); // Добавяме ги в целевия автомат
        }
    }

    // 1. Обединение (Union): Създава автомат, който разпознава езика на a1 ИЛИ езика на a2
    public static Automaton union(Automaton a1, Automaton a2, String newId) {
        Automaton result = new Automaton(newId); // Създаваме новия резултатен автомат

        State newStart = new State(generateStateName(), false); // Генерираме ново общо начално състояние
        State newAccept = new State(generateStateName(), true); // Генерираме ново общо финално състояние

        result.setStartState(newStart); // Задаваме новото начално състояние на резултата
        result.addState(newStart); // Добавяме го в списъка със състояния
        result.addState(newAccept); // Добавяме и новото финално състояние

        copyAutomatonData(a1, result); // Копираме всички данни от първия автомат
        copyAutomatonData(a2, result); // Копираме всички данни от втория автомат

        // Свързваме новото начално състояние със старите начални чрез Епсилон преходи ('E')
        result.addTransition(newStart, 'E', a1.getStartState());
        result.addTransition(newStart, 'E', a2.getStartState());

        // Обхождаме всички състояния в новия сглобен автомат
        for (State s : result.getStates()) {
            // Ако състоянието е било финално в старите автомати (и не е новосъздаденото финално)
            if (s.isAccepting() && !s.equals(newAccept)) {
                result.addTransition(s, 'E', newAccept); // Свързваме го с новото финално чрез Епсилон
                s.setAccepting(false); // Премахваме стария му статус на финално състояние
            }
        }
        return result; // Връщаме готовия обединен автомат
    }

    // 2. Конкатенация (Concatenation): a1 последван от a2
    public static Automaton concat(Automaton a1, Automaton a2, String newId) {
        Automaton result = new Automaton(newId); // Създаваме нов празен автомат

        copyAutomatonData(a1, result); // Вмъкваме първия автомат
        copyAutomatonData(a2, result); // Вмъкваме втория автомат

        result.setStartState(a1.getStartState()); // Началното състояние на общия автомат е началното на първия

        // Обхождаме състоянията само на първия автомат
        for (State s : a1.getStates()) {
            if (s.isAccepting()) { // Търсим неговите финални състояния
                result.addTransition(s, 'E', a2.getStartState()); // Свързваме ги с началното на втория чрез Епсилон
                s.setAccepting(false); // Те спират да бъдат финални за общия автомат
            }
        }
        // Забележка: Финалните състояния на a2 си остават финални за целия автомат, няма нужда от логика за тях
        return result;
    }

    // 3. Звезда на Клини (Kleene Star): Повторение на автомат нула или повече пъти
    public static Automaton kleene(Automaton a, String newId) {
        Automaton result = new Automaton(newId); // Създаваме контейнер за новия автомат

        State newStart = new State(generateStateName(), false); // Генерираме ново начално състояние
        State newAccept = new State(generateStateName(), true); // Генерираме ново финално състояние

        result.setStartState(newStart); // Инициализираме старта
        result.addState(newStart);
        result.addState(newAccept);

        copyAutomatonData(a, result); // Копираме оригиналния автомат вътре

        // Новото начално сочи към старото начално (позволява влизане в автомата за 1+ повторения)
        result.addTransition(newStart, 'E', a.getStartState());
        // Новото начално сочи към новото финално (позволява заобикаляне на автомата за празна дума / 0 повторения)
        result.addTransition(newStart, 'E', newAccept);

        // Обхождаме сглобения автомат
        for (State s : result.getStates()) {
            if (s.isAccepting() && !s.equals(newAccept)) { // Намираме старите финални състояния
                result.addTransition(s, 'E', a.getStartState()); // Връщаме ги към стария старт (създава цикъл за повторение)
                result.addTransition(s, 'E', newAccept); // Пращаме ги и към новия финал (за изход след успешно прочитане)
                s.setAccepting(false); // Те губят статуса си на финални
            }
        }
        return result; // Връщаме обработения автомат
    }
}