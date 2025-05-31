package com.openelements.data.runtime.sql;

import com.openelements.data.runtime.DataContext;
import com.openelements.data.runtime.KeyValueStore;
import com.openelements.data.runtime.Page;
import com.openelements.data.runtime.data.DataRepository;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.types.DataAttributeDefinition;
import com.openelements.data.runtime.types.DataDefinition;
import com.openelements.data.runtime.types.DataUpdate;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class SqlDataContext implements DataContext {

    private final SqlConnection connection;

    private final ConcurrentMap<Class<? extends Record>, DataRepository<?>> repositories = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executor;

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public SqlDataContext(ScheduledExecutorService executor, SqlConnection connection) {
        this.executor = executor;
        this.connection = connection;
    }

    public void initialize() throws SQLException {
        if (initialized.get()) {
            throw new IllegalStateException("Already initialized");
        }
        initialized.set(true);
    }

    @Override
    public Optional<ZonedDateTime> getLastUpdateTime(Class<? extends Record> dataType) {
        return DataUpdate.findLastUpdateTime(DataType.of(dataType).name(), connection);
    }

    @Override
    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    @Override
    public <T extends Record> List<T> getAll(Class<T> dataType) {
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

    @Override
    public <T extends Record> Page<T> getAll(Class<T> dataType, int pageSize) {
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
    public <T extends Record> void store(Class<T> dataType, List<T> data) {
        DataType<T> dataTypeInstance = DataType.of(dataType);
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

    @Override
    public KeyValueStore getKeyValueStore(String name) {
        return new SqlKeyValueStore(name, connection);
    }

    private <E extends Record> Optional<DataRepository<E>> getRepository(Class<E> dataType) {
        return Optional.ofNullable((DataRepository<E>) repositories.get(dataType));
    }

    public <E extends Record> void addDataType(DataType<E> dataType) throws SQLException {
        if (initialized.get()) {
            throw new IllegalStateException("Cannot add data type after initialization");
        }
        repositories.put(dataType.dataClass(), DataRepository.of(dataType, connection));

        final DataDefinition dataDefinition = DataDefinition.of(dataType);
        DataRepository.of(DataDefinition.class, connection).store(dataDefinition);

        final List<DataAttributeDefinition> attributeDefinitions = DataAttributeDefinition.of(dataType);
        DataRepository.of(DataAttributeDefinition.class, connection).store(attributeDefinitions);
    }
}
