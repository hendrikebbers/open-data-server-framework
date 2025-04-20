package com.openelements.data.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbHandler {

    private final static Logger log = LoggerFactory.getLogger(DbHandler.class);

    private final EntityManagerFactory entityManagerFactory;

    public DbHandler(final String persistenceUnitName) {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    public <E extends AbstractEntity> DbBasedDataProvider<E> createDataProvider(Class<E> entityClass) {
        return new DbBasedDataProvider<>(entityClass, createRepository());
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
            log.error("Transaction failed, rolling back", e);
            if (transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackException) {
                    log.error("Rollback failed", rollbackException);
                    throw rollbackException;
                }
            }
            throw e;
        } finally {
            closeEntityManager(entityManager);
        }
    }

}
