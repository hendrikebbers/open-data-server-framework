package com.openelements.data.api;

import com.openelements.data.api.context.DataContext;
import com.openelements.data.api.translation.Translation;
import java.util.Collections;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

@FunctionalInterface
public interface TranslationProvider {

    Set<Translation> getTranslations(DataContext dataContext);

    static Set<TranslationProvider> getInstances() {
        ServiceLoader<TranslationProvider> serviceLoader = ServiceLoader.load(TranslationProvider.class);
        Set<TranslationProvider> instances = new HashSet<>();
        for (TranslationProvider provider : serviceLoader) {
            instances.add(provider);
        }
        return Collections.unmodifiableSet(instances);
    }
}
