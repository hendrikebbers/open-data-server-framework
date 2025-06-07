package com.openelements.recordstore.server.internal.gson;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.openelements.data.runtime.api.Language;
import com.openelements.data.runtime.data.DataAttribute;
import com.openelements.data.runtime.data.DataType;
import com.openelements.recordstore.server.internal.PathResolver;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class JsonFactory {

    private final Map<Class<?>, TypeAdapter<?>> typeAdapterMap;

    private final PathResolver pathResolver;

    public JsonFactory(Set<DataType<?>> dataTypes, PathResolver pathResolver) {
        typeAdapterMap = new java.util.HashMap<>();
        dataTypes.stream().forEach(dataType -> {
            typeAdapterMap.put(dataType.dataClass(), createTypeAdapter(dataType));
        });
        this.pathResolver = pathResolver;
    }

    public <E extends Record> JsonElement createJsonObject(E entry, Class<E> dataType, Language requestedLanguage) {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new TypeAdapterFactoryImpl(typeAdapterMap, requestedLanguage, pathResolver))
                .create().toJsonTree(entry);
    }

    private <E extends Record> TypeAdapter<E> createTypeAdapter(DataType<E> dataType) {
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
