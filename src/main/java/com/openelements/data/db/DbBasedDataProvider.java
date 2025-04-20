package com.openelements.data.db;

import java.util.List;

public class DbBasedDataProvider<E extends AbstractEntity> {

    private final Class<E> entityClass;

    private final ReadOnlyRepository repository;

    public DbBasedDataProvider(Class<E> entityClass, ReadOnlyRepository repository) {
        this.entityClass = entityClass;
        this.repository = repository;
    }

    public List<E> getPage(int page, int pageSize) {
        return repository.getPage(entityClass, page, pageSize);
    }

    public List<E> getAll() {
        return repository.getAll(entityClass);
    }

    public long getCount() {
        return repository.getCount(entityClass);
    }
}
