package automata.core;

import automata.model.State;
import java.util.Stack;

// Клас, отговарящ за парсването на регулярни изрази и превръщането им в крайни автомати
public class RegexParser {

    // Помощен метод, който вмъква изричен символ за конкатенация ('.') между съседни символи.
    // Напр. превръща "ab" в "a.b", за да може парсерът лесно да разпознае операцията.
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

    // Определя приоритета на математическите операции в регулярния израз
    private static int precedence(char c) {
        switch (c) {
            case '*': return 3; // Най-висок приоритет: Звезда на Клини
            case '.': return 2; // Среден приоритет: Конкатенация
            case '|': return 1; // Най-нисък приоритет: Обединение
            default: return 0;  // Базови символи (букви и скоби)
        }
    }

    // Алгоритъм на Shunting-yard за преобразуване на инфиксен израз (напр. "a|b") в постфиксен (напр. "ab|")
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

    // Главен метод, който чете регулярния израз и построява крайния автомат
    public static Automaton createFromRegex(String regex, String id) {
        String postfix = toPostfix(regex); // Първо преобразуваме израза в удобен за машината постфиксен формат
        Stack<Automaton> stack = new Stack<>(); // Стек, в който ще държим временните автомати, докато ги сглобяваме
        int counter = 0; // Брояч за генериране на уникални имена на временните автомати

        // Обхождаме постфиксния израз (където операторите са СЛЕД операндите)
        for (char c : postfix.toCharArray()) {
            if (c == '*') {
                // При Звезда изваждаме 1 автомат от стека и го завъртаме
                Automaton a = stack.pop();
                stack.push(AutomatonOperations.kleene(a, "temp" + (counter++)));
            } else if (c == '.') {
                // При Конкатенация изваждаме 2 автомата и ги залепяме (първо вадим десния, после левия!)
                Automaton a2 = stack.pop();
                Automaton a1 = stack.pop();
                stack.push(AutomatonOperations.concat(a1, a2, "temp" + (counter++)));
            } else if (c == '|') {
                // При Обединение изваждаме 2 автомата и ги обединяваме успоредно
                Automaton a2 = stack.pop();
                Automaton a1 = stack.pop();
                stack.push(AutomatonOperations.union(a1, a2, "temp" + (counter++)));
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