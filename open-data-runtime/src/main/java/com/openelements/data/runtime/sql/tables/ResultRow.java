package com.openelements.data.runtime.sql.tables;

import java.sql.SQLException;

public interface ResultRow {

    boolean containsColumn(String columnName);

    boolean containsColumn(TableColumn<?, ?> column);

    <T> T getJavaValue(String columnName) throws SQLException;

    <T, U> T getJavaValue(TableColumn<T, U> column) throws SQLException;
}
