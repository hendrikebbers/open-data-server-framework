package com.openelements.data.api;

import java.util.Collections;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

@FunctionalInterface
public interface DataTypeProvider {

    Set<Class<? extends Record>> getDataTypes();

    static Set<DataTypeProvider> getInstances() {
        ServiceLoader<DataTypeProvider> serviceLoader = ServiceLoader.load(DataTypeProvider.class);
        Set<DataTypeProvider> instances = new HashSet<>();
        for (DataTypeProvider provider : serviceLoader) {
            instances.add(provider);
        }
        return Collections.unmodifiableSet(instances);
    }
}
