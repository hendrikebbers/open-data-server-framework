package com.openelements.data.server.internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.openelements.data.runtime.api.Language;
import com.openelements.data.server.internal.gson.TemporalAccessorTypeAdapter;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class JsonFactory {

    private String toJson(TemporalAccessor temporalAccessor) {
        return DateTimeFormatter.ISO_DATE_TIME.format(temporalAccessor);
    }

    public <E extends Record> JsonElement createJsonObject(E entry, Class<E> dataType, Language requestedLanguage) {
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(TemporalAccessor.class, new TemporalAccessorTypeAdapter())
                .create();
        return gson.toJsonTree(entry);
    }

}
