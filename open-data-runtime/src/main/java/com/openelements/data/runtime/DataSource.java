package com.openelements.data.runtime;

import java.util.Collections;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
import org.jspecify.annotations.NonNull;

@FunctionalInterface
public interface DataSource {

    void install(@NonNull DataContext dataContext);

    @NonNull
    static Set<DataSource> getInstances() {
        ServiceLoader<DataSource> serviceLoader = ServiceLoader.load(DataSource.class);
        Set<DataSource> instances = new HashSet<>();
        for (DataSource provider : serviceLoader) {
            instances.add(provider);
        }
        return Collections.unmodifiableSet(instances);
    }
}
