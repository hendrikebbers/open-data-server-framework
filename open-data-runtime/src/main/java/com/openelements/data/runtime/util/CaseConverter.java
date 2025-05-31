package com.openelements.data.runtime.util;

import org.jspecify.annotations.Nullable;

public class CaseConverter {

    @Nullable
    public static String toUpperSnakeCase(@Nullable final String input) {
        if (input == null || input.isBlank()) {
            return input;
        }
        final StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            final char c = chars[i];
            if (Character.isUpperCase(c) && i > 0 &&
                    (Character.isLowerCase(chars[i - 1]) || Character.isDigit(chars[i - 1]))) {
                result.append('_');
            }
            result.append(Character.toUpperCase(c));
        }
        return result.toString();
    }
}
