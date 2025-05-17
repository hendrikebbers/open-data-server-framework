package com.openelements.data.api;

import com.openelements.data.api.context.DataContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

@FunctionalInterface
public interface DataTypesProvider {

    Set<Class<? extends Record>> getDataTypes(DataContext dataContext);

    static Set<DataTypesProvider> getInstances() {
        ServiceLoader<DataTypesProvider> serviceLoader = ServiceLoader.load(DataTypesProvider.class);
        Set<DataTypesProvider> instances = new HashSet<>();
        for (DataTypesProvider provider : serviceLoader) {
            instances.add(provider);
        }
        return Collections.unmodifiableSet(instances);
    }
}
