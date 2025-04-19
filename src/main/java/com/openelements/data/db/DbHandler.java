package com.openelements.data.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.function.Function;
import java.util.function.Supplier;

public class DbHandler {


    private final EntityManagerFactory entityManagerFactory;

    public DbHandler(final String persistenceUnitName) {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    public <E extends AbstractEntity> DbBasedDataProvider<E> createDataProvider(Class<E> entityClass) {
        return new DbBasedDataProvider<>(entityClass, new Repository(this));
    }

    public Repository createRepository() {
        return new Repository(this);
    }

    public EntityManager createEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public void closeEntityManager(EntityManager entityManager) {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }

    public void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    public <E> E runInTransaction(Supplier<E> command) {
        return runInTransaction(entityManager -> {
            E result = command.get();
            return result;
        });
    }

    public <E> E runInTransaction(Function<EntityManager, E> command) {
        final EntityManager entityManager = createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            E result = command.apply(entityManager);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            closeEntityManager(entityManager);
        }
    }

}
