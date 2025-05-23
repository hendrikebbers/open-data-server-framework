package com.openelements.data.api.context;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

public interface DataContext {

    Optional<ZonedDateTime> getLastUpdateTime(Class<? extends Record> dataType);

    ScheduledExecutorService getExecutor();

    <T extends Record> List<T> getAll(Class<T> dataType);

    <T extends Record> Page<T> getAll(Class<T> dataType, int pageSize);

    <T extends Record> void provide(Class<T> dataType, List<T> data);

}
