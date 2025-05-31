package com.openelements.data.runtime.data;

import com.openelements.data.runtime.data.impl.DataLoader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.jspecify.annotations.NonNull;

public record DataType<E extends Record>(@NonNull String name, boolean api, boolean publiclyAvailable, boolean virtual,
                                         @NonNull Class<E> dataClass,
                                         @NonNull List<DataAttribute<E, ?>> attributes) {

    public DataType(@NonNull String name, boolean api, boolean publiclyAvailable, boolean virtual,
            @NonNull Class<E> dataClass, @NonNull List<DataAttribute<E, ?>> attributes) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.api = api;
        this.publiclyAvailable = publiclyAvailable;
        this.virtual = virtual;
        this.dataClass = Objects.requireNonNull(dataClass, "dataClass must not be null");
        Objects.requireNonNull(attributes, "attributes must not be null");
        this.attributes = Collections.unmodifiableList(attributes);
    }

    @NonNull
    public E createInstance(@NonNull final List<Object> constructorParams)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Objects.requireNonNull(constructorParams, "constructorParams must not be null");
        final List<Class<?>> list = attributes.stream()
                .map(attribute -> attribute.type())
                .map(type -> {
                    if (type instanceof Class<?> clazz) {
                        return clazz;
                    } else if (type instanceof ParameterizedType parameterizedType) {
                        return (Class<?>) parameterizedType.getRawType();
                    } else {
                        throw new IllegalArgumentException("Unsupported type: " + type);
                    }
                })
                .toList();
        final Constructor<E> constructor = dataClass.getConstructor(list.toArray(new Class[0]));
        return constructor.newInstance(constructorParams.toArray());
    }

    @NonNull
    public static <E extends Record> DataType<E> of(@NonNull final Class<E> recordClass) {
        return DataLoader.load(recordClass);
    }

    @NonNull
    public <D> Optional<DataAttribute<E, D>> getAttribute(@NonNull final String name) {
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Attribute name must not be blank");
        }
        return attributes.stream()
                .filter(attribute -> attribute.name().equals(name))
                .map(attribute -> (DataAttribute<E, D>) attribute)
                .findFirst();
    }

    @NonNull
    public static Set<DataType<?>> loadData() {
        return DataLoader.loadData();
    }
}
