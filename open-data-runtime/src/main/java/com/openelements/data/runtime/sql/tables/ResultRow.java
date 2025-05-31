package com.openelements.data.runtime.sql.tables;

import java.sql.SQLException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface ResultRow {

    boolean containsColumn(@NonNull String columnName);

    boolean containsColumn(@NonNull TableColumn<?, ?> column);

    @Nullable
    <T> T getJavaValue(@NonNull String columnName) throws SQLException;

    @Nullable
    <T, U> T getJavaValue(@NonNull TableColumn<T, U> column) throws SQLException;
}
