package com.openelements.data.runtime.sql;

import java.util.Collections;
import java.util.ServiceLoader;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface DataAttributeTypeSupport<T, U> {

    String getUniqueName();

    Class<T> getJavaType();

    SqlDataType<U> getSqlDataType();

    T convertValueFromSqlResult(U sqlValue, QueryContext queryContext);

    U convertValueForSqlPersit(@Nullable T newValue, @Nullable U currentValue,
            @NonNull PersistenceContext persistenceContext);

    static Set<DataAttributeTypeSupport<?, ?>> getInstances() {
        ServiceLoader<DataAttributeTypeSupport> loader = ServiceLoader.load(DataAttributeTypeSupport.class);
        Set<DataAttributeTypeSupport<?, ?>> instances = new java.util.HashSet<>();
        for (DataAttributeTypeSupport<?, ?> instance : loader) {
            instances.add(instance);
        }
        return Collections.unmodifiableSet(instances);
    }
}
