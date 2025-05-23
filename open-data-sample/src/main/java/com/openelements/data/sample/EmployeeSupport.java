package com.openelements.data.sample;

import com.openelements.data.api.DataSource;
import com.openelements.data.api.DataTypeProvider;
import com.openelements.data.api.context.DataContext;
import java.util.List;
import java.util.Set;

public class EmployeeSupport implements DataTypeProvider, DataSource {

    @Override
    public Set<Class<? extends Record>> getDataTypes() {
        return Set.of(Employee.class);
    }


    @Override
    public void install(DataContext dataContext) {
        final Runnable runnable = () -> {
            Employee employee = new Employee("John", "Doe", "john@doe.com");
            dataContext.provide(Employee.class, List.of(employee));
        };
        dataContext.getExecutor().scheduleAtFixedRate(runnable, 0, 10, java.util.concurrent.TimeUnit.SECONDS);
    }
}
