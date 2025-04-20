package com.openelements.data.db;

import java.util.Arrays;
import java.util.Objects;

public interface EntityMapper<E extends AbstractEntity> {
    E updateEntity(E updated, E toUpdate);

    static <E extends AbstractEntity> EntityMapper<E> createDefaultMapper() {
        return (updated, toUpdate) -> {
            Objects.requireNonNull(updated, "updated must not be null");
            Objects.requireNonNull(toUpdate, "toUpdate must not be null");
            Class<E> clazz = (Class<E>) updated.getClass();
            Arrays.stream(clazz.getDeclaredFields())
                    .forEach(field -> {
                        field.setAccessible(true);
                        try {
                            Object value = field.get(updated);
                            field.set(toUpdate, value);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Failed to access field: " + field.getName(), e);
                        }
                    });
            return toUpdate;
        };
    }

}
