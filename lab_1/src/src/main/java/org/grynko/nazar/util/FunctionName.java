package src.main.java.org.grynko.nazar.util;

public enum FunctionName {

    F ("F"),
    G ("G");

    private final String name;

    FunctionName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
