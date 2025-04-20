package com.openelements.data.provider;

import com.openelements.data.db.AbstractEntity;
import com.openelements.data.db.EntityMapper;
import com.openelements.data.db.internal.DbHandler;
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

    private final DbHandler dbHandler;

    private final ScheduledExecutorService executorService;

    private final static ZonedDateTime MIN_TIME = Instant.ofEpochMilli(Long.MIN_VALUE).atZone(ZoneOffset.UTC);

    public ProviderHandler(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.executorService = Executors.newScheduledThreadPool(12);
    }

    public <E extends AbstractEntity> void add(Class<E> entityClass, EntityUpdatesProvider<E> provider,
            long periodInSeconds) {
        add(entityClass, provider, EntityMapper.createDefaultMapper(), periodInSeconds);
    }

    public <E extends AbstractEntity> void add(Class<E> entityClass, EntityUpdatesProvider<E> provider) {
        add(entityClass, provider, EntityMapper.createDefaultMapper(), 60);
    }

    public <E extends AbstractEntity> void add(Class<E> entityClass, EntityUpdatesProvider<E> provider,
            EntityMapper<E> entityMapper) {
        add(entityClass, provider, entityMapper, 10);
    }

    public <E extends AbstractEntity> void add(Class<E> entityClass, EntityUpdatesProvider<E> provider,
            EntityMapper<E> entityMapper, long periodInSeconds) {
        final Runnable runnable = () -> {
            try {
                final String uuid = entityClass.getSimpleName();
                final UpdateEntity updateEntity = dbHandler.findEntityByUuid(UpdateEntity.class, uuid)
                        .orElseGet(() -> {
                            final UpdateEntity newUpdateEntity = new UpdateEntity();
                            newUpdateEntity.setType(entityClass.getSimpleName());
                            newUpdateEntity.setLastUpdate(MIN_TIME);
                            return newUpdateEntity;
                        });
                final ZonedDateTime startOfUpdate = ZonedDateTime.now();
                final DataProviderContext context = new DataProviderContext(dbHandler, updateEntity.getLastUpdate(),
                        executorService);
                final Set<E> newEntities = provider.loadUpdatedData(context);
                final Duration duration = Duration.between(startOfUpdate, ZonedDateTime.now());
                log.info("Loaded {} new entities", newEntities.size());
                dbHandler.store(newEntities, entityMapper);
                log.info("Stored {} new entities", newEntities.size());
                updateEntity.setLastUpdate(startOfUpdate);
                dbHandler.store(updateEntity, new UpdateEntityMapper());
                UpdateRunEntity updateRunEntity = new UpdateRunEntity();
                updateRunEntity.setType(entityClass.getSimpleName());
                updateRunEntity.setStartOfUpdate(startOfUpdate);
                updateRunEntity.setDuration(duration);
                updateRunEntity.setNumberOfEntities(newEntities.size());
                dbHandler.store(updateRunEntity, new UpdateRunEntityMapper());
            } catch (Exception e) {
                log.error("Error during update for type '" + entityClass.getSimpleName() + "'", e);
            }
        };
        executorService.scheduleAtFixedRate(runnable, 0, periodInSeconds, java.util.concurrent.TimeUnit.SECONDS);
    }

}
