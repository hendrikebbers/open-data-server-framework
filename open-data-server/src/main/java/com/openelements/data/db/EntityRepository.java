package com.openelements.data.db;

import com.openelements.data.db.internal.DbHandler;
import java.util.List;
import java.util.Optional;

public class EntityRepository<E extends AbstractEntity> {

    private final Class<E> entityClass;

    private final DbHandler dbHandler;

    public EntityRepository(Class<E> entityClass, DbHandler dbHandler) {
        this.entityClass = entityClass;
        this.dbHandler = dbHandler;
    }

    public Optional<E> findEntityByUuid(String uuid) {
        return dbHandler.findEntityByUuid(entityClass, uuid);
    }

    public List<E> getPage(int page, int pageSize) {
        return dbHandler.getPage(entityClass, page, pageSize);
    }

    public List<E> getAll() {
        return dbHandler.getAll(entityClass);
    }

    public long getCount() {
        return dbHandler.getCount(entityClass);
    }
}
