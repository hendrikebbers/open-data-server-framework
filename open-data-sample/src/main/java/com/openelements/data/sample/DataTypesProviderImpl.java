package com.openelements.data.sample;

import com.openelements.data.api.DataTypesProvider;
import com.openelements.data.api.context.DataContext;
import java.util.Set;

public class DataTypesProviderImpl implements DataTypesProvider {

    @Override
    public Set<Class<? extends Record>> getDataTypes(DataContext dataContext) {
        return Set.of(Employee.class);
    }
}
