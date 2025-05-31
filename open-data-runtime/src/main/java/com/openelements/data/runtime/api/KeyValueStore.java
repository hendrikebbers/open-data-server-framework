package com.openelements.data.runtime.api;

import java.util.Map;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface KeyValueStore {

    void store(@NonNull String key, @Nullable String value) throws Exception;

    @NonNull
    Map<String, String> getAll() throws Exception;

    @NonNull
    Optional<String> get(@NonNull String key) throws Exception;

    void remove(@NonNull String key) throws Exception;
}
