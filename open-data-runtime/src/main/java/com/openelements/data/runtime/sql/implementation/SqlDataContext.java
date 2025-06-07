package com.openelements.data.runtime.sql.implementation;

import com.openelements.data.runtime.api.DataContext;
import com.openelements.data.runtime.api.DataTypeProvider;
import com.openelements.data.runtime.api.KeyValueStore;
import com.openelements.data.runtime.api.Page;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.integration.DataRepository;
import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.types.DataAttributeDefinition;
import com.openelements.data.runtime.types.DataDefinition;
import com.openelements.data.runtime.types.DataReferenceEntry;
import com.openelements.data.runtime.types.DataUpdate;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlDataContext implements DataContext {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final SqlConnection connection;

    private final ConcurrentMap<Class<? extends Record>, DataRepository<?>> repositories = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executor;

    public SqlDataContext(@NonNull final ScheduledExecutorService executor,
            @NonNull final SqlConnection connection) {
        this.executor = Objects.requireNonNull(executor, "executor must not be null");
        this.connection = Objects.requireNonNull(connection, "connection must not be null");
        DataTypeProvider.getInstances().stream()
                .flatMap(provider -> provider.getDataTypes().stream())
                .forEach(dataType -> {
                    try {
                        final DataType<?> dataTypeInstance = DataType.of(dataType);
                        final DataRepository<?> repository = DataRepository.of(dataType, connection);
                        repositories.put(dataType, repository);
                        final DataDefinition dataDefinition = DataDefinition.of(dataTypeInstance);
                        DataRepository.of(DataDefinition.class, connection).store(dataDefinition);
                        final List<DataAttributeDefinition> attributeDefinitions = DataAttributeDefinition.of(
                                dataTypeInstance);
                        DataRepository.of(DataAttributeDefinition.class, connection).store(attributeDefinitions);
                        final List<DataReferenceEntry> attributeReferences = DataReferenceEntry.of(dataTypeInstance);
                        DataRepository.of(DataReferenceEntry.class, connection).store(attributeReferences);
                    } catch (Exception e) {
                        throw new RuntimeException("Error initializing record store for datatype " + dataType, e);
                    }
                });
    }

    @NonNull
    @Override
    public Optional<ZonedDateTime> getLastUpdateTime(@NonNull final Class<? extends Record> dataType) {
        Objects.requireNonNull(dataType, "dataType must not be null");
        return DataUpdate.findLastUpdateTime(DataType.of(dataType).name(), connection);
    }

    @NonNull
    @Override
    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    @NonNull
    @Override
    public <T extends Record> List<T> getAll(@NonNull final Class<T> dataType) {
        Objects.requireNonNull(dataType, "dataType must not be null");
        final DataRepository<T> dataRepository = (DataRepository<T>) repositories.get(dataType);
        if (dataRepository == null) {
            throw new IllegalArgumentException("No data repository found for data type: " + dataType);
        }
        try {
            return dataRepository.getAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    @Override
    public <T extends Record> Page<T> getAll(@NonNull final Class<T> dataType, final int pageSize) {
        Objects.requireNonNull(dataType, "dataType must not be null");
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero");
        }
        final DataRepository<T> dataRepository = (DataRepository<T>) repositories.get(dataType);
        if (dataRepository == null) {
            throw new IllegalArgumentException("No data repository found for data type: " + dataType);
        }
        try {
            return dataRepository.getPage(0, pageSize);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends Record> void store(@NonNull final Class<T> dataType, @NonNull final List<T> data) {
        Objects.requireNonNull(dataType, "dataType must not be null");
        Objects.requireNonNull(data, "data must not be null");
        final DataType<T> dataTypeInstance = DataType.of(dataType);
        if (dataTypeInstance.api()) {
            throw new IllegalArgumentException("Cannot store data for API data type: " + dataType);
        }
        try {
            final DataRepository<T> repository = getRepository(dataType);
            repository.store(data);
        } catch (SQLException e) {
            throw new RuntimeException("Error in storing data", e);
        }
        try {
            DataUpdate.getDataRepository(connection)
                    .store(new DataUpdate(DataType.of(dataType).name(), ZonedDateTime.now(), data.size()));
        } catch (SQLException e) {
            throw new RuntimeException("Error in storing data update", e);
        }
    }

    @NonNull
    @Override
    public KeyValueStore getKeyValueStore(@NonNull final String name) {
        Objects.requireNonNull(name, "name must not be null");
        return new SqlKeyValueStore(name, connection);
    }

    @NonNull
    private synchronized <E extends Record> DataRepository<E> getRepository(@NonNull final Class<E> dataType)
            throws SQLException {
        Objects.requireNonNull(dataType, "dataType must not be null");
        final DataRepository<?> dataRepository = repositories.get(dataType);
        if (dataRepository == null) {
            throw new IllegalArgumentException("No data repository found for data type: " + dataType);
        }
        return (DataRepository<E>) dataRepository;
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(long initialDelay, long delay, TimeUnit unit, UpdateTask task) {
        return getExecutor().scheduleWithFixedDelay(() -> {
            try {
                task.run(this);
            } catch (Exception e) {
                log.error("Error in scheduled task", e);
            }
        }, initialDelay, delay, unit);
    }
}
