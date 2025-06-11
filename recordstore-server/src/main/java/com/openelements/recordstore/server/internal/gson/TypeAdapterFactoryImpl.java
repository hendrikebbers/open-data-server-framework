package com.openelements.recordstore.server.internal.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.openelements.data.runtime.api.Language;
import com.openelements.data.runtime.api.Page;
import com.openelements.data.runtime.api.types.Binary;
import com.openelements.data.runtime.api.types.I18nString;
import com.openelements.data.runtime.data.DataReference;
import com.openelements.recordstore.server.internal.PathResolver;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeAdapterFactoryImpl implements TypeAdapterFactory {

    private final static Logger log = LoggerFactory.getLogger(TypeAdapterFactoryImpl.class);

    private final Language requestedLanguage;

    private final PathResolver pathResolver;

    private final Map<Class<?>, Function<Language, TypeAdapter<?>>> typeAdapterMap;

    public TypeAdapterFactoryImpl(Map<Class<?>, Function<Language, TypeAdapter<?>>> typeAdapterMap,
            Language requestedLanguage,
            PathResolver pathResolver) {
        this.typeAdapterMap = Collections.unmodifiableMap(typeAdapterMap);
        this.requestedLanguage = requestedLanguage;
        this.pathResolver = pathResolver;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (TemporalAccessor.class.isAssignableFrom(type.getRawType())) {
            return (TypeAdapter<T>) new TemporalAccessorTypeAdapter();
        } else if (DataReference.class.isAssignableFrom(type.getRawType())) {
            return (TypeAdapter<T>) new DataReferenceTypeAdapter();
        } else if (Binary.class.isAssignableFrom(type.getRawType())) {
            return (TypeAdapter<T>) new BinaryTypeAdapter(pathResolver);
        } else if (I18nString.class.isAssignableFrom(type.getRawType())) {
            return (TypeAdapter<T>) new I18NTypeAdapter(requestedLanguage);
        } else if (Page.class.isAssignableFrom(type.getRawType())) {
            return (TypeAdapter<T>) new PageTypeAdapter<>(gson, pathResolver);
        }
        for (Entry<Class<?>, Function<Language, TypeAdapter<?>>> entry : typeAdapterMap.entrySet()) {
            if (entry.getKey().isAssignableFrom(type.getRawType())) {
                return (TypeAdapter<T>) entry.getValue().apply(requestedLanguage);
            }
        }
        log.debug("No TypeAdapter found for type: {}", type.getRawType().getName());
        return null;
    }
}
