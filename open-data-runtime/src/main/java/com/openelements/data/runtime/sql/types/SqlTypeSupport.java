package com.openelements.data.runtime.sql.types;

import com.openelements.data.runtime.sql.SqlConnection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface SqlTypeSupport<T, U> {

    Class<T> getJavaType();

    String getSqlType();

    Set<String> getSupportedJdbcDrivers();

    T convertValueFromSqlResult(U sqlValue, SqlConnection connection);

    U convertValueForSqlPersit(@Nullable T newValue, @Nullable U currentValue,
            @NonNull SqlConnection connection);

    static Set<SqlTypeSupport<?, ?>> getInstances() {
        ServiceLoader<SqlTypeSupport> loader = ServiceLoader.load(SqlTypeSupport.class);
        Set<SqlTypeSupport<?, ?>> instances = new java.util.HashSet<>();
        for (SqlTypeSupport<?, ?> instance : loader) {
            for (SqlTypeSupport<?, ?> existing : instances) {
                if (Objects.equals(existing.getJavaType(), instance.getJavaType())) {
                    instance.getSupportedJdbcDrivers().forEach(driver -> {
                        if (existing.getSupportedJdbcDrivers().contains(driver)) {
                            throw new IllegalStateException("Duplicate SqlTypeSupport found for type: "
                                    + instance.getJavaType().getName() + " with driver: " + driver);
                        }
                    });
                }
            }
            instances.add(instance);
        }
        return Collections.unmodifiableSet(instances);
    }

    static <T, U> Optional<SqlTypeSupport<T, U>> forJavaType(Class<T> type) {
        return getInstances().stream()
                .filter(support -> support.getJavaType().isAssignableFrom(type))
                .map(support -> (SqlTypeSupport<T, U>) support)
                .findFirst();
    }
}
