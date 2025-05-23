package com.openelements.data.server.internal;

import com.google.gson.JsonElement;
import com.openelements.data.api.data.Language;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class JsonFactory {

    private String toJson(TemporalAccessor temporalAccessor) {
        return DateTimeFormatter.ISO_DATE_TIME.format(temporalAccessor);
    }

    public <E extends Record> JsonElement createJsonObject(E entry, Class<E> dataType, Language requestedLanguage) {
        return null;
    }

}
