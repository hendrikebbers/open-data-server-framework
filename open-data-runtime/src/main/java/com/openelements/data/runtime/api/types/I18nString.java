package com.openelements.data.runtime.api.types;

import com.openelements.data.runtime.api.Language;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record I18nString(Map<Language, String> translations) {

    public I18nString(@NonNull final Map<Language, String> translations) {
        Objects.requireNonNull(translations, "translations must be null");
        this.translations = Collections.unmodifiableMap(translations);
        if (translations.isEmpty()) {
            throw new IllegalArgumentException("translations must not be empty");
        }
        if (!translations.containsKey(Language.EN)) {
            throw new IllegalArgumentException("translations must not contain EN");
        }
    }

    @NonNull
    public static I18nString of(@NonNull final String english) {
        Objects.requireNonNull(english, "english must be null");
        return new I18nString(Map.of(Language.EN, english));
    }

    @NonNull
    public String resolve(@NonNull final Language language) {
        Objects.requireNonNull(language, "language must be null");
        final String translation = translations.get(language);
        if (translation != null) {
            return translation;
        }
        // Fallback to English if the requested language is not available
        return translations.get(Language.EN);
    }
}
