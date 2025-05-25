package com.openelements.data.runtime.sql.support;

import com.openelements.data.runtime.sql.SqlConnection;
import java.util.Collections;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface DataAttributeTypeSupport<T, U> {

    String getUniqueName();

    Class<T> getJavaType();

    String getSqlType();

    T convertValueFromSqlResult(U sqlValue, SqlConnection connection);

    U convertValueForSqlPersit(@Nullable T newValue, @Nullable U currentValue,
            @NonNull SqlConnection connection);

    static Set<DataAttributeTypeSupport<?, ?>> getInstances() {
        ServiceLoader<DataAttributeTypeSupport> loader = ServiceLoader.load(DataAttributeTypeSupport.class);
        Set<DataAttributeTypeSupport<?, ?>> instances = new java.util.HashSet<>();
        for (DataAttributeTypeSupport<?, ?> instance : loader) {
            instances.add(instance);
        }
        return Collections.unmodifiableSet(instances);
    }

    static <T, U> Optional<DataAttributeTypeSupport<T, U>> forType(Class<T> type) {
        return getInstances().stream()
                .filter(support -> support.getJavaType().isAssignableFrom(type))
                .map(support -> (DataAttributeTypeSupport<T, U>) support)
                .findFirst();
    }
}
