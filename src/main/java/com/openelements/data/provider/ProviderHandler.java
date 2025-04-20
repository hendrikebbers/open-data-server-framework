package com.openelements.data.provider;

import com.openelements.data.db.AbstractEntity;
import com.openelements.data.db.EntityMapper;
import com.openelements.data.db.Repository;
import com.openelements.data.provider.db.UpdateEntity;
import com.openelements.data.provider.db.UpdateEntityMapper;
import com.openelements.data.provider.db.UpdateRunEntity;
import com.openelements.data.provider.db.UpdateRunEntityMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ProviderHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProviderHandler.class);

    private final Repository repository;

    private final ScheduledExecutorService executorService;

    private final static ZonedDateTime MIN_TIME = Instant.ofEpochMilli(Long.MIN_VALUE).atZone(ZoneOffset.UTC);

    public ProviderHandler(Repository repository) {
        this.repository = repository;
        this.executorService = Executors.newScheduledThreadPool(12);
    }

    public <E extends AbstractEntity> void add(Class<E> entityClass, EntityUpdatesProvider<E> provider,
            EntityMapper<E> entityMapper, long periodInSeconds) {
        final Runnable runnable = () -> {
            final String uuid = entityClass.getSimpleName();
            final UpdateEntity updateEntity = repository.findEntityByUuid(UpdateEntity.class, uuid)
                    .orElseGet(() -> {
                        final UpdateEntity newUpdateEntity = new UpdateEntity();
                        newUpdateEntity.setType(entityClass.getSimpleName());
                        newUpdateEntity.setLastUpdate(MIN_TIME);
                        return newUpdateEntity;
                    });
            final ZonedDateTime startOfUpdate = ZonedDateTime.now();
            final DataProviderContext context = new DataProviderContext(repository, updateEntity.getLastUpdate(),
                    executorService);
            final Set<E> newEntities = provider.loadUpdatedData(context);
            final Duration duration = Duration.between(startOfUpdate, ZonedDateTime.now());
            log.info("Loaded {} new entities", newEntities.size());
            repository.store(newEntities, entityMapper);
            log.info("Stored {} new entities", newEntities.size());
            updateEntity.setLastUpdate(startOfUpdate);
            repository.store(updateEntity, new UpdateEntityMapper());
            UpdateRunEntity updateRunEntity = new UpdateRunEntity();
            updateRunEntity.setType(entityClass.getSimpleName());
            updateRunEntity.setStartOfUpdate(startOfUpdate);
            updateRunEntity.setDuration(duration);
            updateRunEntity.setNumberOfEntities(newEntities.size());
            repository.store(updateRunEntity, new UpdateRunEntityMapper());
        };
        executorService.scheduleAtFixedRate(runnable, 0, periodInSeconds, java.util.concurrent.TimeUnit.SECONDS);
    }

    public <E extends AbstractEntity> void add(Class<E> entityClass, EntityUpdatesProvider<E> provider,
            EntityMapper<E> entityMapper) {
        add(entityClass, provider, entityMapper, 10);
    }
}
