package src.main.java.org.grynko.nazar.util;

public enum Token {

    SUCCESS ("Success (%s), result: [%d]"),
    SOFT_CALCULATE_FAILURE ("Soft calculate failure (%s), calculate attempt: [%d] of %d"),
    HARD_CALCULATE_FAILURE ("Hard calculate failure (%s): function cannot be calculated with this parameter (%d)"),
    HARD_ATTEMPTS_FAILURE ("Hard calculate failure (%s): the limit of attempts (%d) is exceeded with this parameter (%d)"),
    HARD_EXTERNAL_FAILURE ("Hard failure external causes (%s): %s");

    private final String message;

    Token(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }



}
