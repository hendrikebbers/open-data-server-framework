package com.openelements.data.runtime.sql.tables;

import java.sql.SQLException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface ResultRow {

    <T, U> boolean containsColumn(@NonNull TableColumn<T, U> column);

    @Nullable
    <T> T getJavaValue(@NonNull String columnName) throws SQLException;

    @Nullable
    <T, U> T getJavaValue(@NonNull TableColumn<T, U> column) throws SQLException;
}
