package automata.core;

import automata.core.operations.ConcatOperation;
import automata.core.operations.KleeneOperation;
import automata.core.operations.UnionOperation;
import automata.model.State;
import java.util.Stack;

/**
 * Парсва регулярен израз и го преобразува в краен автомат.
 *
 * <p>Работи в три фази: (1) вмъкване на изрични оператори за конкатенация,
 * (2) преобразуване от инфиксен в постфиксен запис чрез алгоритъма
 * Shunting-yard на Дийкстра, и (3) сглобяване на автомата от постфиксния
 * израз по конструкцията на Томпсън. За самите операции използва
 * специализираните класове от пакета {@code automata.core.operations}.</p>
 */
public class RegexParser {

    /** Операция звезда на Клини, прилагана при символ '*'. */
    private static final KleeneOperation KLEENE = new KleeneOperation();

    /** Операция конкатенация, прилагана при символ '.'. */
    private static final ConcatOperation CONCAT = new ConcatOperation();

    /** Операция обединение, прилагана при символ '|'. */
    private static final UnionOperation UNION  = new UnionOperation();

    /**
     * Вмъква изричен символ за конкатенация ('.') между съседни операнди.
     *
     * <p>Например низът "ab" се преобразува в "a.b", за да може парсерът да
     * разпознае имплицитната конкатенация като явна операция.</p>
     *
     * @param regex регулярният израз
     * @return изразът с вмъкнати изрични оператори за конкатенация
     */
    private static String insertExplicitConcat(String regex) {
        StringBuilder res = new StringBuilder(); // Използваме StringBuilder за по-ефективно конструиране на низа

        // Обхождаме всички символи в израза
        for (int i = 0; i < regex.length(); i++) {
            char c1 = regex.charAt(i); // Взимаме текущия символ
            res.append(c1); // Добавяме го към резултата

            // Ако не сме на последния символ, проверяваме дали трябва да сложим '.' между текущия и следващия
            if (i + 1 < regex.length()) {
                char c2 = regex.charAt(i + 1); // Взимаме следващия символ

                // Конкатенация се случва между: (буква, ')' или '*') И (буква или '(')
                boolean c1IsNormal = Character.isLetterOrDigit(c1) || c1 == ')' || c1 == '*';
                boolean c2IsNormal = Character.isLetterOrDigit(c2) || c2 == '(';

                if (c1IsNormal && c2IsNormal) {
                    res.append('.'); // Вмъкваме скритата точка за конкатенация
                }
            }
        }
        return res.toString(); // Връщаме форматирания низ
    }

    /**
     * Връща приоритета на даден оператор в регулярния израз.
     *
     * @param c символът на оператора
     * @return 3 за '*', 2 за '.', 1 за '|', 0 за всичко останало
     */
    private static int precedence(char c) {
        switch (c) {
            case '*': return 3; // Най-висок приоритет: Звезда на Клини
            case '.': return 2; // Среден приоритет: Конкатенация
            case '|': return 1; // Най-нисък приоритет: Обединение
            default: return 0;  // Базови символи (букви и скоби)
        }
    }

    /**
     * Преобразува инфиксен регулярен израз в постфиксен (обратен полски запис)
     * чрез алгоритъма Shunting-yard.
     *
     * @param regex инфиксният регулярен израз, например "a|b"
     * @return еквивалентният постфиксен израз, например "ab|"
     */
    private static String toPostfix(String regex) {
        String formattedRegEx = insertExplicitConcat(regex); // Първо добавяме скритите точки
        StringBuilder postfix = new StringBuilder(); // Тук ще трупаме крайния резултат
        Stack<Character> stack = new Stack<>(); // Стек за временно съхранение на операторите

        // Обхождаме форматирания низ символ по символ
        for (char c : formattedRegEx.toCharArray()) {
            switch (c) {
                case '(':
                    stack.push(c); // Отварящата скоба винаги влиза в стека
                    break;
                case ')':
                    // При затваряща скоба вадим от стека всички оператори докато не стигнем отварящата
                    while (!stack.isEmpty() && stack.peek() != '(') {
                        postfix.append(stack.pop());
                    }
                    stack.pop(); // Премахваме самата отваряща скоба от стека
                    break;
                case '*':
                case '.':
                case '|':
                    // Ако дойде оператор, вадим от стека тези с по-висок или равен приоритет и ги добавяме към резултата
                    while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(c)) {
                        postfix.append(stack.pop());
                    }
                    stack.push(c); // Добавяме текущия оператор в стека
                    break;
                default:
                    // Ако е обикновена буква/цифра, отива директно в резултата (операнд)
                    postfix.append(c);
                    break;
            }
        }
        // Накрая изпразваме останалите оператори от стека
        while (!stack.isEmpty()) {
            postfix.append(stack.pop());
        }
        return postfix.toString(); // Връщаме израза в постфиксен формат (Обратен полски запис)
    }

    /**
     * Построява краен автомат от подаден регулярен израз.
     *
     * @param regex регулярният израз
     * @param id    идентификаторът на новия автомат
     * @return автомат, разпознаващ езика на регулярния израз
     */
    public static Automaton createFromRegex(String regex, String id) {
        String postfix = toPostfix(regex); // Първо преобразуваме израза в удобен за машината постфиксен формат
        Stack<Automaton> stack = new Stack<>(); // Стек, в който ще държим временните автомати, докато ги сглобяваме
        int counter = 0; // Брояч за генериране на уникални имена на временните автомати

        // Обхождаме постфиксния израз (където операторите са СЛЕД операндите)
        for (char c : postfix.toCharArray()) {
            if (c == '*') {
                // При Звезда изваждаме 1 автомат от стека и го завъртаме
                Automaton a = stack.pop();
                stack.push(KLEENE.apply(a, "temp" + (counter++)));
            } else if (c == '.') {
                // При Конкатенация изваждаме 2 автомата и ги залепяме (първо вадим десния, после левия!)
                Automaton a2 = stack.pop();
                Automaton a1 = stack.pop();
                stack.push(CONCAT.apply(a1, a2, "temp" + (counter++)));
            } else if (c == '|') {
                // При Обединение изваждаме 2 автомата и ги обединяваме успоредно
                Automaton a2 = stack.pop();
                Automaton a1 = stack.pop();
                stack.push(UNION.apply(a1, a2, "temp" + (counter++)));
            } else {
                // Базов случай: Ако е буква, създаваме мини-автомат само с 2 състояния (начално и крайно) и преход с тази буква
                Automaton a = new Automaton("temp" + (counter++));
                State s1 = new State("start_" + c + counter, false); // Създаваме начално състояние
                State s2 = new State("end_" + c + counter, true);   // Създаваме финално състояние
                a.addState(s1);
                a.addState(s2);
                a.setStartState(s1);
                a.addTransition(s1, c, s2); // Свързваме ги с текущата буква
                stack.push(a); // Бутаме базовия автомат в стека за по-нататъшна обработка
            }
        }

        // Накрая в стека остава точно 1 автомат – пълният сглобен резултат
        Automaton result = stack.pop();
        result.setId(id); // Задаваме му името, което потребителят е поискал
        return result; // Връщаме готовия автомат
    }
}