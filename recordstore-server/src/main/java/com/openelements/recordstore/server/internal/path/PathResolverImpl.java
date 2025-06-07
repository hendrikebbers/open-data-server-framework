package com.openelements.recordstore.server.internal.path;

import com.openelements.data.runtime.data.DataType;
import com.openelements.recordstore.server.internal.PathResolver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathResolverImpl implements PathResolver {

    private final Map<DataType<?>, String> getAllMapping = new HashMap<>();

    public void registerGetAllPath(DataType<?> dataType, String path) {
        getAllMapping.put(dataType, path);
    }

    @Override
    public String resolveCountPath(Class<? extends Record> dataType) {
        return "";
    }

    @Override
    public String resolveGetAllPath(Class<? extends Record> dataType) {
        return getAllMapping.entrySet().stream()
                .filter(entry -> entry.getKey().dataClass().equals(dataType))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("No path registered for data type: " + dataType.getName()));
    }

    @Override
    public String resolveGetAllWithPaginationPath(Class<? extends Record> dataType) {
        return "";
    }

    public List<PathEntity> getAllPaths() {
        return getAllMapping.entrySet().stream()
                .map(entry -> new PathEntity(entry.getKey().name(), "GET_ALL", entry.getValue()))
                .toList();
    }
}
