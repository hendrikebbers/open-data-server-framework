package com.openelements.data.db;

import java.util.List;
import java.util.Optional;

public interface ReadOnlyRepository {

    <E extends AbstractEntity> Optional<E> findEntityByUuid(Class<E> entityClass, String uuid);

    <E extends AbstractEntity> List<E> getPage(Class<E> entityClass, int page, int pageSize);

    <E extends AbstractEntity> List<E> getAll(Class<E> entityClass);

    <E extends AbstractEntity> long getCount(Class<E> entityClass);
}
