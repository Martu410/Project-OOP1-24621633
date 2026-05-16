package automata.core;

// Интерфейс, който дефинира основните операции за разпознаване на език
public interface Recognizable {
    // Дефиниция на метод за проверка дали дадена дума се разпознава от автомата
    boolean recognize(String word);
    // Дефиниция на метод за проверка дали езикът на автомата е празен
    boolean isEmpty();
}