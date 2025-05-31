package com.openelements.data.runtime;

import java.util.Map;
import java.util.Optional;

public interface KeyValueStore {

    void store(String key, String value) throws Exception;

    Map<String, String> getAll() throws Exception;

    Optional<String> get(String key) throws Exception;

    void remove(String key) throws Exception;
}
