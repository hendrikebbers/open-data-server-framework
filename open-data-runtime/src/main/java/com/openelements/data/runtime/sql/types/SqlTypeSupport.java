package com.openelements.data.runtime.sql.types;

import com.openelements.data.runtime.sql.SqlConnection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;

public interface SqlTypeSupport<T, U> {

    Class<T> getJavaClass();

    String getNativeSqlType();

    Class<U> getSqlType();

    default U normalizeSqlValue(Object sqlValue) throws SQLException {
        if (sqlValue == null) {
            return null; // Handle null case
        }
        if (!getSqlType().isInstance(sqlValue)) {
            throw new SQLException(
                    "Expected SQL value of type " + getSqlType().getName() + ", but got: " + sqlValue.getClass()
                            .getName());
        }
        return getSqlType().cast(sqlValue);
    }

    Set<String> getSupportedJdbcDrivers();

    default boolean isReferenceType() {
        return false;
    }

    T convertToJavaValue(U sqlValue, SqlConnection connection) throws SQLException;

    U convertToSqlValue(T javaValue, SqlConnection connection) throws SQLException;

    static Set<SqlTypeSupport<?, ?>> getInstances() {
        ServiceLoader<SqlTypeSupport> loader = ServiceLoader.load(SqlTypeSupport.class);
        Set<SqlTypeSupport<?, ?>> instances = new java.util.HashSet<>();
        for (SqlTypeSupport<?, ?> instance : loader) {
            for (SqlTypeSupport<?, ?> existing : instances) {
                if (Objects.equals(existing.getJavaClass(), instance.getJavaClass())) {
                    instance.getSupportedJdbcDrivers().forEach(driver -> {
                        if (existing.getSupportedJdbcDrivers().contains(driver)) {
                            throw new IllegalStateException("Duplicate SqlTypeSupport found for type: "
                                    + instance.getJavaClass().getName() + " with driver: " + driver);
                        }
                    });
                }
            }
            instances.add(instance);
        }
        return Collections.unmodifiableSet(instances);
    }

    default U insertReference(T javaValue, SqlConnection connection) throws SQLException {
        throw new UnsupportedOperationException("Insert reference not supported for type: " + getJavaClass().getName());
    }

    default U updateReference(U currentValue, T javaValue, SqlConnection connection) throws SQLException {
        throw new UnsupportedOperationException("Update reference not supported for type: " + getJavaClass().getName());
    }
}
