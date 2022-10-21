package src.main.java.org.grynko.nazar.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TokenPattern {

    SUCCESS("Success \\([a-zA-Z0-9]+\\), result: \\[[0-9]+\\.?[0-9]?\\]"),
    SOFT_CALCULATE_FAILURE("Soft calculate failure \\([a-zA-Z0-9]+\\), calculate attempt: \\[[0-9]+\\.?[0-9]?\\] of [0-9]+\\.?[0-9]?"),
    HARD_CALCULATE_FAILURE ("Hard calculate failure \\([a-zA-Z0-9]+\\): function cannot be calculated with this parameter \\([0-9]+\\.?[0-9]?\\)"),
    HARD_ATTEMPTS_FAILURE ("Hard calculate failure \\([a-zA-Z0-9]+\\): the limit of attempts \\([0-9]+\\.?[0-9]?\\) is exceeded with this parameter \\([0-9]+\\.?[0-9]?\\)"),
    HARD_EXTERNAL_FAILURE ("Hard failure external causes \\([a-zA-Z0-9]+\\): [a-zA-Z0-9]+"),
    RESULT("\\[[0-9]+\\.?[0-9]*\\]");

    private final String value;

    TokenPattern(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static void main(String[] args) {
        TokenPattern tp = RESULT;
        String message = "Success (F), result: [3]";

        Pattern p = Pattern.compile(tp.getValue());
        Matcher m = p.matcher(message);

        System.out.println(m.find());
        System.out.println(m.group());
        String tmp = m.group();

        System.out.println(tmp);
        tmp = m.group().replace('[', ' ').replace(']', ' ').trim();
        System.out.println(tmp);
    }

}
