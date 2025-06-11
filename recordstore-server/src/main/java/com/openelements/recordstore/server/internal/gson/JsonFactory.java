package com.openelements.recordstore.server.internal.gson;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.openelements.data.runtime.api.Language;
import com.openelements.data.runtime.api.types.I18nString;
import com.openelements.data.runtime.data.DataAttribute;
import com.openelements.data.runtime.data.DataType;
import com.openelements.recordstore.server.internal.PathResolver;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class JsonFactory {

    private final Map<Class<?>, Function<Language, TypeAdapter<?>>> typeAdapterMap;

    private final PathResolver pathResolver;

    public JsonFactory(Set<DataType<?>> dataTypes, PathResolver pathResolver) {
        typeAdapterMap = new java.util.HashMap<>();
        dataTypes.stream().forEach(dataType -> {
            typeAdapterMap.put(dataType.dataClass(), l -> createTypeAdapter(dataType, l));
        });
        this.pathResolver = pathResolver;
    }

    public JsonElement createJsonObject(Object entry, Language requestedLanguage) {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new TypeAdapterFactoryImpl(typeAdapterMap, requestedLanguage, pathResolver))
                .create().toJsonTree(entry);
    }

    private static <E extends Record> TypeAdapter<E> createTypeAdapter(DataType<E> dataType,
            final Language requestedLanguage) {
        return new TypeAdapter<E>() {
            @Override
            public void write(com.google.gson.stream.JsonWriter out, E value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    out.beginObject();
                    dataType.attributes().forEach(attribute -> {
                        try {
                            out.name(attribute.name());
                            Object attributeValue = DataAttribute.getFor(value, attribute);
                            if (attributeValue == null) {
                                out.nullValue();
                            } else if (attributeValue instanceof I18nString i18nString) {
                                final String message = Optional.ofNullable(
                                                i18nString.translations().get(requestedLanguage))
                                        .orElseGet(() -> i18nString.translations().get(Language.EN));
                                out.value(message);
                            } else {
                                out.value(attributeValue.toString());
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Error writing attribute " + attribute.name(), e);
                        }
                    });
                    out.endObject();
                }
            }

            @Override
            public E read(com.google.gson.stream.JsonReader in) {
                throw new UnsupportedOperationException("Reading datatype from JSON is not supported");
            }
        };
    }
}
