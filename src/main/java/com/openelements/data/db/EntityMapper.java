package com.openelements.data.db;

import java.util.Arrays;
import java.util.Objects;

public interface EntityMapper<E extends AbstractEntity> {
    E updateEntity(E updated, E toUpdate);

    E updateUUUIDs(E entity);

    static <E extends AbstractEntity> EntityMapper<E> createDefaultMapper() {
        return new EntityMapper<E>() {
            @Override
            public E updateEntity(E updated, E toUpdate) {
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
            }

            @Override
            public E updateUUUIDs(E entity) {
                Objects.requireNonNull(entity, "entity must not be null");
                Class<E> clazz = (Class<E>) entity.getClass();
                Arrays.stream(clazz.getDeclaredFields())
                        .forEach(field -> {
                            field.setAccessible(true);
                            try {
                                Object value = field.get(entity);
                                if (value != null && value instanceof AbstractEntity e) {
                                    EntityMapper genericMapper = EntityMapper.createDefaultMapper();
                                    genericMapper.updateUUUIDs(e);
                                }
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException("Failed to access field: " + field.getName(), e);
                            }
                        });
                entity.updateUUID();
                return entity;
            }
        };
    }

}
