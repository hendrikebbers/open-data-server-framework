package com.openelements.recordstore.server.internal;

public interface PathResolver {

    String resolveCountPath(Class<? extends Record> dataType);

    String resolveGetAllPath(Class<? extends Record> dataType);

    String resolveGetAllWithPaginationPath(Class<? extends Record> dataType);
}
