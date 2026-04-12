package automata.model;

public class State {
    private String name;
    private boolean isAccepting;
    public State(String name, boolean isAccepting) {
        this.name = name;
        this.isAccepting = isAccepting;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isAccepting() {
        return isAccepting;
    }
    public void setAccepting(boolean accepting) {
        isAccepting = accepting;
    }
}
