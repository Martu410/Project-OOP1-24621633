package automata;

import automata.core.Automaton;
import automata.core.AutomatonException;
import automata.core.AutomatonOperations;
import automata.core.RegexParser;
import automata.manager.AutomatonManager;
import automata.manager.FileHandler; // ВАЖНО: Добавихме импорта за работа с файлове

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AutomatonManager manager = new AutomatonManager(); // Създаваме си мениджъра за паметта
        boolean isRunning = true; // Флаг за цикъла

        System.out.println("=== Система за Крайни Автомати (НКА/ДКА) ===");
        System.out.println("Напишете 'help' за списък с команди.");

        // Безкраен цикъл, докато не напишем "exit"
        while (isRunning) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue; // Ако ударим само Enter, нищо не правим

            // Разделяме входа по интервали. parts[0] е командата, parts[1] е първият аргумент и т.н.
            String[] parts = input.split("\\s+");
            String command = parts[0].toLowerCase();

            // Слагаме всичко в try-catch, за да хващаме грешките (напр. ако търсим липсващ автомат)
            try {
                switch (command) {
                    case "help":
                        System.out.println("Налични команди:");
                        System.out.println("  list                     - Списък на всички автомати");
                        System.out.println("  print <id>               - Извежда всички преходи на автомата");
                        System.out.println("  reg <regex> <id>         - Създава автомат от регулярен израз");
                        System.out.println("  recognize <id> <word>    - Проверява дали дума се разпознава");
                        System.out.println("  empty <id>               - Проверява дали езикът е празен");
                        System.out.println("  deterministic <id>       - Проверява дали автоматът е ДКА");
                        System.out.println("  finite <id>              - Проверява дали езикът е краен");
                        System.out.println("  union <id1> <id2> <new>  - Обединение на два автомата");
                        System.out.println("  concat <id1> <id2> <new> - Конкатенация на два автомата");
                        System.out.println("  kleene <id> <new>        - Звезда на Клини (повторение)");
                        System.out.println("  un <id> <new>            - Позитивна обвивка (Kleene Plus)");
                        System.out.println("  determinize <id> <new>   - Превръща НКА в ДКА");
                        System.out.println("  save <filename>          - Запазва автоматите във файл");
                        System.out.println("  open <filename>          - Зарежда автомати от файл");
                        System.out.println("  exit                     - Изход от програмата");
                        break;

                    case "list":
                        if (manager.getAllAutomata().isEmpty()) {
                            System.out.println("Няма заредени автомати.");
                        } else {
                            System.out.println("Заредени автомати:");
                            for (String key : manager.getAllAutomata().keySet()) System.out.println(" - " + key);
                        }
                        break;

                    case "print":
                        if (parts.length < 2) throw new AutomatonException("Формат: print <id>");
                        Automaton autPrint = getRequired(manager, parts[1]);
                        autPrint.printInfo();
                        break;

                    case "reg":
                        if (parts.length < 3) throw new AutomatonException("Формат: reg <regex> <id>");
                        Automaton regAut = RegexParser.createFromRegex(parts[1], parts[2]);
                        manager.addAutomaton(regAut);
                        System.out.println("Създаден автомат: " + parts[2]);
                        break;

                    case "recognize":
                        if (parts.length < 3) throw new AutomatonException("Формат: recognize <id> <word>");
                        String word = parts[2].equals("\"\"") ? "" : parts[2];
                        Automaton autRec = getRequired(manager, parts[1]);
                        boolean accepted = autRec.recognize(word);
                        System.out.println("Думата " + (accepted ? "СЕ РАЗПОЗНАВА" : "НЕ СЕ РАЗПОЗНАВА"));
                        break;

                    case "empty":
                        if (parts.length < 2) throw new AutomatonException("Формат: empty <id>");
                        Automaton autEmpty = getRequired(manager, parts[1]);
                        System.out.println("Езикът на автомата " + (autEmpty.isEmpty() ? "Е празен." : "НЕ Е празен."));
                        break;

                    case "deterministic":
                        if (parts.length < 2) throw new AutomatonException("Формат: deterministic <id>");
                        Automaton autDet = getRequired(manager, parts[1]);
                        System.out.println("Автоматът " + (autDet.isDeterministic() ? "Е детерминиран (ДКА)." : "Е недетерминиран (НКА)."));
                        break;

                    case "finite":
                        if (parts.length < 2) throw new AutomatonException("Формат: finite <id>");
                        Automaton autFin = getRequired(manager, parts[1]);
                        System.out.println("Езикът на автомата е " + (autFin.isFinite() ? "КРАЕН." : "БЕЗКРАЕН."));
                        break;

                    case "union":
                        if (parts.length < 4) throw new AutomatonException("Формат: union <id1> <id2> <new_id>");
                        Automaton u1 = getRequired(manager, parts[1]);
                        Automaton u2 = getRequired(manager, parts[2]);
                        manager.addAutomaton(AutomatonOperations.union(u1, u2, parts[3]));
                        System.out.println("Обединен автомат: " + parts[3]);
                        break;

                    case "concat":
                        if (parts.length < 4) throw new AutomatonException("Формат: concat <id1> <id2> <new_id>");
                        Automaton c1 = getRequired(manager, parts[1]);
                        Automaton c2 = getRequired(manager, parts[2]);
                        manager.addAutomaton(AutomatonOperations.concat(c1, c2, parts[3]));
                        System.out.println("Конкатениран автомат: " + parts[3]);
                        break;

                    case "kleene":
                        if (parts.length < 3) throw new AutomatonException("Формат: kleene <id> <new_id>");
                        Automaton k1 = getRequired(manager, parts[1]);
                        manager.addAutomaton(AutomatonOperations.kleene(k1, parts[2]));
                        System.out.println("Звезда на Клини приложена: " + parts[2]);
                        break;

                    case "un":
                        if (parts.length < 3) throw new AutomatonException("Формат: un <id> <new_id>");
                        Automaton un1 = getRequired(manager, parts[1]);
                        manager.addAutomaton(AutomatonOperations.positiveKleene(un1, parts[2]));
                        System.out.println("Позитивна обвивка (un) приложена: " + parts[2]);
                        break;

                    case "determinize":
                        if (parts.length < 3) throw new AutomatonException("Формат: determinize <id> <new_id>");
                        Automaton d1 = getRequired(manager, parts[1]);
                        manager.addAutomaton(AutomatonOperations.determinize(d1, parts[2]));
                        System.out.println("Автоматът е конвертиран в ДКА: " + parts[2]);
                        break;

                    case "save":
                        if (parts.length < 2) throw new AutomatonException("Формат: save <filename>");
                        FileHandler.save(manager, parts[1]);
                        break;

                    case "open":
                        if (parts.length < 2) throw new AutomatonException("Формат: open <filename>");
                        FileHandler.open(manager, parts[1]);
                        break;

                    case "exit":
                        System.out.println("Изход...");
                        isRunning = false;
                        break;

                    default:
                        System.out.println("Непозната команда. Напишете 'help'.");
                        break;
                }
            } catch (AutomatonException e) {
                System.out.println("Грешка: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Неочаквана грешка: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static Automaton getRequired(AutomatonManager manager, String id) throws AutomatonException {
        Automaton a = manager.getAutomaton(id);
        if (a == null) {
            throw new AutomatonException("Автомат '" + id + "' не съществува в паметта.");
        }
        return a;
    }
}