package com.openelements.data.runtime;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

public record DataType<E extends Record>(String name, boolean publiclyAvailable, Class<E> dataClass,
                                         Set<DataAttribute> attributes) {

    public E createInstance(List<Object> constructorParams)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final List<? extends Class<?>> list = attributes.stream()
                .map(attribute -> attribute.dataTypeSupport().getJavaType())
                .toList();
        final Constructor<E> constructor = dataClass.getConstructor(list.toArray(new Class[0]));
        return constructor.newInstance(constructorParams.toArray());
    }
}
