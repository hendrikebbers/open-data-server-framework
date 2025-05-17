package com.openelements.data.api.translation;


import com.openelements.data.api.data.Language;

public record DataTypeDescriptionTranslation(Class<? extends Record> dataType,
                                             Language language,
                                             String text) implements Translation {

    @Override
    public TranslationType type() {
        return TranslationType.DATA_DESCRIPTION;
    }

    @Override
    public String key() {
        return TranslationKeyFactory.getKeyForDataTypeDescription(dataType);
    }
}