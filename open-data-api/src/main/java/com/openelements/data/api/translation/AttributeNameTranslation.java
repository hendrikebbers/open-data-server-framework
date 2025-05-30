package com.openelements.data.api.translation;

import com.openelements.data.api.data.Language;
import java.lang.reflect.RecordComponent;

public record AttributeNameTranslation(Class<? extends Record> dataType,
                                       RecordComponent attribute,
                                       Language language,
                                       String text) implements Translation {

    @Override
    public TranslationType type() {
        return TranslationType.ATTRIBUTE_NAME;
    }

    @Override
    public String key() {
        return TranslationKeyFactory.getKeyForAttributeDescription(dataType, attribute);
    }
}