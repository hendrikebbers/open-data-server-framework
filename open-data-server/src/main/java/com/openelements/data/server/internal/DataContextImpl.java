package com.openelements.data.server.internal;

import com.openelements.data.api.context.DataContext;
import com.openelements.data.api.context.Page;
import com.openelements.data.runtime.sql.DataRepository;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class DataContextImpl implements DataContext {

    private final static DataContextImpl INSTANCE = new DataContextImpl();

    private final ConcurrentMap<Class<? extends Record>, ZonedDateTime> updateTimes = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors());

    private final ConcurrentMap<Class<? extends Record>, DataRepository<?>> repositories = new ConcurrentHashMap<>();

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
    public <T extends Record> void provide(Class<T> dataType, List<T> data) {
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
        repositories.put(dataType, repository);
    }

    public static DataContextImpl getInstance() {
        return INSTANCE;
    }

}
