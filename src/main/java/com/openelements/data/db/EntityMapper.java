package com.openelements.data.db;

public interface EntityMapper<E extends AbstractEntity> {
    E updateEntity(E updated, E toUpdate);
}
