package com.openelements.data.api;

import com.openelements.data.api.context.DataContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

@FunctionalInterface
public interface DataSourceProvider {

    Set<? extends Record> getDataProvider(DataContext dataContext);

    static Set<DataSourceProvider> getInstances() {
        ServiceLoader<DataSourceProvider> serviceLoader = ServiceLoader.load(DataSourceProvider.class);
        Set<DataSourceProvider> instances = new HashSet<>();
        for (DataSourceProvider provider : serviceLoader) {
            instances.add(provider);
        }
        return Collections.unmodifiableSet(instances);
    }
}
