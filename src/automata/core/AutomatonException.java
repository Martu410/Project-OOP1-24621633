package automata.core;

// Потребителско изключение за обработка на специфични грешки свързани с автоматите
public class AutomatonException extends Exception {
    // Конструктор, който приема съобщение за грешка
    public AutomatonException(String message) {
        super(message); // Подава съобщението на базовия клас Exception
    }

    // Конструктор, който приема съобщение и оригиналната причина (друго изключение)
    public AutomatonException(String message, Throwable cause) {
        super(message, cause); // Подава ги на базовия клас за проследяване на стека от грешки
    }
}