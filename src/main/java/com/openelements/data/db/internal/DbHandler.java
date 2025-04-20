package com.openelements.data.db.internal;

import com.openelements.data.db.AbstractEntity;
import com.openelements.data.db.EntityMapper;
import com.openelements.data.db.EntityRepository;
import com.openelements.data.db.EntityRepositoryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbHandler implements EntityRepositoryFactory {

    private final static Logger log = LoggerFactory.getLogger(DbHandler.class);

    private final EntityManagerFactory entityManagerFactory;

    public DbHandler(final String persistenceUnitName) {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    @Override
    public <E extends AbstractEntity> EntityRepository<E> createRepository(Class<E> entityClass) {
        return new EntityRepository<>(entityClass, this);
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

    private <E extends AbstractEntity> E storeImpl(EntityManager entityManager, E entity,
            EntityMapper<E> entityMapper) {
        entity.updateUUID();
        if (entity.getUuid() == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        final Class<E> entityClass = (Class<E>) entity.getClass();
        final Optional<E> entityByUuid = findEntityByUuid(entityClass, entity.getUuid());
        if (entityByUuid.isPresent()) {
            log.info("Entity with UUID: {} already exists, updating it.", entity.getUuid());
            final E updatedEntity = entityMapper.updateEntity(entity, entityByUuid.get());
            return entityManager.merge(updatedEntity);
        } else {
            log.info("Entity with UUID: {} does not exist, creating a new one.", entity.getUuid());
            entityManager.persist(entity);
            return entity;
        }
    }

    public <E extends AbstractEntity> Set<E> store(Set<E> entities, EntityMapper<E> entityMapper) {
        return runInTransaction(entityManager -> {
            final Set<E> result = new HashSet<>();
            for (E entity : entities) {
                final E stored = storeImpl(entityManager, entity, entityMapper);
                result.add(stored);
            }
            return Collections.unmodifiableSet(result);
        });
    }

    public <E extends AbstractEntity> E store(E entity, EntityMapper<E> entityMapper) {
        return runInTransaction(entityManager -> {
            final E stored = storeImpl(entityManager, entity, entityMapper);
            return stored;
        });
    }

    public <E extends AbstractEntity> Optional<E> findEntityByUuid(Class<E> entityClass, String uuid) {
        return runInTransaction(entityManager -> {
            final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            final CriteriaQuery<E> query = cb.createQuery(entityClass);
            final Root<E> root = query.from(entityClass);
            query.select(root).where(cb.equal(root.get("uuid"), uuid));
            try {
                return Optional.of(entityManager.createQuery(query).getSingleResult());
            } catch (final NoResultException e) {
                return Optional.empty();
            }
        });
    }

    public <E extends AbstractEntity> List<E> getPage(Class<E> entityClass, int page, int pageSize) {
        return runInTransaction(entityManager -> {
            final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            final CriteriaQuery<E> cq = cb.createQuery(entityClass);
            final Root<E> rootEntry = cq.from(entityClass);
            final CriteriaQuery<E> all = cq.select(rootEntry);
            final TypedQuery<E> typedQuery = entityManager.createQuery(all);
            typedQuery.setFirstResult(page * pageSize);
            typedQuery.setMaxResults(pageSize);
            return typedQuery.getResultList();
        });
    }

    public <E extends AbstractEntity> List<E> getAll(Class<E> entityClass) {
        return runInTransaction(entityManager -> {
            final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            final CriteriaQuery<E> cq = cb.createQuery(entityClass);
            final Root<E> rootEntry = cq.from(entityClass);
            final CriteriaQuery<E> all = cq.select(rootEntry);
            final TypedQuery<E> allQuery = entityManager.createQuery(all);
            return allQuery.getResultList();
        });
    }

    public <E extends AbstractEntity> long getCount(Class<E> entityClass) {
        return runInTransaction(entityManager -> {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<E> root = cq.from(entityClass);
            cq.select(cb.count(root));
            return entityManager.createQuery(cq).getSingleResult();
        });
    }
}
