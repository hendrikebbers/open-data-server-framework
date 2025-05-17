package com.openelements.data.db;

public interface EntityRepositoryFactory {

    <E extends AbstractEntity> EntityRepository<E> createRepository(Class<E> entityClass);
}
