package automata.manager;

import automata.core.Automaton;
import automata.model.State;
import automata.model.Transition;

import java.io.*;

/**
 * Отговаря за сериализацията и десериализацията на автомати във/от текстов файл.
 *
 * <p>Използва собствен текстов формат, в който всеки автомат се записва като
 * блок от редове: {@code ID:}, {@code STATES:}, {@code START:},
 * {@code TRANSITIONS:}, последван от разделителя {@code ---}. Файловите
 * операции използват конструкцията {@code try-with-resources} за гарантирано
 * освобождаване на ресурсите.</p>
 */
public class FileHandler {

    /**
     * Записва всички автомати от мениджъра в текстов файл.
     *
     * @param manager  мениджърът, чиито автомати ще бъдат записани
     * @param filename името на изходния файл
     */
    public static void save(AutomatonManager manager, String filename) {
        // Използваме try-with-resources за автоматично затваряне на потока (PrintWriter) след приключване
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Взимаме речника с всички автомати и обхождаме само стойностите (самите обекти Automaton)
            for (Automaton a : manager.getAllAutomata().values()) {
                // Записваме ID-то на автомата на първия ред
                writer.println("ID:" + a.getId());

                // Започваме реда със списъка от състояния
                writer.print("STATES:");
                // Обхождаме всяко състояние в текущия автомат
                for (State s : a.getStates()) {
                    // Записваме името му и добавяме (1) ако е финално или (0) ако не е, следвано от запетая
                    writer.print(s.getName() + (s.isAccepting() ? "(1)" : "(0)") + ",");
                }
                writer.println(); // Преминаваме на нов ред във файла

                // Проверяваме дали автоматът има зададено начално състояние
                if (a.getStartState() != null) {
                    // Ако има, записваме името му
                    writer.println("START:" + a.getStartState().getName());
                } else {
                    // Ако няма, изрично записваме null
                    writer.println("START:null");
                }

                // Започваме реда с преходите
                writer.print("TRANSITIONS:");
                // Обхождаме всеки преход в текущия автомат
                for (Transition t : a.getTransitions()) {
                    // Записваме прехода във формат "от,символ,към;"
                    writer.print(t.getFrom().getName() + "," + t.getSymbol() + "," + t.getTo().getName() + ";");
                }
                writer.println(); // Преминаваме на нов ред

                // Записваме разделител "---", за да знаем къде свършва този автомат и започва следващият
                writer.println("---");
            }
            // Извеждаме съобщение за успех в конзолата
            System.out.println("Автоматите са успешно запазени във файл: " + filename);
        } catch (IOException e) {
            // При грешка с файловата система (напр. няма права за писане), прихващаме изключението
            System.out.println("Грешка при запазване: " + e.getMessage());
        }
    }

    /**
     * Чете автомати от текстов файл и ги зарежда в мениджъра.
     *
     * <p>Ако файлът не съществува, методът извежда съобщение и приключва без
     * да хвърля изключение.</p>
     *
     * @param manager  мениджърът, в който ще бъдат заредени автоматите
     * @param filename името на входния файл
     */
    public static void open(AutomatonManager manager, String filename) {
        File file = new File(filename); // Създаваме обект File, за да проверим дали файлът съществува
        // Ако файлът не е намерен на диска
        if (!file.exists()) {
            System.out.println("Файлът не съществува!");
            return; // Прекратяваме изпълнението на метода
        }

        // Използваме BufferedReader за ефективно четене на файла ред по ред
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line; // Променлива за съхранение на текущо прочетения ред
            Automaton current = null; // Временна променлива за автомата, който сглобяваме в момента

            // Четем файла докато не стигнем до края му (null)
            while ((line = reader.readLine()) != null) {
                // Ако редът започва с "ID:", значи започваме нов автомат
                if (line.startsWith("ID:")) {
                    current = new Automaton(line.substring(3)); // Създаваме нов автомат, като изрязваме първите 3 символа ("ID:")
                }
                // Ако редът съдържа състояния и вече имаме създаден автомат
                else if (line.startsWith("STATES:") && current != null) {
                    String statesStr = line.substring(7); // Изрязваме "STATES:"
                    if (!statesStr.isEmpty()) { // Ако има поне едно състояние
                        String[] states = statesStr.split(","); // Разделяме ги по запетая
                        for (String s : states) { // Обхождаме всяко от тях
                            if (s.isEmpty()) continue; // Пропускаме празни елементи
                            boolean isAcc = s.contains("(1)"); // Проверяваме дали съдържа флага за финално състояние
                            String name = s.replace("(1)", "").replace("(0)", ""); // Изчистваме името от флаговете
                            current.addState(new State(name, isAcc)); // Създаваме обекта State и го добавяме в автомата
                        }
                    }
                }
                // Ако редът указва началното състояние
                else if (line.startsWith("START:") && current != null) {
                    String startName = line.substring(6); // Изрязваме "START:"
                    if (!startName.equals("null")) { // Ако не е null
                        for (State s : current.getStates()) { // Търсим това състояние в вече добавените
                            if (s.getName().equals(startName)) { // Ако имената съвпаднат
                                current.setStartState(s); // Задаваме го като начално
                                break; // Прекъсваме търсенето
                            }
                        }
                    }
                }
                // Ако редът съдържа преходите
                else if (line.startsWith("TRANSITIONS:") && current != null) {
                    String transStr = line.substring(12); // Изрязваме "TRANSITIONS:"
                    if (!transStr.isEmpty()) {
                        String[] trans = transStr.split(";"); // Разделяме преходите по точка и запетая
                        for (String t : trans) { // Обхождаме ги
                            if (t.isEmpty()) continue; // Пропускаме празни
                            String[] parts = t.split(","); // Разделяме прехода на: от, символ, към
                            if (parts.length == 3) { // Уверяваме се, че имаме точно 3 части
                                State from = null, to = null; // Временни променливи за състоянията
                                for (State s : current.getStates()) { // Намираме реалните обекти State по техните имена
                                    if (s.getName().equals(parts[0])) from = s;
                                    if (s.getName().equals(parts[2])) to = s;
                                }
                                if (from != null && to != null) { // Ако сме намерили и двете състояния
                                    current.addTransition(from, parts[1].charAt(0), to); // Създаваме прехода
                                }
                            }
                        }
                    }
                }
                // Ако стигнем до разделителя "---"
                else if (line.equals("---") && current != null) {
                    manager.addAutomaton(current); // Добавяме напълно сглобения автомат в мениджъра
                    current = null; // Зануляваме променливата, за да сме готови за следващия автомат
                }
            }
            System.out.println("Автоматите са успешно заредени от файл: " + filename);
        } catch (IOException e) {
            // Прихващаме грешки при четенето
            System.out.println("Грешка при четене: " + e.getMessage());
        }
    }
}