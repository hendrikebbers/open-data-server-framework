package com.openelements.recordstore.server.internal;

import org.jspecify.annotations.NonNull;

public interface PathResolver {

    String resolveCountPath(Class<? extends Record> dataType);

    String resolveGetAllPath(Class<? extends Record> dataType);

    String resolveGetAllWithPaginationPath(Class<? extends Record> dataType, int pageNumber, int pageSize);

    String resolveGetAllWithPaginationPathBase(Class<? extends Record> dataType);

    String resolveGetCountPath(@NonNull Class<?> aClass);
}
