package com.openelements.data.api.translation;


import com.openelements.data.api.data.Language;

public interface Translation {

    TranslationType type();

    String key();

    Language language();

    String text();
}
