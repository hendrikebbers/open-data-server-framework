package com.openelements.data.db;

import com.openelements.data.data.DataProvider;
import java.util.List;

public class DbBasedDataProvider<E extends AbstractEntity> implements DataProvider<E> {

    private final Class<E> entityClass;

    private final Repository repository;

    public DbBasedDataProvider(Class<E> entityClass, Repository repository) {
        this.entityClass = entityClass;
        this.repository = repository;
    }

    @Override
    public List<E> getPage(int page, int pageSize) {
        return repository.getPage(entityClass, page, pageSize);
    }

    @Override
    public List<E> getAll() {
        return repository.getAll(entityClass);
    }

    @Override
    public long getCount() {
        return repository.getCount(entityClass);
    }
}
