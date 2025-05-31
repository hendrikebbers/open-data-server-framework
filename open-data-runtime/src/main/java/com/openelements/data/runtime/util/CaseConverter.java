package com.openelements.data.runtime.util;

public class CaseConverter {

    public static String toUpperSnakeCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        final StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isUpperCase(c) && i > 0 &&
                    (Character.isLowerCase(chars[i - 1]) || Character.isDigit(chars[i - 1]))) {
                result.append('_');
            }
            result.append(Character.toUpperCase(c));
        }
        return result.toString();
    }
}
