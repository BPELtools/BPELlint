package bpellint.core.validators.rules.infos;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class EnvironmentVariableInterpreter {

    public static final String BPEL_LINT_SA_RULES_ENVIRONMENT_VARIABLE = "BPEL_LINT_SA_RULES";
    private static final Integer[] ALL_SA_RULES = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36,
            37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
            60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81,
            82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95};

    public List<Integer> getRulesToValidate() {
        String variable;
        try {
            variable = System.getProperty(BPEL_LINT_SA_RULES_ENVIRONMENT_VARIABLE);
        } catch (NullPointerException e) {
            variable = System.getenv(BPEL_LINT_SA_RULES_ENVIRONMENT_VARIABLE);
        }

        List<Integer> rulesToAnalyze = interpretRulesToValidate(variable);
        return Collections.unmodifiableList(rulesToAnalyze);
    }

    private List<Integer> interpretRulesToValidate(String variable) {
        if (variable == null || variable.trim().isEmpty()) {
            variable = "STABLE"; // default case
        }

        if ("ALL".equals(variable.toUpperCase())) {
            return getAllRules();
        } else if ("STABLE".equals(variable.toUpperCase())) {
            List<Integer> allRules = getAllRules();
            allRules.removeAll(experimental());
            return allRules;
        } else if ("EXPERIMENTAL".equals(variable.toUpperCase())) {
            return experimental();
        } else {
            return parseCommaSeparatedRules(variable);
        }
    }

    private List<Integer> experimental() {
        //TODO SA60 does not handle joinConditions
        //TODO SA21 does not handle varprop:query see issue #14
        return Arrays.asList(21, 60);
    }

    private List<Integer> getAllRules() {
        return new LinkedList<>(Arrays.asList(ALL_SA_RULES));
    }

    private List<Integer> parseCommaSeparatedRules(String variable) {
        List<Integer> ruleNumbers = new LinkedList<>();
        for (String string : variable.split(",")) {
            try {
                Integer ruleNumber = Integer.valueOf(string.trim());
                ruleNumbers.add(ruleNumber);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Variable " + BPEL_LINT_SA_RULES_ENVIRONMENT_VARIABLE
                        + "is not set properly. Got [" + variable
                        + "] but expected something like [61,10,63].", e);
            }
        }
        return ruleNumbers;
    }

}
