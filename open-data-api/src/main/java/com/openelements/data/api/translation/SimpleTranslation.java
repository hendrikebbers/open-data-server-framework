package com.openelements.data.api.translation;


import com.openelements.data.api.data.Language;

public record SimpleTranslation(TranslationType type, String key, Language language, String text) implements
        Translation {
}
