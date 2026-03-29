package automata.core;

public interface Recognizable {
    boolean recognize(String word);
    boolean isEmpty();
}
