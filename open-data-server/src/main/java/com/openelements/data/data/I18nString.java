package com.openelements.data.data;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public record I18nString(Map<Language, String> translations) {

    public I18nString(Map<Language, String> translations) {
        Objects.requireNonNull(translations, "translations must be null");
        this.translations = Collections.unmodifiableMap(translations);
        if (translations.isEmpty()) {
            throw new IllegalArgumentException("translations must not be empty");
        }
        if (!translations.containsKey(Language.EN)) {
            throw new IllegalArgumentException("translations must not contain EN");
        }
    }

    public static I18nString of(String english) {
        return new I18nString(Map.of(Language.EN, english));
    }

    public String resolve(Language language) {
        Objects.requireNonNull(language, "language must be null");
        final String translation = translations.get(language);
        if (translation != null) {
            return translation;
        }
        // Fallback to English if the requested language is not available
        return translations.get(Language.EN);
    }
}
