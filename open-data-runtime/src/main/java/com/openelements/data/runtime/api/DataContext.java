package com.openelements.data.runtime.api;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import org.jspecify.annotations.NonNull;

public interface DataContext {

    @NonNull
    Optional<ZonedDateTime> getLastUpdateTime(@NonNull Class<? extends Record> dataType);

    @NonNull
    ScheduledExecutorService getExecutor();

    @NonNull
    <T extends Record> List<T> getAll(@NonNull Class<T> dataType);

    @NonNull
    <T extends Record> Page<T> getAll(@NonNull Class<T> dataType, int pageSize);

    <T extends Record> void store(@NonNull Class<T> dataType, @NonNull List<T> data);

    default <T extends Record> void store(@NonNull Class<T> dataType, @NonNull T data) {
        store(dataType, List.of(data));
    }

    @NonNull
    KeyValueStore getKeyValueStore(@NonNull String name);
}
