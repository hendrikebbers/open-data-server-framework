package com.openelements.data.runtime.data;

import com.openelements.data.api.data.Attribute;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

public record DataAttribute<E extends Record, D>(String name, int oder, boolean required,
                                                 boolean partOfIdentifier, Type type) {

    public static <E extends Record, D> D getFor(E data, DataType<E> dataType, String name) {
        return (D) dataType.getAttribute(name)
                .orElseThrow(() -> new IllegalArgumentException("No attribute found with name: " + name))
                .getFor(data);
    }

    public static <E extends Record, D> D getFor(E data, DataAttribute<E, D> attribute) {
        return attribute.getFor(data);
    }

    public <E extends Record> D getFor(E data) {
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
