package com.openelements.data.runtime;

import java.util.Collections;
import java.util.ServiceLoader;
import java.util.Set;

public interface DataAttributeTypeSupport<T, U> {

    String getUniqueName();
    
    Class<T> getJavaType();

    SqlDataType<U> getSqlDataType();

    T convertValueFromSqlResult(U sqlValue, QueryContext queryContext);

    default String convertValueForSqlStatement(T value, PersistenceContext persistenceContext) {
        if (value == null) {
            return "NULL";
        }
        return value.toString();
    }

    static Set<DataAttributeTypeSupport<?, ?>> getInstances() {
        ServiceLoader<DataAttributeTypeSupport> loader = ServiceLoader.load(DataAttributeTypeSupport.class);
        Set<DataAttributeTypeSupport<?, ?>> instances = new java.util.HashSet<>();
        for (DataAttributeTypeSupport<?, ?> instance : loader) {
            instances.add(instance);
        }
        return Collections.unmodifiableSet(instances);
    }
}
