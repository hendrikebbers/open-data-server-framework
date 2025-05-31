package com.openelements.data.runtime.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

public record DataType<E extends Record>(String name, boolean api, boolean publiclyAvailable, boolean virtual,
                                         Class<E> dataClass,
                                         List<DataAttribute<E, ?>> attributes) {

    public E createInstance(List<Object> constructorParams)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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

    public static <E extends Record> DataType<E> of(Class<E> recordClass) {
        return DataLoader.load(recordClass);
    }

    public <D> Optional<DataAttribute<E, D>> getAttribute(String name) {
        return attributes.stream()
                .filter(attribute -> attribute.name().equals(name))
                .map(attribute -> (DataAttribute<E, D>) attribute)
                .findFirst();
    }
}
