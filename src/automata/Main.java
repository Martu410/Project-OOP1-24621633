package automata;

import automata.manager.AutomatonManager;
import automata.core.Automaton;
import java.util.Scanner;

// Главен клас, съдържащ входната точка на програмата и конзолното меню (REPL цикъл)
public class Main {
    public static void main(String[] args) {
        // Създаваме обект Scanner за четене на вход от потребителя чрез конзолата (System.in)
        Scanner scanner = new Scanner(System.in);
        // Създаваме инстанция на мениджъра, който ще съхранява създадените автомати по време на сесията
        AutomatonManager manager = new AutomatonManager();

        // Отпечатваме приветствени съобщения при стартиране на програмата
        System.out.println("=== Система за Крайни Автомати ===");
        System.out.println("Въведете команда (или 'help' за списък, 'exit' за изход):");

        // Безкраен цикъл за интерактивно въвеждане на команди
        while (true) {
            System.out.print("> "); // Индикатор за чакащо въвеждане от потребителя
            String input = scanner.nextLine().trim(); // Четем целия ред и премахваме излишните интервали в началото и края

            // Проверка дали потребителят е въвел команда за изход
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Излизане от програмата...");
                break; // Прекъсваме безкрайния цикъл, което води до край на програмата
            }

            // Ако потребителят е натиснал само Enter (празен низ), пропускаме тази итерация на цикъла
            if (input.isEmpty()) continue;

            // Разделяме входа на масив от думи, използвайки един или повече интервали като разделител
            String[] parts = input.split("\\s+");
            // Извличаме първата дума, която представлява самата команда, и я правим с малки букви
            String command = parts[0].toLowerCase();

            // Изпълняваме различна логика в зависимост от въведената команда
            switch (command) {
                case "help":
                    // Извеждаме списък с всички налични команди и тяхното описание
                    System.out.println("Налични команди до момента:");
                    System.out.println("  list                     - Показва всички заредени автомати");
                    System.out.println("  empty <id>               - Проверява дали езикът на автомата е празен");
                    System.out.println("  recognize <id> <word>    - Проверява дали дума се разпознава");
                    System.out.println("  exit                     - Изход от програмата");
                    break;

                case "list":
                    // Извикваме метода от мениджъра за отпечатване на списъка с автомати
                    manager.listAutomata();
                    break;

                case "empty":
                    // Проверяваме дали потребителят е подал необходимия аргумент (ID на автомат)
                    if (parts.length < 2) {
                        System.out.println("Грешка: Моля, въведете ID.");
                    } else {
                        // Извличаме автомата от мениджъра по даденото ID
                        Automaton a = manager.getAutomaton(parts[1]);
                        if (a != null) { // Проверяваме дали автоматът реално съществува в паметта
                            // Извикваме метода isEmpty() и отпечатваме съответния резултат
                            if (a.isEmpty()) System.out.println("Езикът на автомата е ПРАЗЕН.");
                            else System.out.println("Езикът на автомата НЕ Е празен.");
                        } else {
                            System.out.println("Автомат не е намерен.");
                        }
                    }
                    break;

                case "recognize":
                    // Проверяваме дали има достатъчно аргументи (команда, ID, дума за разпознаване)
                    if (parts.length < 3) {
                        System.out.println("Грешка: Форматът е 'recognize <id> <word>'");
                    } else {
                        // Намираме автомата в мениджъра
                        Automaton a = manager.getAutomaton(parts[1]);
                        if (a != null) {
                            String word = parts[2]; // Извличаме целевата дума от входа
                            // Извикваме метода recognize() за да проверим думата
                            if (a.recognize(word)) System.out.println("Думата '" + word + "' се РАЗПОЗНАВА.");
                            else System.out.println("Думата '" + word + "' НЕ СЕ разпознава.");
                        } else {
                            System.out.println("Автомат не е намерен.");
                        }
                    }
                    break;

                default:
                    // Съобщение при въвеждане на невалидна команда
                    System.out.println("Непозната команда. Напишете 'help'.");
            }
        }
        // Затваряме скенера, за да освободим системните ресурси след края на програмата
        scanner.close();
    }
}