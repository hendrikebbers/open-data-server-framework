package com.openelements.data.data;

public enum Language {
    DE, EN;

    public static Language fromString(String languageHeader) {
        if (languageHeader == null) {
            return null;
        }
        String[] parts = languageHeader.split(",");
        for (String part : parts) {
            String trimmedPart = part.trim();
            if (trimmedPart.equalsIgnoreCase("de")) {
                return DE;
            } else if (trimmedPart.equalsIgnoreCase("en")) {
                return EN;
            }
        }
        return EN;
    }
}
