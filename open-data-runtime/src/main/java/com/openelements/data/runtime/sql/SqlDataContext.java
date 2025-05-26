package com.openelements.data.runtime.sql;

import com.openelements.data.api.context.DataContext;
import com.openelements.data.api.context.Page;
import com.openelements.data.runtime.sql.repositories.DataRepository;
import com.openelements.data.runtime.sql.repositories.InternalI18nStringRepository;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class SqlDataContext implements DataContext {

    private final ConcurrentMap<Class<? extends Record>, ZonedDateTime> updateTimes = new ConcurrentHashMap<>();

    private final ConcurrentMap<Class<? extends Record>, DataRepository<?>> repositories = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executor;

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public SqlDataContext(ScheduledExecutorService executor) {
        this.executor = executor;
    }

    public void initialize(SqlConnection connection) throws SQLException {
        if (initialized.get()) {
            throw new IllegalStateException("Already initialized");
        }
        InternalI18nStringRepository i18nStringRepository = new InternalI18nStringRepository(connection);
        i18nStringRepository.createTables(connection);

        repositories.forEach((dataType, repository) -> {
            try {
                repository.createTable();
            } catch (SQLException e) {
                throw new RuntimeException("Error initializing repository for data type: " + dataType, e);
            }
        });
    }

    @Override
    public Optional<ZonedDateTime> getLastUpdateTime(Class<? extends Record> dataType) {
        return Optional.ofNullable(updateTimes.get(dataType));
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
        final DataRepository<T> dataRepository = (DataRepository<T>) repositories.get(dataType);
        if (dataRepository == null) {
            throw new IllegalArgumentException("No data repository found for data type: " + dataType);
        }
        try {
            dataRepository.store(data);
        } catch (SQLException e) {
            throw new RuntimeException("Error in storing data", e);
        }
    }

    public void addRepository(Class<? extends Record> dataType, DataRepository<?> repository) {
        if (initialized.get()) {
            throw new IllegalStateException("Cannot add repository after initialization");
        }
        repositories.put(dataType, repository);
    }
}
