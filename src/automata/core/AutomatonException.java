package automata.core;

/**
 * Потребителско проверявано (checked) изключение за грешки, свързани с
 * операции над крайни автомати.
 *
 * <p>Тъй като наследява {@link Exception}, а не {@link RuntimeException},
 * компилаторът изисква явна обработка чрез {@code try-catch} или деклариране
 * с {@code throws}. Използва се при невалиден потребителски вход, липсващ
 * автомат и други предвидими грешки по време на изпълнение.</p>
 */
public class AutomatonException extends Exception {

    /**
     * Създава ново изключение със зададено съобщение за грешка.
     *
     * @param message описание на грешката
     */
    public AutomatonException(String message) {
        super(message);
    }

    /**
     * Създава ново изключение със съобщение и оригинална причина.
     *
     * @param message описание на грешката
     * @param cause   изключението, което е предизвикало тази грешка
     */
    public AutomatonException(String message, Throwable cause) {
        super(message, cause);
    }
}