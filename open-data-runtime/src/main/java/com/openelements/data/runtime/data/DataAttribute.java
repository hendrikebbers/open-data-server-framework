package com.openelements.data.runtime.data;

import com.openelements.data.runtime.api.Attribute;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record DataAttribute<E extends Record, D>(String name, int oder, boolean required,
                                                 boolean partOfIdentifier, Type type,
                                                 Set<DataAttributeReference> references) {

    @NonNull
    public static <E extends Record, D> D getFor(@NonNull E data, @NonNull DataType<E> dataType, @NonNull String name) {
        Objects.requireNonNull(data, "data must not be null");
        Objects.requireNonNull(dataType, "dataType must not be null");
        Objects.requireNonNull(name, "name must not be null");
        return (D) dataType.getAttribute(name)
                .orElseThrow(() -> new IllegalArgumentException("No attribute found with name: " + name))
                .getFor(data);
    }

    @Nullable
    public static <E extends Record, D> D getFor(@NonNull final E data, @NonNull final DataAttribute<E, D> attribute) {
        Objects.requireNonNull(data, "data must not be null");
        Objects.requireNonNull(attribute, "attribute must not be null");
        return attribute.getFor(data);
    }

    @Nullable
    public <E extends Record> D getFor(@NonNull final E data) {
        Objects.requireNonNull(data, "data must not be null");
        final RecordComponent recordComponent = Arrays.asList(data.getClass().getRecordComponents()).stream()
                .filter(component -> {
                    if (component.isAnnotationPresent(Attribute.class)) {
                        final Attribute attribute = component.getAnnotation(Attribute.class);
                        String nameByAnnotation = attribute.name();
                        if (nameByAnnotation != null && !nameByAnnotation.isBlank()) {
                            if (nameByAnnotation.equals(name)) {
                                return true;
                            }
                        }
                    }
                    return Objects.equals(name, component.getName());
                }).findAny()
                .orElseThrow(() -> new IllegalArgumentException("No attribute found with name: " + name));
        try {
            return (D) recordComponent.getAccessor().invoke(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get value for attribute: " + name, e);
        }
    }
}
