package com.openelements.data.runtime.sql.api;

import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.sql.implementation.TableRepository;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public class CreateTableSupport {

    private CreateTableSupport() {
    }

    public static boolean isVirtualTable(@NonNull final Class<? extends Record> recordClass) {
        Objects.requireNonNull(recordClass, "recordClass must not be null");
        return DataType.of(recordClass).virtual();
    }

    @NonNull
    public static <E extends Record> String createCreateTableStatement(@NonNull final Class<E> recordClass,
            @NonNull final SqlConnection connection) {
        Objects.requireNonNull(recordClass, "recordClass must not be null");
        Objects.requireNonNull(connection, "connection must not be null");
        final DataType<E> dataType = DataType.of(recordClass);
        final SqlDataTable sqlDataTable = TableRepository.createTable(dataType, connection);
        return connection.getSqlStatementFactory().createTableCreateStatement(sqlDataTable, false).getStatement();
    }

    @NonNull
    public static <E extends Record> String createUniqueIndexStatement(@NonNull final Class<E> recordClass,
            @NonNull final SqlConnection connection) {
        Objects.requireNonNull(recordClass, "recordClass must not be null");
        Objects.requireNonNull(connection, "connection must not be null");
        final DataType<E> dataType = DataType.of(recordClass);
        final SqlDataTable sqlDataTable = TableRepository.createTable(dataType, connection);
        return connection.getSqlStatementFactory().createUniqueIndexStatement(sqlDataTable, false).getStatement();
    }
}
