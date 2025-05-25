package com.openelements.data.runtime.sql.types;

import com.openelements.data.runtime.sql.SqlConnection;
import java.util.Collections;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface SqlTypeSupport<T, U> {

    Class<T> getJavaType();

    String getSqlType();

    T convertValueFromSqlResult(U sqlValue, SqlConnection connection);

    U convertValueForSqlPersit(@Nullable T newValue, @Nullable U currentValue,
            @NonNull SqlConnection connection);

    static Set<SqlTypeSupport<?, ?>> getInstances() {
        ServiceLoader<SqlTypeSupport> loader = ServiceLoader.load(SqlTypeSupport.class);
        Set<SqlTypeSupport<?, ?>> instances = new java.util.HashSet<>();
        for (SqlTypeSupport<?, ?> instance : loader) {
            instances.stream()
                    .filter(support -> support.getJavaType().equals(instance.getJavaType()))
                    .findFirst()
                    .ifPresent(existing -> {
                        throw new IllegalStateException(
                                "Duplicate SqlTypeSupport found for type: "
                                        + instance.getJavaType());
                    });
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
