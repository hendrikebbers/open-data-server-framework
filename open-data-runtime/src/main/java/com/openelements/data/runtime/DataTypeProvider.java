package com.openelements.data.runtime;

import java.util.Collections;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
import org.jspecify.annotations.NonNull;

@FunctionalInterface
public interface DataTypeProvider {

    @NonNull
    Set<Class<? extends Record>> getDataTypes();

    @NonNull
    static Set<DataTypeProvider> getInstances() {
        ServiceLoader<DataTypeProvider> serviceLoader = ServiceLoader.load(DataTypeProvider.class);
        Set<DataTypeProvider> instances = new HashSet<>();
        for (DataTypeProvider provider : serviceLoader) {
            instances.add(provider);
        }
        return Collections.unmodifiableSet(instances);
    }
}
