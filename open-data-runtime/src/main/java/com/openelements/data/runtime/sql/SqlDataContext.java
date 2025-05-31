package com.openelements.data.runtime.sql;

import com.openelements.data.runtime.DataContext;
import com.openelements.data.runtime.KeyValueStore;
import com.openelements.data.runtime.Page;
import com.openelements.data.runtime.data.DataRepository;
import com.openelements.data.runtime.data.DataType;
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
import java.util.concurrent.atomic.AtomicBoolean;
import org.jspecify.annotations.NonNull;

public class SqlDataContext implements DataContext {

    private final SqlConnection connection;

    private final ConcurrentMap<Class<? extends Record>, DataRepository<?>> repositories = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executor;

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public SqlDataContext(@NonNull final ScheduledExecutorService executor, @NonNull final SqlConnection connection) {
        this.executor = Objects.requireNonNull(executor, "executor must not be null");
        this.connection = Objects.requireNonNull(connection, "connection must not be null");
    }

    public void initialize() throws SQLException {
        if (initialized.get()) {
            throw new IllegalStateException("Already initialized");
        }
        initialized.set(true);
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
            final DataRepository<T> repository = getRepository(dataType).orElseThrow(
                    () -> new IllegalStateException("No data repository found for data type: " + dataType));
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
    private <E extends Record> Optional<DataRepository<E>> getRepository(@NonNull final Class<E> dataType) {
        Objects.requireNonNull(dataType, "dataType must not be null");
        return Optional.ofNullable((DataRepository<E>) repositories.get(dataType));
    }

    public <E extends Record> void addDataType(@NonNull final DataType<E> dataType) throws SQLException {
        Objects.requireNonNull(dataType, "dataType must not be null");
        if (initialized.get()) {
            throw new IllegalStateException("Cannot add data type after initialization");
        }
        repositories.put(dataType.dataClass(), DataRepository.of(dataType, connection));

        final DataDefinition dataDefinition = DataDefinition.of(dataType);
        DataRepository.of(DataDefinition.class, connection).store(dataDefinition);

        final List<DataAttributeDefinition> attributeDefinitions = DataAttributeDefinition.of(dataType);
        DataRepository.of(DataAttributeDefinition.class, connection).store(attributeDefinitions);

        final List<DataReferenceEntry> attributeReferences = DataReferenceEntry.of(dataType);
        DataRepository.of(DataReferenceEntry.class, connection).store(attributeReferences);
    }
}
