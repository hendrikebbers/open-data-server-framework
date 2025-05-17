package com.openelements.data.api.translation;

import com.openelements.data.api.data.Attribute;
import com.openelements.data.api.data.Data;
import java.lang.reflect.RecordComponent;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public class TranslationKeyFactory {

    public static String getKeyForDataTypeName(@NonNull final Class<? extends Record> dataType) {
        return getKeyForDataType(dataType) + ".name";
    }

    public static String getKeyForDataTypeDescription(@NonNull final Class<? extends Record> dataType) {
        return getKeyForDataType(dataType) + ".description";
    }

    public static String getKeyForAttributeName(@NonNull final Class<? extends Record> dataType,
            RecordComponent attribute) {
        return getKeyForDataType(dataType) + ".name";
    }

    public static String getKeyForAttributeDescription(@NonNull final Class<? extends Record> dataType,
            RecordComponent attribute) {
        return getKeyForDataType(dataType) + ".description";
    }

    private static String getKeyForAttribute(@NonNull final Class<? extends Record> dataType,
            RecordComponent attribute) {
        String keyForDataType = getKeyForDataType(dataType);
        if (attribute.isAnnotationPresent(Attribute.class)) {
            final Attribute data = attribute.getAnnotation(Attribute.class);
            if (data.name() != null && !data.name().isBlank()) {
                return keyForDataType + "." + data.name();
            }
        }
        return keyForDataType + "." + attribute.getName();
    }

    private static String getKeyForDataType(@NonNull final Class<? extends Record> dataType) {
        Objects.requireNonNull("dataType", "Data type must not be null");
        if (dataType.isAnnotationPresent(Data.class)) {
            final Data data = dataType.getAnnotation(Data.class);
            if (data.name() != null && !data.name().isBlank()) {
                return data.name();
            }
        }
        return dataType.getName();
    }
}
