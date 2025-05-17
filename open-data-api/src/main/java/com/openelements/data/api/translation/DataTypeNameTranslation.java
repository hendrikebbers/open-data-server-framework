package com.openelements.data.api.translation;


import com.openelements.data.api.data.Language;

public record DataTypeNameTranslation(Class<? extends Record> dataType,
                                      Language language,
                                      String text) implements Translation {

    @Override
    public TranslationType type() {
        return TranslationType.DATA_NAME;
    }

    @Override
    public String key() {
        return TranslationKeyFactory.getKeyForDataTypeName(dataType);
    }
}
