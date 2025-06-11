package com.openelements.data.runtime.sql.h2.impl;

import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.connection.SqlConnectionImpl;
import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.implementation.TableRepository;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import org.jspecify.annotations.NonNull;

public class H2BasedVirtualDataRepository<E extends Record> extends TableRepository<E> {

    private static SqlConnection connection;

    private final static Lock connectionLock = new java.util.concurrent.locks.ReentrantLock();

    private final static List<DataType<?>> initializedDataTypes = new CopyOnWriteArrayList<>();

    private final static Lock initializedDataTypesLock = new java.util.concurrent.locks.ReentrantLock();

    public H2BasedVirtualDataRepository(
            @NonNull DataType<E> dataType) {
        super(dataType, getSqlConnection());
        createTableIfNeeded(dataType);
    }


    private <E extends Record> void createTableIfNeeded(DataType<E> dataType) {
        if (!initializedDataTypes.contains(dataType)) {
            initializedDataTypesLock.lock();
            try {
                if (!initializedDataTypes.contains(dataType)) {
                    final SqlDataTable table = createTable(dataType, getSqlConnection());
                    try {
                        getSqlConnection().runInTransaction(() -> {
                            getSqlConnection().getSqlStatementFactory().createTableCreateStatement(table, true)
                                    .executeUpdate();
                            getSqlConnection().getSqlStatementFactory().createUniqueIndexStatement(table, true)
                                    .executeUpdate();
                        });
                        initializedDataTypes.add(dataType);
                    } catch (SQLException e) {
                        throw new RuntimeException("Failed to create table for data type " + dataType, e);
                    }
                }
            } finally {
                initializedDataTypesLock.unlock();
            }
        }
    }

    private static synchronized SqlConnection getSqlConnection() {
        if (connection == null) {
            connectionLock.lock();
            try {
                if (connection == null) {
                    try {
                        final Connection innerConnection = DriverManager.getConnection("jdbc:h2:mem:", "sa", "");
                        connection = new SqlConnectionImpl(() -> innerConnection, H2Dialect.getInstance());
                    } catch (SQLException e) {
                        throw new RuntimeException("Failed to create H2 connection", e);
                    }
                }
            } finally {
                connectionLock.unlock();
            }
        }
        return connection;
    }
}
