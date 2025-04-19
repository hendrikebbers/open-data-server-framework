package com.openelements.data.provider;

import com.openelements.data.db.AbstractEntity;
import com.openelements.data.db.EntityMapper;
import com.openelements.data.db.Repository;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ProviderHandler {

    private final Repository repository;

    private final ScheduledExecutorService executorService;

    private final static ZonedDateTime MIN_TIME = Instant.ofEpochMilli(Long.MIN_VALUE).atZone(ZoneOffset.UTC);

    public ProviderHandler(Repository repository) {
        this.repository = repository;
        this.executorService = Executors.newScheduledThreadPool(4);
    }

    public <E extends AbstractEntity> void add(Class<E> entityClass, DataProvider<E> provider,
            EntityMapper<E> entityMapper) {
        final Runnable runnable = () -> {
            final ZonedDateTime startOfUpdate = ZonedDateTime.now();
            final String uuid = entityClass.getName();
            final UpdateEntity updateEntity = repository.findEntityByUuid(UpdateEntity.class, uuid)
                    .orElseGet(() -> {
                        final UpdateEntity newUpdateEntity = new UpdateEntity();
                        newUpdateEntity.setUuid(uuid);
                        newUpdateEntity.setType(entityClass.getSimpleName());
                        newUpdateEntity.setLastUpdate(MIN_TIME);
                        return newUpdateEntity;
                    });
            final Set<E> newEntities = provider.loadUpdateData(updateEntity.getLastUpdate());
            repository.store(newEntities, entityMapper);
            updateEntity.setLastUpdate(startOfUpdate);
            repository.store(updateEntity, new UpdateEntityMapper());
        };
        executorService.scheduleAtFixedRate(runnable, 0, 10, java.util.concurrent.TimeUnit.SECONDS);
    }
}
