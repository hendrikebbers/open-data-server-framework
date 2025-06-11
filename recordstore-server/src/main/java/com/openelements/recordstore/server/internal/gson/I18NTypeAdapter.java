package com.openelements.recordstore.server.internal.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.openelements.data.runtime.api.Language;
import com.openelements.data.runtime.api.types.I18nString;
import java.io.IOException;
import java.util.Optional;

public class I18NTypeAdapter extends TypeAdapter<I18nString> {

    private final Language language;

    public I18NTypeAdapter(Language language) {
        this.language = language;
    }

    @Override
    public void write(JsonWriter out, I18nString value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            final String message = Optional.ofNullable(value.translations().get(language))
                    .orElseGet(() -> value.translations().get(Language.EN));
            out.value(message);
        }
    }

    @Override
    public I18nString read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("Reading datatype from JSON is not supported");
    }
}
